package com.ibm.security.resilient.common;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.co3.dto.json.FullIncidentDataDTO;
import com.co3.dto.json.IncidentArtifactDTO;
import com.co3.dto.json.IncidentArtifactPropertyDTO;
import com.google.gson.JsonObject;
import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;
import com.ibm.security.resilient.model.QRadar;
import com.ibm.security.resilient.service.ResilientService;

/**
 * Resilient service utility class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@Component
public class ResilientUtils {
	/**
	 * ResilientUtils logger instance
	 */
	private Logger logger = Logger.getLogger(ResilientUtils.class);
	@Autowired
	private Utils utils;
	@Autowired
	private AESEncryption aESEncryption;
	@Autowired
	private ResilientService resilientService;

	/**
	 * get incident params
	 * 
	 * @param offenseJsonObject
	 *            instance of QRadar offense
	 * @return instance of FullIncidentDataDTO
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	public FullIncidentDataDTO getIncidentParam(int orgId, JsonObject offenseJsonObject) throws ResilientBrokerException {
		String fieldPrefix = utils.getValue(RBConstants.KEY_INCIDENT_FIELD_PREFIX);

		if (fieldPrefix == null || fieldPrefix.length() == 0) {
			logger.error(
					"Resilent Incident Field Prefix (resilent.incident.field.prefix) not found in application.properties");
			throw new ResilientBrokerException("Resilent Incident Field Prefix not found in application.properties");
		}
		String startTime = null;

		if (offenseJsonObject.get(RBConstants.KEY_START_TIME) != null) {
			startTime = offenseJsonObject.get(RBConstants.KEY_START_TIME).toString();
		} else {
			logger.error("QRadar start_time not found in Offense");
			throw new ResilientBrokerException("start_time not found in Offense.");
		}
		//return createFullIncidentDataDTO(getName(offenseJsonObject, utils, fieldPrefix),
			//	getDescription(offenseJsonObject, utils, fieldPrefix), Long.parseLong(startTime));
		return createFullIncidentDataDTO(orgId, getName(offenseJsonObject, utils, fieldPrefix),
			getDescription(offenseJsonObject, utils, fieldPrefix), Long.parseLong(startTime), offenseJsonObject);

	}

	/**
	 * get the request parameter map for creating new incident in Resilient.
	 * 
	 * @param domainId
	 *            Resilient incident domain_id
	 * @param resilientBrokerDTO
	 *            instance of ResilientBrokerDTO
	 * @param apiName
	 *            Resilient API name
	 * @return Map object of request parameter
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	public Map<String, Object> getRequestParamsMap(String domainId, ResilientBrokerDTO resilientBrokerDTO,
			String apiName) throws ResilientBrokerException {
		if (domainId != null && resilientBrokerDTO != null && apiName != null) {
			String resilientOrgName = null;
			URL url = null;
			String resilientEmail = null;
			String resilientPasswordPlain = null;
			File resilientKeystoreFile = null;
			String resilientKeystorePassPhrasePlain = null;
			/**
			 * Getting resilientOrgName
			 */
			resilientOrgName = utils.getValue(domainId);

			if (null == resilientOrgName) {
				logger.error(
						"Resilient Organization name not found in application.properties for domain_id: " + domainId);
				throw new ResilientBrokerException("Resilient Organization name not found");
			}
			logger.debug("Found domain_id " + domainId + " is mapped with Organization name " + resilientOrgName);
			/**
			 * Getting url
			 */
			String urlString = null;

			if (apiName.equals(RBConstants.KEY_URL_RESILIENT_CREATE_INCIDENT)) {
				urlString = utils.getValue(RBConstants.KEY_RESILIENT_URL);
			} else if (apiName.equals(RBConstants.KEY_URL_RESILIENT_POST_INCIDENTS_NOTE)) {
				urlString = resilientBrokerDTO.getResilientApiPostIncidentsNote();
			} else if (apiName.equals(RBConstants.KEY_URL_RESILIENT_GET_INCIDENTS)) {
				urlString = resilientBrokerDTO.getResilientApiGetIncidents();
			} else if (apiName.equals(RBConstants.KEY_URL_RESILIENT_GET_INCIDENTS_NOTES)) {
				urlString = resilientBrokerDTO.getResilientApiGetIncidentsNotes();
			} else if (apiName.equals(RBConstants.KEY_URL_RESILIENT_PUT_INCIDENTS_UPDATE)) {
				urlString = resilientBrokerDTO.getResilientApiPutIncidentsUpdate();
			}

			if (urlString == null || urlString.length() == 0) {
				logger.error("Resilient URL (resilient.url) not found in application.properties");
				throw new ResilientBrokerException("Resilient URL not found in application.properties");
			}

			try {
				url = new URL(urlString);
			} catch (Exception e) {
				throw new ResilientBrokerException(e.getMessage());
			}
			/**
			 * Getting resilientKeystoreFile
			 */
			String resilientKeystorePath = utils.getValue(RBConstants.KEY_RESILIENT_KEYSTORE_PATH);

			if (resilientKeystorePath == null || resilientKeystorePath.length() == 0) {
				logger.error("Resilient Keystore Path (resilient.keystore.path) not found in application.properties");
				throw new ResilientBrokerException("Resilient Keystore Path not found in application.properties");
			}
			resilientKeystoreFile = new File(resilientKeystorePath);

			if (!resilientKeystoreFile.exists() || !resilientKeystoreFile.isFile()) {
				logger.error("Resilient Keystore Path (resilient.keystore.path) not found in application.properties");
				throw new ResilientBrokerException("Resilient Keystore Path not found in application.properties");
			}
			/**
			 * Getting resilientEmail
			 */
			resilientEmail = utils.getValue(RBConstants.KEY_RESILIENT_EMAIL);

			if (resilientEmail == null || resilientEmail.length() == 0) {
				logger.error("Resilient Email (resilient.email) not found in application.properties");
				throw new ResilientBrokerException("Resilient Email not found in application.properties");
			}
			/**
			 * Getting resilientPassword_plain
			 */
			String resilientPasswordEncrypted = utils.getValue(RBConstants.KEY_RESILIENT_PASSWORD);

			if (resilientPasswordEncrypted == null || resilientPasswordEncrypted.trim().length() == 0) {
				logger.error("Resilient Password (resilient.password) not found in application.properties");
				throw new ResilientBrokerException("Resilient Password not found in application.properties");
			}

			try {
				resilientPasswordPlain = aESEncryption.decrypt(resilientPasswordEncrypted);
			} catch (Exception e) {
				throw new ResilientBrokerException(e.getMessage());
			}

			/**
			 * Getting resilientPassword_plain
			 */
			String resilientKeystorePassPhraseEncrypted = utils
					.getValue(RBConstants.KEY_RESILIENT_KEYSTORE_PASS_PHRASE);

			if (resilientKeystorePassPhraseEncrypted == null || resilientKeystorePassPhraseEncrypted.length() == 0) {
				logger.error(
						"Resilient Keystore PassPhrase (resilient.keystore.pass.phrase) not found in application.properties");
				throw new ResilientBrokerException("Resilient Keystore PassPhrase not found in application.properties");
			}

			try {
				resilientKeystorePassPhrasePlain = aESEncryption.decrypt(resilientKeystorePassPhraseEncrypted);
			} catch (Exception e) {
				throw new ResilientBrokerException(e.getMessage());
			}
			/**
			 * Setting all params into a HashMap
			 */
			Map<String, Object> requestParamsMap = new HashMap<>();
			requestParamsMap.put(RBConstants.KEY_URL_RESILIENT_RESILIENTORGNAME, resilientOrgName);
			requestParamsMap.put(RBConstants.KEY_URL, url);
			requestParamsMap.put(RBConstants.KEY_URL_RESILIENT_RESILIENTEMAIL, resilientEmail);
			requestParamsMap.put(RBConstants.KEY_URL_RESILIENT_RESILIENTPASSWORD_PLAIN, resilientPasswordPlain);
			requestParamsMap.put(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREFILE, resilientKeystoreFile);
			requestParamsMap.put(RBConstants.KEY_URL_RESILIENT_RESILIENTKEYSTOREPASSPHRASE_PLAIN,
					resilientKeystorePassPhrasePlain);
			requestParamsMap.put(RBConstants.KEY_URL_RESILIENT_URLSTRING, urlString);
			return requestParamsMap;
		}
		return null;
	}

	/**
	 * Fetch Resilient incident based on passed parameter.
	 * 
	 * @param qRadar
	 *            instance of QRadar
	 * @param resilientBrokerDTO
	 *            instance of ResilientBrokerDTO
	 * @return object of FullIncidentDataDTO
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	public FullIncidentDataDTO getExistingIncident(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException {
		FullIncidentDataDTO existingIncident = resilientService.getIncident(resilientBrokerDTO, qRadar.getDomain_id(),
				qRadar.getIncident_id(), qRadar.getOrg_id());
		Integer resolutionId = null;

		if (null == utils.getValue(qRadar.getOrg_id() + RBConstants.CHAR_EMPTY_STRING)) {
			logger.error("No Resilient OrgId and ResolutionId mapping found for Ord_id " + qRadar.getOrg_id()
					+ " in application.properties file.");
			throw new ResilientBrokerException("No Resilient OrgId and ResolutionId mapping found for Ord_id");
		} else {
			resolutionId = Integer.parseInt(utils.getValue(qRadar.getOrg_id() + RBConstants.CHAR_EMPTY_STRING));
		}
		logger.info("Found " + resolutionId + " ResolutionId for Org Id " + qRadar.getOrg_id());
		existingIncident.setResolutionId(resolutionId);

		if (null != utils.getValue(RBConstants.KEY_INCIDENT_SUMMARY_INCIDENT)) {
			existingIncident.setResolutionSummary(utils.getValue(RBConstants.KEY_INCIDENT_SUMMARY_INCIDENT));
		} else {
			existingIncident.setResolutionSummary("Closing incident.");
		}
		existingIncident.setEndDate(new Date());
		existingIncident.setPlanStatus(RBConstants.KEY_DB_CLOSE);
		return existingIncident;
	}

	/**
	 * generate instance of FullIncidentDataDTO for creating incident in Resilient
	 * server.
	 * 
	 * @param name
	 *            incident name
	 * @param description
	 *            incident description
	 * @param startTime
	 *            incident startTime
	 * @return instance of FullIncidentDataDTO
	 */
	private FullIncidentDataDTO createFullIncidentDataDTO(int orgId, String name, String description, Long startTime, JsonObject offenseJsonObject) {
		FullIncidentDataDTO fullIncidentData = new FullIncidentDataDTO();
		fullIncidentData.setName(name);
		fullIncidentData.setDescription(description);
		Date discoveredDate = new Date();
		discoveredDate.setTime(startTime);
		fullIncidentData.setDiscoveredDate(discoveredDate);
		Date createDate = new Date();
		fullIncidentData.setCreateDate(createDate);
		Date startDate = new Date();
		startDate.setTime(startTime);
		fullIncidentData.setStartDate(startDate);
		//set severity -------------
		Long severityCode = offenseJsonObject.get("severity").getAsLong();
		Long resolutionId = Long.valueOf(utils.getValue(Integer.toString(orgId)));
		Long severity;
		if (severityCode < 4L )
			severity = resolutionId-6; // Low 
			else if (severityCode < 7L)
				severity = resolutionId-5; // Medium
			else 
				severity = resolutionId-4; //High
		fullIncidentData.setSeverityCode(severity);
		// ---------
		Integer offenseType = offenseJsonObject.get("offense_type").getAsInt();
		String offenseSource = offenseJsonObject.get("offense_source").getAsString();
		String desc = offenseJsonObject.get("description").getAsString();
		// Incident artifact details
		List <IncidentArtifactDTO> artifacts = new ArrayList<IncidentArtifactDTO>();
		IncidentArtifactDTO artifact = new IncidentArtifactDTO();
		IncidentArtifactPropertyDTO prop = new IncidentArtifactPropertyDTO();
		Collection <IncidentArtifactPropertyDTO> properties = new ArrayList<IncidentArtifactPropertyDTO>();
		switch (offenseType){
			case 0: case 10:  
				artifact.setType("IP Address");
				prop.setName("source");
				prop.setValue("true");
				break;
			case 1: case 11:  
				artifact.setType("IP Address");
				prop.setName("destination");
				prop.setValue("true");
				break;
			case 3:
				artifact.setType("User Account");
				prop.setName("");
				prop.setValue("");
				break;
			case 4:
				artifact.setType("MAC Address");
				prop.setName("source");
				prop.setValue("true");
				break;
			case 5:
				artifact.setType("MAC Address");
				prop.setName("destination");
				prop.setValue("true");
				break;
			case 7:
				artifact.setType("System Name");
				prop.setName("");
				prop.setValue("");
				break;
			case 8:
				artifact.setType("Port");
				prop.setName("source");
				prop.setValue("true");
				break;
			case 9:
				artifact.setType("Port");
				prop.setName("destination");
				prop.setValue("true");
				break;
			default:
				artifact.setType("String");
				prop.setName("");
				prop.setValue("");
				break;
		}
		artifact.setValue(offenseSource);
		artifact.setDescription(desc);
		properties.add(prop);
		artifact.setProperties(properties);
		artifacts.add(artifact);
		fullIncidentData.setArtifacts(artifacts);
//		fullIncidentData.setPerms(null);
//		fullIncidentData.setPhaseId(1038);
//		fullIncidentData.setCreator(null);
//		List<Object> incidentTypeIds = new ArrayList<>();
//		incidentTypeIds.add(20);
//		fullIncidentData.setIncidentTypeIds(incidentTypeIds);
//		fullIncidentData.setReporter(null);
//		fullIncidentData.setPii(null);
//		fullIncidentData.setPlanStatus("A");
//		fullIncidentData.setDueDate(null);
		return fullIncidentData;
	}

	/**
	 * This method will create name field by reading application.properties
	 * 
	 * @param offenseJsonObject
	 *            object of QRadar offense
	 * @param utils
	 *            ResilientBroker utility instance
	 * @param fieldPrefix
	 *            prefix for generating instance name
	 * @return name for creating new instance
	 */
	public String getName(JsonObject offenseJsonObject, Utils utils, String fieldPrefix) {
		int nameFieldCount = Integer.parseInt(utils.getValue(RBConstants.KEY_INCIDENT_NAME_FIELD_COUNT));
		String nameFormat = utils.getValue(RBConstants.KEY_INCIDENT_NAME_FORMAT);

		for (int i = 1; i <= nameFieldCount; i++) {
			String key = RBConstants.KEY_RESILENT_INCIDENT_NAME + i;
			String value = utils.getValue(key);
			String replaceWithKey = fieldPrefix + value;
			String replaceWithValue = RBConstants.CHAR_EMPTY_STRING;

			if (null != offenseJsonObject.get(value)) {
				replaceWithValue = offenseJsonObject.get(value).toString();
			}
			replaceWithValue = replaceWithValue.replaceAll(RBConstants.CHAR_DOUBLE_QUOTE,
					RBConstants.CHAR_EMPTY_STRING);
			nameFormat = nameFormat.replaceAll(replaceWithKey, replaceWithValue);
		}
		return nameFormat;
	}

	/**
	 * This method will create description field by reading application.properties
	 * 
	 * @param offenseJsonObject
	 *            object of QRadar offense
	 * @param utils
	 *            ResilientBroker utility instance
	 * @param fieldPrefix
	 *            prefix for generating instance description
	 * @return description for creating new instance
	 */
	public String getDescription(JsonObject offenseJsonObject, Utils utils, String fieldPrefix) {
		// Creating description parameter for resilient
		int descriptionFieldCount = Integer
				.parseInt(utils.getValue(RBConstants.KEY_RESILENT_INCIDENT_DESCRIPTION_FIELD_COUNT));
		String descriptionFormat = utils.getValue(RBConstants.KEY_RESILENT_INCIDENT_DESCRIPTION_FORMAT);

		for (int i = 1; i <= descriptionFieldCount; i++) {
			String key = RBConstants.KEY_RESILENT_INCIDENT_DESCRIPTION + i;
			String value = utils.getValue(key);
			descriptionFormat = descriptionFormat.replaceAll(fieldPrefix + value,
					offenseJsonObject.get(value).toString());
		}
		return descriptionFormat;
	}
}
