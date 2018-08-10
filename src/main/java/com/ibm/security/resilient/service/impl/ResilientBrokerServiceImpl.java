package com.ibm.security.resilient.service.impl;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.co3.dto.json.FullIncidentDataDTO;
import com.co3.simpleclient.SimpleClient;
import com.co3.simpleclient.SimpleClient.SimpleHTTPException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.security.resilient.common.RBConstants;
import com.ibm.security.resilient.common.ResilientBrokerUtils;
import com.ibm.security.resilient.common.ResilientUtils;
import com.ibm.security.resilient.common.TransactionLog;
import com.ibm.security.resilient.common.Utils;
import com.ibm.security.resilient.dao.ResilientBrokerDAO;
import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;
import com.ibm.security.resilient.model.QRadar;
import com.ibm.security.resilient.service.QRadarService;
import com.ibm.security.resilient.service.ResilientBrokerService;
import com.ibm.security.resilient.service.ResilientService;

/**
 * Resilient-Broker Service implementation class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1 since August 10th, 2017
 */
@Service
public class ResilientBrokerServiceImpl implements ResilientBrokerService {
	/**
	 * logger instance for ResilientBrokerServiceImpl
	 */
	private Logger logger = LoggerFactory.getLogger(ResilientBrokerServiceImpl.class);
	/**
	 * Resilient-Broker repository/DAO instance 
	 */
	@Autowired
	private ResilientBrokerDAO resilientBrokerDAO;
	/**
	 * Resilient-Broker utility class
	 */
	@Autowired
	private ResilientBrokerUtils resilientBrokerUtils;
	/**
	 * Instance of Resilient Service
	 */
	@Autowired
	private ResilientService resilientService;
	/**
	 * Instance of QRadar Service
	 */
	@Autowired
	private QRadarService qRadarService;
	
	@Autowired
	private ResilientUtils resilientUtils;
		
	private Date d;
	
	@Autowired
	private Utils utils;
		
	private List<TransactionLog> transactionLog;

	private TransactionLog transactionRecord;

	/**
	 * FullIncidentDataDTO type reference
	 */
	private static final TypeReference<FullIncidentDataDTO> FULL_INC_DATA = new TypeReference<FullIncidentDataDTO>() {};

