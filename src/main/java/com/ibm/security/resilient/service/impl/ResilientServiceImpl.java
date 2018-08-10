package com.ibm.security.resilient.service.impl;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.co3.dto.comment.json.CommentDTO;
import com.co3.dto.json.FullIncidentDataDTO;
import com.co3.simpleclient.SimpleClient;
import com.co3.simpleclient.SimpleClient.SimpleHTTPException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.JsonObject;
import com.ibm.security.resilient.common.RBConstants;
import com.ibm.security.resilient.common.ResilientUtils;
import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;
import com.ibm.security.resilient.model.QRadar;
import com.ibm.security.resilient.service.ResilientService;

/**
 * Resilient Service implementation class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 17th, 2017
 */
@Service
public class ResilientServiceImpl implements ResilientService {
	/**
	 * logger instance for ResilientServiceImpl class
	 */
	private Logger logger = Logger.getLogger(ResilientServiceImpl.class);
	/**
	 * FullIncidentDataDTO type reference
	 */
	private static final TypeReference<FullIncidentDataDTO> FULL_INC_DATA = new TypeReference<FullIncidentDataDTO>() {
	};
	/**
	 * Resilient Broker utility instance
	 */
	@Autowired
	private ResilientUtils resilientUtils;
	
	@Override
	public FullIncidentDataDTO createIncident(JsonObject offenseJsonObject, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException {
		logger.info("Entering into createIncident() function with offense id:" + offenseJsonObject.get(RBConstants.KEY_ID));
		logger.info("Entering into createIncident() function with jsonObject:" + offenseJsonObject);

		SimpleClient simpleClient = null;
		String domainId = null;
		FullIncidentDataDTO fullIncidentData = null;

		if (null != offenseJsonObject.get(RBConstants.KEY_DOMAIN_ID)) {
			domainId = offenseJsonObject.get(RBConstants.KEY_DOMAIN_ID).toString();
		} else {
			logger.error("QRadar domain_id not found in Offense");
			throw new ResilientBrokerException("domain_id not found in Offense.");
		}

		try {
			Map<String, Object> requestParamsMap = resilientUtils.getRequestParamsMap(domainId, resilientBrokerDTO,
					RBConstants.KEY_URL_RESILIENT_CREATE_INCIDENT);

			if (null != requestParamsMap) {
				URL url = (URL) requestParamsMap.get(RBConstants.KEY_URL);
				String urlString = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_URLSTRING).toString();
				logger.info("createIncident -> url: " + urlString);
				String resilientOrgName = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTORGNAME)
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
				int orgId = simpleClient.getOrgData().getId();
				fullIncidentData = resilientUtils.getIncidentParam(orgId, offenseJsonObject);
				
				logger.info(">>>>>>>>> offenseJsonObject:" + offenseJsonObject);
				String incidentsURI = simpleClient.getOrgURL("/incidents?want_full_data=true");
				logger.info("createIncident -> url: " + urlString+incidentsURI);
				FullIncidentDataDTO newFullIncidentData = simpleClient.post(incidentsURI, fullIncidentData, FULL_INC_DATA);
				
				if (null != newFullIncidentData && newFullIncidentData.getId() != null) {
					Integer incidetId = newFullIncidentData.getId();
					logger.info("Successfully created incidentId:" + incidetId + " under Resilient organization: " + resilientOrgName);
					return newFullIncidentData;
				}
			}
		}catch(SimpleHTTPException e) {
			logger.error("SimpleHTTPException: Failed to create incident. Message is: ", e);

			//throw new ResilientBrokerException(e.getMessage());
			System.out.println(e.getMessage());
		}catch(Exception e) {
			logger.error("Exception: Failed to create incident. Exception message is: {}", e);
			//throw new ResilientBrokerException(e.getMessage());
			System.out.println(e.getMessage());
		} finally {

			if (null != simpleClient) {
				simpleClient = null;
			}
			logger.info("Exiting from createIncident() function with offense id: " + offenseJsonObject.get(RBConstants.KEY_ID));
		}
		return null;
	}

	@Override
	public CommentDTO postIncidentNote(String noteText, QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException {
		logger.info("Entering into postIncidentNote() function");
		logger.debug("Entering into postIncidentNote() function with qRadar:" + qRadar);
		
		if (noteText != null && qRadar != null && resilientBrokerDTO != null) {
			SimpleClient simpleClient = null;
			String domainId = null;
			
			if (null != qRadar.getDomain_id()) {
				domainId = qRadar.getDomain_id().toString();
			} else {
				logger.error("QRadar domainId not found in Offense");
				throw new ResilientBrokerException("domainId not found in Offense.");
			}
			
			try {
				Map<String, Object> requestParamsMap = resilientUtils.getRequestParamsMap(domainId, resilientBrokerDTO,
						RBConstants.KEY_URL_RESILIENT_POST_INCIDENTS_NOTE);
				URL url = (URL) requestParamsMap.get(RBConstants.KEY_URL);
				String resilientOrgName = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTORGNAME)
						.toString();
				String resilientEmail = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTEMAIL).toString();
				String resilientPasswdPlain = requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTPASSWORD_PLAIN).toString();
				File resilientKeystoreFile = (File) requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREFILE);
				String resilientKeystorePassPhrasePlain = requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREPASSPHRASE_PLAIN).toString();
				String urlString = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_URLSTRING).toString()
						.replace(RBConstants.KEY_ORG_ID, qRadar.getOrg_id().toString())
						.replace(RBConstants.KEY_INCIDENT_ID, qRadar.getIncident_id().toString());
				logger.debug("postIncidentNote() -> url:" + url.toString());
				simpleClient = new SimpleClient(url, resilientOrgName, resilientEmail, resilientPasswdPlain,
						resilientKeystoreFile, resilientKeystorePassPhrasePlain);
				simpleClient.connect();
				CommentDTO commentDTO = new CommentDTO();
				commentDTO.setText(noteText);
				CommentDTO commentDTO2 = simpleClient.post(urlString, commentDTO, CommentDTO.class);
				logger.info("Successfully created note for incidentId " + qRadar.getIncident_id()
						+ ", under Resilient organization:" + resilientOrgName);
				logger.info("Exiting from postIncidentNote() function");
				return commentDTO2;
			} catch (ResilientBrokerException e) {
				logger.error("Exception occured while creating note in incident. Context: {}", e);
				throw e;
			} catch (Exception e) {
				logger.error("Exception occured while creating note in incident. Context: {}", e);
				throw new ResilientBrokerException(e.getMessage());
			} finally {
				
				if (simpleClient != null) {
					simpleClient = null;
				}
			}
		}
		return null;
	}

	@Override
	public FullIncidentDataDTO getIncident(ResilientBrokerDTO resilientBrokerDTO, Integer domainId, Integer incidentId,
			Integer orgId) throws ResilientBrokerException {
		logger.info("Entering into getIncident() function");
		logger.debug("Entering into getIncident() function with domainId: " + domainId + " and incidentId: " + incidentId);
		SimpleClient simpleClient = null;
		
		try {
			Map<String, Object> requestParamsMap = resilientUtils.getRequestParamsMap(domainId + RBConstants.CHAR_EMPTY_STRING,
					resilientBrokerDTO, RBConstants.KEY_URL_RESILIENT_GET_INCIDENTS);
			
			if (null != requestParamsMap) {
				String resilientOrgName = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTORGNAME)
						.toString();
				String resilientEmail = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTEMAIL).toString();
				String resilientPasswdPlain = requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTPASSWORD_PLAIN).toString();
				File resilientKeystoreFile = (File) requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREFILE);
				String resilientKeystorePassPhrasePlain = requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREPASSPHRASE_PLAIN).toString();
				String urlString = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_URLSTRING).toString();
				urlString = urlString.replace(RBConstants.KEY_ORG_ID, orgId + RBConstants.CHAR_EMPTY_STRING);
				urlString = urlString.replace(RBConstants.KEY_INCIDENT_ID, incidentId + RBConstants.CHAR_EMPTY_STRING);
				URL url = new URL(urlString);
				logger.debug("inside getIncident() function -> url:" + url.toString());
				simpleClient = new SimpleClient(url, resilientOrgName, resilientEmail, resilientPasswdPlain,
						resilientKeystoreFile, resilientKeystorePassPhrasePlain);
				simpleClient.connect();
				FullIncidentDataDTO fullIncidentDataDTO = simpleClient.get(urlString, FullIncidentDataDTO.class);
				logger.info("Successfully fetched incident from Resilient for domainId:" + domainId + " and incidentId: "
						+ incidentId);
				logger.info("Exiting from getIncident() function");
				return fullIncidentDataDTO;
			}
		} catch (ResilientBrokerException e) {
			logger.error("Failed in fetching incident from Resilient for domainId:" + domainId + ", and incidentId: "
					+ incidentId);
			logger.error("Exception context: {}", e);
			throw e;
		} catch (Exception e) {
			logger.error("Failed in fetching incident from Resilient for domainId:" + domainId + ", and incidentId: "
					+ incidentId);
			throw new ResilientBrokerException(e.getMessage());
		} finally {
			
			if (null != simpleClient) {
				simpleClient = null;
			}
		}
		return null;
	}

	public CommentDTO[] getIncidentNotes(ResilientBrokerDTO resilientBrokerDTO, Integer domainId, Integer incidentId,
			Integer orgId) throws ResilientBrokerException {
		//logger.info("Entering into getIncidentNotes() function");
		//logger.debug("Entering into getIncidentNotes() function with domainId:" + domainId + ", and incidentId:" + incidentId);
		SimpleClient simpleClient = null;
		
		try {
			Map<String, Object> requestParamsMap = resilientUtils.getRequestParamsMap(domainId + RBConstants.CHAR_EMPTY_STRING,
					resilientBrokerDTO, RBConstants.KEY_URL_RESILIENT_GET_INCIDENTS_NOTES);
			
			if (null != requestParamsMap) {
				String resilientOrgName = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTORGNAME)
						.toString();
				String resilientEmail = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTEMAIL).toString();
				String resilientPasswdPlain = requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTPASSWORD_PLAIN).toString();
				File resilientKeystoreFile = (File) requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREFILE);
				String resilientKeystorePassPhrasePlain = requestParamsMap
						.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREPASSPHRASE_PLAIN).toString();
				String urlString = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_URLSTRING).toString();
				urlString = urlString.replace(RBConstants.KEY_ORG_ID, orgId + RBConstants.CHAR_EMPTY_STRING);
				urlString = urlString.replace(RBConstants.KEY_INCIDENT_ID, incidentId + RBConstants.CHAR_EMPTY_STRING);
				URL url = new URL(urlString);
				//logger.debug("inside getIncidentNotes() function -> url:" + url.toString());
				simpleClient = new SimpleClient(url, resilientOrgName, resilientEmail, resilientPasswdPlain,
						resilientKeystoreFile, resilientKeystorePassPhrasePlain);
				simpleClient.connect();
				CommentDTO[] commentDTOArray = simpleClient.get(urlString, CommentDTO[].class);
				logger.info("Successfully fetched incident note from Resilient for orgId:" + domainId
						+ ", and incidentId:" + incidentId);
				//logger.info("Exiting from getIncidentNotes() function");
				return commentDTOArray;
			}else{
				throw new ResilientBrokerException("Failed to read Resilient parameter.");
			}
		} catch (ResilientBrokerException e) {
			logger.error("Failed to get incident note. Exception message is: {}", e);
			throw e;
		} catch (Exception e) {
			logger.error("Failed to get incident note. Exception message is: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}  finally {
			
			if (null != simpleClient) {
				simpleClient = null;
			}
		}
	}

	public FullIncidentDataDTO updateIncidentToClose(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO,
			Integer offenseClosingReasonId) throws ResilientBrokerException {
		logger.info("Entering into updateIncidentToClose() function with incident_id: " + qRadar.getIncident_id());
		SimpleClient simpleClient = null;
		String domainId = qRadar.getDomain_id().toString();
		String orgId = qRadar.getOrg_id().toString();
		Integer incidentId = qRadar.getIncident_id();
		
		FullIncidentDataDTO existingIncident = null;
		try {
			existingIncident = resilientUtils.getExistingIncident(qRadar, resilientBrokerDTO);
		} catch (Exception ex) {
			logger.error(RBConstants.FAILED_TO_CLOSE_INCIDENT, ex);
			throw new ResilientBrokerException(ex.getMessage());
		}
		
		
		if (null != existingIncident) {
			
			try {
				Map<String, Object> requestParamsMap = resilientUtils.getRequestParamsMap(domainId, resilientBrokerDTO,
						RBConstants.KEY_URL_RESILIENT_PUT_INCIDENTS_UPDATE);
				
				if (null != requestParamsMap) {
					String resilientOrgName = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTORGNAME)
							.toString();
					String resilientEmail = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_RESILIENTEMAIL)
							.toString();
					String resilientPasswdPlain = requestParamsMap
							.get(RBConstants.KEY_URL_RESILIENT_RESILIENTPASSWORD_PLAIN).toString();
					File resilientKeystoreFile = (File) requestParamsMap
							.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREFILE);
					String resilientKeystorePassPhrasePlain = requestParamsMap
							.get(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREPASSPHRASE_PLAIN).toString();
					String urlString = requestParamsMap.get(RBConstants.KEY_URL_RESILIENT_URLSTRING).toString();
					urlString = urlString.replace(RBConstants.KEY_ORG_ID, orgId + RBConstants.CHAR_EMPTY_STRING);
					urlString = urlString.replace(RBConstants.KEY_INCIDENT_ID, incidentId + RBConstants.CHAR_EMPTY_STRING);
					URL url = new URL(urlString);
					logger.debug("getIncidentNotes() -> url:" + url.toString());
					simpleClient = new SimpleClient(url, resilientOrgName, resilientEmail, resilientPasswdPlain,
							resilientKeystoreFile, resilientKeystorePassPhrasePlain);
					simpleClient.connect();
					existingIncident.setPlanStatus("C"); // close the incident
					FullIncidentDataDTO returnFullIncidentDataDTO = simpleClient.put(urlString, existingIncident,
							FULL_INC_DATA);
					incidentId = returnFullIncidentDataDTO.getId();
					logger.info("Successfully updated incidentId:" + incidentId + ", under Resilient organization:"
							+ resilientOrgName);
					logger.info("Exiting from updateIncidentToClose() function with incident_id:"
							+ qRadar.getIncident_id());
					return returnFullIncidentDataDTO;
				}else{
					throw new ResilientBrokerException("Failed to read Resilient parameter.");
				}
			} catch (ResilientBrokerException e) {
				logger.error(RBConstants.FAILED_TO_CLOSE_INCIDENT, e);
				throw e;
			} catch (Exception e) {
				logger.error(RBConstants.FAILED_TO_CLOSE_INCIDENT, e);
				throw new ResilientBrokerException(e.getMessage());
			} finally {
				
				if (null != simpleClient) {
					simpleClient = null;
				}
			}
		}else{
			logger.error("Incident Id " + incidentId + " not found in Resilient.");
			throw new ResilientBrokerException("Incident Id {} not found in Resilient." + incidentId);
		}
	}

}