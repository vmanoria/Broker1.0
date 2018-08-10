package com.ibm.security.resilient.batch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ibm.security.resilient.common.AESEncryption;
import com.ibm.security.resilient.common.GlobalProperties;
import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;
import com.ibm.security.resilient.service.ResilientBrokerService;

/**
 * Resilient-Broker cron batch scheduler class.
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@Component
public class ResilientBrokerScheduler {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ResilientBrokerService resilientBrokerService;
	@Autowired
	private GlobalProperties global;
	boolean updateSwitch = true;

	@Scheduled(cron = "${resilient.scheduler.cron.create.new.incedent}")
	public void createNewIncedentCronJob() throws ResilientBrokerException {
		Long startTime;
		Long endTime = 0L;
		Long ttTriggerTime;
		PrintWriter out = null;
		
		try{
		// Print the log report
		Date d = new Date();
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd"); 
		String fileName = "logs\\opTransactions_"+sdf.format(d)+".log"; 
		FileWriter writer = new FileWriter(fileName, true);
	    BufferedWriter bw = new BufferedWriter(writer);
	    out = new PrintWriter(bw);


		startTime = System.currentTimeMillis();
		logger.info("*********** Initializing Resilient-Broker application");
		ResilientBrokerDTO resilientBrokerDTO = initApp();
		logger.info("*********** Successfully initializing Resilient-Broker application");

		out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>=====<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");		
		out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Successfully started the Broker schedule at "+d.toString()+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>=====<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");		
		d=new Date();
		out.println("\n=====>>>>> Started creating incidents in Resilient using new QRadar offences at "+d.toString());
		logger.info(">>>>> Started creating new incidents for latest QRadar offences");
		resilientBrokerService.createNewIncedents(resilientBrokerDTO, out);
		Long endTime1 = System.currentTimeMillis();
		ttTriggerTime = endTime1 - startTime;
		logger.info("<<<<< Ended executing create new incedent for latest QRadar offences. ~Timetaken: {} millisec", ttTriggerTime);
		out.println("<<<<<===== Done with new incidents creations for this schedule. ~Timetaken: "+(float)ttTriggerTime/1000+" seconds" );
		
		if (ttTriggerTime < global.getIncidentsOffencesTriggerTime()*1000) {
			d=new Date();
			out.println("\nSince current ~time elapsed: "+(float)ttTriggerTime/1000+ " (seconds) is less than the given trigger value: "+global.getIncidentsOffencesTriggerTime());
		 if (updateSwitch){
			out.println("=====>>>>> (QRadar to Resilient) Incidents status and Notes updation routine triggered at "+d.toString());
			Long startTime2 = System.currentTimeMillis();
			logger.info(">>>>> Started updating Status and Notes from QRadar to Resilient");
			resilientBrokerService.updateStatusAndNotesFromQRadar(resilientBrokerDTO, out);
			Long endTime2 = System.currentTimeMillis();			
			logger.info("<<<<< Ended executing Status and Notes update from QRadar to Resilient. ~Timetaken: {} millisec",endTime2 - startTime2);
			out.println("<<<<<===== Done with updation of Status and Notes from QRadar to Resilient. ~Timetaken: "+(float)(endTime2 - startTime2)/1000+" seconds");
			ttTriggerTime = ttTriggerTime + (endTime2 - startTime2);
		    updateSwitch=false;
		} else {
			out.println("\n=====>>>>>(Resilient to QRadar) Offenses status and Notes updation routine triggered at "+d.toString());
			Long startTime1 = System.currentTimeMillis();
			logger.info(">>>>> Started updating offence Status and Notes from Resilient to QRadar at {}",d.toString());
			resilientBrokerService.updateStatusAndNotesFromResilient(resilientBrokerDTO, out);
			Long endTime2 = System.currentTimeMillis();
			logger.info("<<<<< Ended executing Status and Notes update from Resilient to QRadar. ~Timetaken: {} millisec", endTime2-startTime1);
			out.println("<<<<<===== Done with updation of Status and Notes from Resilient to QRadar. ~Timetaken: "+(float)(endTime2-startTime1)/1000+" seconds"); 
			updateSwitch = true;
		}
		endTime = System.currentTimeMillis();
		logger.info("<<<<< Ended with this run. Overall utilized time: {} seconds.",(endTime - startTime)/1000);	
		d=new Date();
		out.println("<<<<<=====>>>>>       Finished the Broker run at "+d.toString()+". Utilization: "+ (float)(endTime - startTime)/1000+" seconds.      <<<<<=====>>>>>");		             
		out.println("<<<<<========================================================================================================================>>>>> \n");		             
		} 
		}catch (Exception e){
			logger.error("Failed to complete scheduler run. Context {}", e);
		} finally {
			out.close();
		}
		
	}

	/**
	 * Loads all properties from application.properties file
	 * 
	 * @return Object of ResilientBrokerDTO class
	 * @throws ResilientBrokerException
	 *             if any exception occurs while reading application.properties file
	 */
	private ResilientBrokerDTO initApp() throws ResilientBrokerException {
		AESEncryption aesEncryption = new AESEncryption();
		ResilientBrokerDTO resilientBrokerDTO = new ResilientBrokerDTO();
		try {
			resilientBrokerDTO.setApiPostQradars(global.getApiPostQradars());
			resilientBrokerDTO.setQradarApiOffensesFilterId(global.getQradarApiOffensesFilterId());
			resilientBrokerDTO
					.setQradarApiOffensesFilterLastUpdatedTime(global.getQradarApiOffensesFilterLastUpdatedTime());
			resilientBrokerDTO.setQradarApiOffensesSourceAddressId(global.getQradarApiOffensesSourceAddressId());
			resilientBrokerDTO.setQradarApiOffensesStatusUpdate(global.getQradarApiOffensesStatusUpdate());
			resilientBrokerDTO.setQradarApiPostOffensesNotes(global.getQradarApiPostOffensesNotes());
			resilientBrokerDTO.setQradarApiGetOffenses(global.getQradarApiGetOffenses());
			resilientBrokerDTO.setQradarApiGetOffensesNotes(global.getQradarApiGetOffensesNotes());
			resilientBrokerDTO.setQradarPassword(aesEncryption.decrypt(global.getQradarPassword()));
			resilientBrokerDTO.setQradarUser(global.getQradarUser());
			resilientBrokerDTO.setResilientEmail(global.getResilientEmail());
			resilientBrokerDTO
					.setResilientKeystorePassPhrase(aesEncryption.decrypt(global.getResilientKeystorePassPhrase()));
			resilientBrokerDTO.setResilientKeystorePath(global.getResilientKeystorePath());
			resilientBrokerDTO.setResilientPassword(aesEncryption.decrypt(global.getResilientPassword()));
			resilientBrokerDTO.setResilientUrl(global.getResilientUrl());
			resilientBrokerDTO.setResilientApiGetIncidents(global.getResilientApiGetIncidents());
			resilientBrokerDTO.setResilientApiGetIncidentsNotes(global.getResilientApiGetIncidentsNotes());
			resilientBrokerDTO.setResilientApiPutIncidentsUpdate(global.getResilientApiPutIncidentsUpdate());
			resilientBrokerDTO.setResilientApiPostIncidentsNote(global.getResilientApiPostIncidentsNote());
			resilientBrokerDTO.setEnableAutomaticEscalationCondition(global.getEnableAutomaticEscalationCondition());
		} catch (Exception e) {
			logger.error("initApp():Failed to Initialize Resilient-Broker application. Context {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}
		return resilientBrokerDTO;
	}
}