	@Override
	public void createNewIncedents(ResilientBrokerDTO resilientBrokerDTO, PrintWriter out) throws ResilientBrokerException {
		logger.info("Entering into createNewIncedents() function");
		transactionLog = new ArrayList<TransactionLog>();
		transactionRecord = new TransactionLog();
		
		d = new Date();
		long beginTime = System.currentTimeMillis();		
		Collection<Long> offenseIds = resilientBrokerDAO.findAllOffenseIds();
		Long maxOffense ;
		if (null == offenseIds || offenseIds.isEmpty()) {
			maxOffense = 0L;
		} else {
			Iterator<Long> iterator = offenseIds.iterator();
			maxOffense = iterator.next();
		}
		logger.info("Fetching all candidate offences from QRadar. Type-1) New offences Type-2) Previously failed offences");
		JsonArray offenseJsonArray = qRadarService.getOffencesFromQRadar(maxOffense, resilientBrokerDTO);
		int newOffenses = offenseJsonArray.size();
		List<Long> failedOffenses = resilientBrokerDAO.findFailedOffenses();
		for (int i=0;i<failedOffenses.size();i++){
			QRadar qRadar = resilientBrokerDAO.findOffense(failedOffenses.get(i));
			JsonObject failedOffense = qRadarService.getOffense(qRadar, resilientBrokerDTO);
			offenseJsonArray.add(failedOffense.getAsJsonObject());
		}
		long endTime = System.currentTimeMillis();
		long ttOffensesPulled = endTime - beginTime;
		logger.info("Pulled {} Type-1 & {} Type-2, total {} offences from QRadar in {} millisec. ", newOffenses, failedOffenses.size(), offenseJsonArray.size(), ttOffensesPulled);
		
		//-------------- Sort the offenses domain_id wise	
		// -- Create a List from JsonObjects
		beginTime = System.currentTimeMillis();
		List<JsonObject> jsonList = new ArrayList<JsonObject>();
		Set<Integer> domainIds = new HashSet<Integer>();
		String offStatus;  
		String urlString=""; 
		String resilientOrgName="";
		FullIncidentDataDTO newFullIncidentData;
		int x= offenseJsonArray.size();
		for (int i = 0; i < x; i++) {
			JsonObject offence = (JsonObject)offenseJsonArray.get(0);
		    offStatus = offence.get(RBConstants.KEY_STATUS).getAsString();
		    domainIds.add(offence.get("domain_id").getAsInt());
		    String givenOrg4DomainId = utils.getValue(offence.get("domain_id").getAsString());
		 
		    // Don't add closed offenses and undefined domains in List
		    if (!offStatus.equalsIgnoreCase(RBConstants.KEY_OFFENSE_STATUS_CLOSED) && givenOrg4DomainId != null) {
			    resilientBrokerUtils.addNewlyPulledOffenseInDb(offence, resilientBrokerDTO); //Add new opened offense in DB
		    	jsonList.add(offence);
		    }
		    
			offenseJsonArray.remove(0);  // Remove the offense from JsonArray
		}
			resilientBrokerDAO.deleteDummy(-99L); // Remove first dummy DB row, if any

			logger.info(">>>> Added all newly pulled offenses in db but only {} are qualified, rest all are closed/non-qualified.", jsonList.size());

		Set<Integer> sortedDomains = new TreeSet<Integer>();
		sortedDomains.addAll(domainIds);
		
		// -- Sort the List using Collection		
		Collections.sort( jsonList, new Comparator<JsonObject>() {

		    public int compare(JsonObject a, JsonObject b) {
		    	Integer valA = new Integer(0);
		    	Integer valB = new Integer(0);

		        try { 
		            valA = a.get("domain_id").getAsInt();
		            valB = b.get("domain_id").getAsInt();
		        } 
		        catch (Exception e) {
		    		logger.info("Sorting error: ", e);
		        }

		        return valA.compareTo(valB);
		    }
		});
		//--- put sorted values in JsonArray again
		Integer[] domains = domainIds.toArray(new Integer[0]);
	    HashMap<Integer, Integer> domainData = new HashMap<Integer,Integer>();
	    for (int i=0;i<domainIds.size();i++)
	    	domainData.put(domains[i], 0);
	    
 		for (int i = 0; i < jsonList.size(); i++) { // identify number of offenses in each domain
 			offenseJsonArray.add(jsonList.get(i));
 			int freq = domainData.get(jsonList.get(i).get("domain_id").getAsInt());
 			domainData.put(jsonList.get(i).get("domain_id").getAsInt(),++freq);
		}
 		endTime = System.currentTimeMillis();
 		Long ttStoreNSort = endTime-beginTime;
		if (offenseJsonArray != null && offenseJsonArray.size() > 0) {
			logger.info("===> Stored & sorted {} offences from {} unique QRadar domains: {}.", offenseJsonArray.size(), domainIds.size(), domains);
		/* Display domains & offences */
		logger.info("\nTotal time taken: {} millisec.\nDomain IDs and respective num of opened offences are: {} ",ttStoreNSort, domainData.toString());
		SimpleClient simpleClient = null;
		String domainId = "-100"; // DomainId of first offense
		FullIncidentDataDTO fullIncidentData = null;
		
		for (int i = 0; i < offenseJsonArray.size(); i++) {
			transactionRecord = new TransactionLog();
			beginTime = System.currentTimeMillis();
			JsonObject offenseJsonObject = (JsonObject) offenseJsonArray.get(i);
			int domain_id = offenseJsonObject.get(RBConstants.KEY_DOMAIN_ID).getAsInt();
			String offenseId = offenseJsonObject.get(RBConstants.KEY_ID).getAsString();
			String startTime = offenseJsonObject.get(RBConstants.KEY_START_TIME).getAsString();
			String status = offenseJsonObject.get(RBConstants.KEY_STATUS).getAsString();
			try {
//				logger.info("Checking Incident Automatic Escalation Condition for offense_id: {}", offenseId);
//				Boolean flag = resilientBrokerUtils.isOffenesQualifies(offenseJsonObject, resilientBrokerDTO);
//				//System.out.println("--Is Offense Qualifies:" + flag);
//				if (!flag) {
//					logger.info("offense_id {} DOES NOT QUALIFY to create Incident", offenseId);
//					logger.info("offense_id {} DOES NOT QUALIFY to create Incident. Full offense object: \n{}",
//							offenseId, offenseJsonObject);
//					continue;
//				}
//				logger.warn("offense_id {} QUALIFIES to create incident in Resilient.", offenseId);
//				logger.info("offense_id {} QUALIFIES to create Incident. Full offense object: \n{}\n----------", offenseId,
//						offenseJsonObject);
			logger.info("Creating incident for offense_id: {} \t start_time: {} \t status: {}\n---------- Full offense object: \n{}\n----------", offenseId,
					startTime, status,offenseJsonObject);
//------------------------------------------- Create incident 
			if (domain_id != Integer.parseInt(domainId)){
				domainId = offenseJsonObject.get(RBConstants.KEY_DOMAIN_ID).toString();
				 
				simpleClient=null;
				Map<String, Object> requestParamsMap = resilientUtils.getRequestParamsMap(domainId, resilientBrokerDTO,
						RBConstants.KEY_URL_RESILIENT_CREATE_INCIDENT);					
				if (null != requestParamsMap) {
					URL url = (URL) requestParamsMap.get(RBConstants.KEY_URL);
					urlString = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_URLSTRING).toString();
					logger.info("createIncident -> url: " + urlString);
					resilientOrgName = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTORGNAME)
							.toString();
					String resilientEmail = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTEMAIL).toString();
					String resilientPasswdPlain = requestParamsMap
							.get(RBConstants.KEY_URL_RESILIENT_RESILIENTPASSWORD_PLAIN).toString();
					File resilientKeystoreFile = (File) requestParamsMap
							.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREFILE);
					String resilientKeystorePassPhrasePlain = requestParamsMap
							.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREPASSPHRASE_PLAIN).toString();
					simpleClient = new SimpleClient(url, resilientOrgName, resilientEmail, resilientPasswdPlain,
							resilientKeystoreFile, resilientKeystorePassPhrasePlain);

					simpleClient.connect();
				}}
					int orgId = simpleClient.getOrgData().getId();
					fullIncidentData = resilientUtils.getIncidentParam(orgId, offenseJsonObject);
					
					//Remove special characters from Name & Description field 
					fullIncidentData.setName( fullIncidentData.getName().replaceAll("[^\\w\\s.:-]","")); //replace everything that is not a word character (a-z in any case, 0-9 or _) or whitespace.
					fullIncidentData.setDescription(fullIncidentData.getDescription().toString().replaceAll("[^\\w\\s.:-]",""));	
					
					String incidentsURI = simpleClient.getOrgURL("incidents?want_full_data=true");
					logger.info("createIncident -> url: " + urlString+incidentsURI);
					logger.info(">>>>>>>>> Incident name: {} and description: {}", fullIncidentData.getName(), fullIncidentData.getDescription());
					
					newFullIncidentData = simpleClient.post(incidentsURI, fullIncidentData, FULL_INC_DATA);
					endTime = System.currentTimeMillis();

					if (null != newFullIncidentData && newFullIncidentData.getId() != null) {
						Integer incidetId = newFullIncidentData.getId();
						logger.info("Successfully created incidentId:" + incidetId + " under Resilient organization: " + resilientOrgName);
					}					

// --------------------------- Create incident ends
			
				if (null != newFullIncidentData) {
					boolean flag2 = resilientBrokerUtils.updateDBwithIncidentDetails(offenseJsonObject, resilientBrokerDTO, newFullIncidentData);
					if (!flag2) {
						logger.error("Failed to insert newly created incident details in Resilient-Broker database.");
					}
					transactionRecord.setTransactionLog(resilientBrokerDAO.findOffense(Long.valueOf(offenseId)));
					transactionRecord.setIncident_name(fullIncidentData.getName());
					transactionRecord.setIncident_description(fullIncidentData.getDescription().toString());
					transactionRecord.setTimetaken(endTime-beginTime);
					transactionRecord.setTransaction_status("[Successful]");
					transactionLog.add(transactionRecord);

				} else {
					logger.error("Failed to create incident for \n offense_id: {} \n start_time: {} \n status: {}",
							offenseId, startTime, status);
				}
			}catch(SimpleHTTPException e) {
				endTime = System.currentTimeMillis();
				transactionRecord.setTransactionLog(resilientBrokerDAO.findOffense(Long.valueOf(offenseId)));
				transactionRecord.setIncident_name(fullIncidentData.getName());
				transactionRecord.setIncident_description(fullIncidentData.getDescription().toString());
				transactionRecord.setTimetaken(endTime-beginTime);
				transactionRecord.setTransaction_status("[Unsuccessful]");
				transactionLog.add(transactionRecord);
				simpleClient = null;
				domainId = "-100";
				logger.error("SimpleHTTPException: Failed to create incident. Message is: ", e);
			}catch(Exception e) {
				logger.error("Exception: Failed to create incident. Exception message is: {}", e);
			} 
		}
		} else {
			logger.error("No new offense found in QRadar at {}", new Date());
		}
		Long ttCreateIncidents = 100L;
		if (transactionLog.size()>0){
			try{ //Print the log rep
			logger.info("--------------- Incidents Creation Run Report <Start>-----------------");	
			out.println("--------------------------------- Incidents Creation Run Report <Start: "+ d.toString()+ " >------------------------");	
			out.println(" Timetaken(ms) to pull "+ (newOffenses+failedOffenses.size())+" offenses ("+newOffenses+" new & "+failedOffenses.size()+" failed) from QRadar: "+ ttOffensesPulled);
			out.println(" Timetaken(ms) to store & sort offenses: "+ ttStoreNSort);			
			ttCreateIncidents = 0L;
			for (int i=0;i< transactionLog.size();i++){
				ttCreateIncidents = ttCreateIncidents + transactionLog.get(i).getTimetaken();
				logger.info("{}) {}", i+1, transactionLog.get(i).printFacts());	
				out.println ((i+1) + ") " + transactionLog.get(i).printFacts());
			}
			logger.info("--------------- Report <End>-----------------");	
			out.println(" Timetaken(ms) to create incidents: "+ ttCreateIncidents);
			d = new Date();
			out.println("---------------------------------- Report <End: "+ d.toString()+ ">-------------------------------");				
			} catch (Exception e){
				logger.info("Can't print the report due to this error : {}",e);}
			}		
		transactionLog = null;
	}

	@Override
	public void updateStatusAndNotesFromResilient(ResilientBrokerDTO resilientBrokerDTO, PrintWriter out) throws ResilientBrokerException {
		logger.info("Entering into updateStatusAndNotesFromResilient() function");
		long beginTime; long endTime;
		int numOffenses=0; 
		int numClosed=0;
		int numNotes=0;
		long timetaken=0L;
		
		d = new Date();
		out.println("--------------------- Offenses status & notes updation run Report <Start: "+ d.toString()+ " >-----------------");	
	
		List<QRadar> qRadars = resilientBrokerDAO.findAllOpenOffenses();

		if (null == qRadars || qRadars.isEmpty()) {
			logger.error(RBConstants.ERROR_MSG_NO_RECORD_FOUND);
			throw new ResilientBrokerException(RBConstants.ERROR_MSG_NO_RECORD_FOUND);
		}

		logger.info("Found {} open record(s) in Resilient-Broker database.", qRadars.size());
		Iterator<QRadar> iterator = qRadars.iterator();

		while (iterator.hasNext()) {
			beginTime = System.currentTimeMillis();
			QRadar qRadar = iterator.next();
			transactionRecord = new TransactionLog();
			String transStatus="";
			
			Integer incidentId = qRadar.getIncident_id();
			Integer orgId = qRadar.getOrg_id();
			Integer domainId = qRadar.getDomain_id();
			resilientBrokerDTO.setResilientOrgName(qRadar.getOrg_name());

			if (qRadar.getIncident_status() != null && qRadar.getIncident_status().equalsIgnoreCase(RBConstants.KEY_DB_OPEN)
					&& qRadar.getOffense_status().equalsIgnoreCase(RBConstants.KEY_DB_OPEN)) {
				try{
				FullIncidentDataDTO fullIncidentDataDTO = resilientService.getIncident(resilientBrokerDTO, domainId,
						incidentId, orgId);
				/**
				 * Close QRadar offense, if Resilient Incident is Closed.
				 */
				if (resilientBrokerUtils.closeOffenseIfIncidentIsClosed(fullIncidentDataDTO, qRadar, resilientBrokerDTO,
						qRadarService, resilientBrokerDAO)) {numClosed++;transStatus="[Closed]";}
				/**
				 * Updating notes from Resilient to QRadar.
				 */
				qRadar.setIncident_id(incidentId);
				qRadar.setOrg_id(orgId);
				if (resilientBrokerUtils.addNoteInOffenseIfNewNoteFoundInIncident(qRadar, resilientBrokerDTO,
						resilientService, qRadarService, resilientBrokerDAO)){numNotes++;transStatus=transStatus+"[NoteAdded]";}
				endTime = System.currentTimeMillis();
				timetaken = timetaken + (endTime-beginTime);
				transactionRecord.setTransactionLog(qRadar);
				transStatus=transStatus+"[Successful]";
				transactionRecord.setTransaction_status(transStatus);
				transactionRecord.setTimetaken(endTime-beginTime);
				} catch (Exception e){
					endTime = System.currentTimeMillis();
					transactionRecord.setTransactionLog(qRadar);
					transStatus=transStatus+"[Unsuccessful]";
					transactionRecord.setTransaction_status(transStatus);
					transactionRecord.setTimetaken(endTime-beginTime);			
					logger.info("Exception from updateStatusAndNotesFromResilient() function: {}",e);
				}
			}
			out.println ((++numOffenses) + ") " + transactionRecord.printFacts());
			logger.info ( "{} ) {} ",numOffenses, transactionRecord.printFacts());
		}
		logger.info ("Total {} offenses (closed={} NewNotes={}) processed in {} millisec", numOffenses,numClosed, numNotes, timetaken);				
		out.println ("Total " +numOffenses+ " offenses (closed="+numClosed+" NewNotes="+numNotes+") processed in "+timetaken+" millisec");				
		d = new Date();
		out.println("-------------------------------- Report <End: "+ d.toString()+ " >-----------------------------");				
		logger.info("-------------------------------- Report <End: {} >-----------------------------", d.toString());				
		logger.info("Exiting from updateStatusAndNotesFromResilient() function");
	}

	@Override
	public void updateStatusAndNotesFromQRadar(ResilientBrokerDTO resilientBrokerDTO, PrintWriter out) throws ResilientBrokerException {
		logger.info("Entering into updateStatusAndNotesFromQRadar() function");
		long beginTime; long endTime;
		int numIncidents=0; 
		int numClosed=0;
		int numUpdated=0;
		int numNotes=0;
		long timetaken=0L;
		
		d = new Date();
		out.println("----------------------- Incidents status & notes updation run Report <Start: "+ d.toString()+ " >-------------------------");	

		List<QRadar> qRadars = resilientBrokerDAO.findAllOpenOffenses();

		if (null == qRadars || qRadars.isEmpty()) {
			logger.error(RBConstants.ERROR_MSG_NO_RECORD_FOUND);
			throw new ResilientBrokerException(RBConstants.ERROR_MSG_NO_RECORD_FOUND);
		}
		logger.info("Found {} open record(s) in Resilient-Broker database.", qRadars.size());
		Iterator<QRadar> iterator = qRadars.iterator();

		while (iterator.hasNext()) {
			beginTime = System.currentTimeMillis();
			QRadar qRadar = iterator.next();
			transactionRecord = new TransactionLog();
			String transStatus="";

			resilientBrokerDTO.setResilientOrgName(qRadar.getOrg_name());
			
			if (qRadar.getIncident_status().equalsIgnoreCase(RBConstants.KEY_DB_OPEN) 
					&& qRadar.getOffense_status().equalsIgnoreCase(RBConstants.KEY_DB_OPEN)) {
				try{
				JsonObject offenseJsonObject = qRadarService.getOffense(qRadar, resilientBrokerDTO);
				/**
				 * Close Incident if Offense is Closed.
				 */
				if (resilientBrokerUtils.closeIncidentIfOffenseIsClosed(offenseJsonObject, qRadar, resilientBrokerDTO,
						resilientService, resilientBrokerDAO)){numClosed++;transStatus="[Closed]";}
				/**
				 * Update description in Incident
				 */
				if (resilientBrokerUtils.updateDescriptionIfCountsChanged(offenseJsonObject, qRadar)){
					numUpdated++;transStatus+="[Updated]";
				}
				/**
				 * Adding new Notes in Incident
				 */
				if (resilientBrokerUtils.addNoteInIncidentIfNewNoteFoundInOffense(offenseJsonObject, qRadar,
						resilientBrokerDTO, qRadarService, resilientService, resilientBrokerDAO)){
				numNotes++;transStatus=transStatus+"[NoteAdded]";
				}
				
				endTime = System.currentTimeMillis();
				timetaken = timetaken + (endTime-beginTime);
				transactionRecord.setTransactionLog(qRadar);
				transStatus=transStatus+"[Successful]";
				transactionRecord.setTransaction_status(transStatus);
				transactionRecord.setTimetaken(endTime-beginTime);
				} catch (Exception e){
					endTime = System.currentTimeMillis();
					transactionRecord.setTransactionLog(qRadar);
					transStatus=transStatus+"[Unsuccessful]";
					transactionRecord.setTransaction_status(transStatus);
					transactionRecord.setTimetaken(endTime-beginTime);			
					logger.info("Exception from updateStatusAndNotesFromQRadar() function: {}",e);
				}
				out.println ((++numIncidents) + ") " + transactionRecord.printFacts());				
			} 
		}
			out.println ("Total " +numIncidents+ " incidents (closed="+numClosed+",updated="+numUpdated+",NewNotes="+numNotes+") processed in "+timetaken+" millisec");				
			logger.info ( "{} ) {} ",numIncidents, transactionRecord.printFacts());

			d = new Date();
			out.println("----------------------------------- Report <End: "+ d.toString()+ " >-------------------------------");				
			logger.info("-------------------------------- Report <End: {} >-----------------------------", d.toString());				
			logger.info("Exiting from updateStatusAndNotesFromQRadar() function");
	}
}