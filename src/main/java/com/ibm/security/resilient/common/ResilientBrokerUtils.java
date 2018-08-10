package com.ibm.security.resilient.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.co3.dto.comment.json.CommentDTO;
import com.co3.dto.json.FullIncidentDataDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.security.resilient.dao.ResilientBrokerDAO;
import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;
import com.ibm.security.resilient.model.QRadar;
import com.ibm.security.resilient.service.QRadarService;
import com.ibm.security.resilient.service.ResilientService;

/**
 * Resilient-Broker utility class. Contains utility function, which are used in
 * Resilient-Broker service implementation functions.
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 25th, 2017
 */
@Component
public class ResilientBrokerUtils {
	/**
	 * logger of the ResilientBrokerUtils class
	 */
	private Logger logger = LoggerFactory.getLogger(ResilientBrokerUtils.class);
	/**
	 * Resilient-Broker utility class
	 */
	@Autowired
	private Utils utils;
	/**
	 * Resilient-Broker repository/DAO instance 
	 */
	@Autowired
	private ResilientBrokerDAO resilientBrokerDAO;
	@Autowired
	private ResilientUtils reslientUtils;

	/**
	 * Check Automatic Escalation Condition for given offense object and return the
	 * boolean result
	 * 
	 * @param offenseJsonObject
	 *            object of QRadar offense in JsonObject format
	 * @param resilientBrokerDTO
	 *            object of Resilient-Broker class, containing properties
	 * @return true if passed offense qualified "Automatic Escalation Condition"
	 *         otherwise false
	 */
	public boolean isOffenesQualifies(JsonObject offenseJsonObject, ResilientBrokerDTO resilientBrokerDTO) {
		boolean returnFlag = false;
		Boolean enableAutomaticEscalationCondition = resilientBrokerDTO.getEnableAutomaticEscalationCondition();
		logger.info("Is enabled Automatic Escalation Condition? {}", enableAutomaticEscalationCondition);

		if (!enableAutomaticEscalationCondition) {
			return true;
		}
		Integer numberOfOffenseFields = 0;

		if (null != utils.getValue(RBConstants.KEY_NUMBER_OF_OFFENSE_FIELDS)) {
			numberOfOffenseFields = Integer.parseInt(utils.getValue(RBConstants.KEY_NUMBER_OF_OFFENSE_FIELDS).trim());
		}
		List<String> offenceFieldList = null;

		if (numberOfOffenseFields > 0) {
			logger.info("Found {} offense field(s) in Automatic Escalation Condition", numberOfOffenseFields);
			offenceFieldList = new ArrayList<>();

			for (int i = 0; i < numberOfOffenseFields; i++) {

				if (null != utils.getValue(RBConstants.KEY_OFFENSE_FIELD + i)) {
					String fieldName = utils.getValue(RBConstants.KEY_OFFENSE_FIELD + i);
					offenceFieldList.add(fieldName);
				}
			}
		}
		Map<String, List<String>> automaticEscalationConditionMap = new HashMap<>();

		if (null != offenceFieldList && !offenceFieldList.isEmpty()) {

			String offenseFieldName = RBConstants.KEY_OFFENSE_FIELD_NAME;
			for (String fieldName : offenceFieldList) {

				if (null != utils.getValue(offenseFieldName + fieldName + RBConstants.KEY_COUNT)) {
					Integer count = Integer
							.parseInt(utils.getValue(offenseFieldName + fieldName + RBConstants.KEY_COUNT));

					if (count > 0) {
						List<String> valueList = new ArrayList<>();

						for (int i = 0; i < count; i++) {

							if (null != utils.getValue(offenseFieldName + fieldName + RBConstants.CHAR_DOT + i)) {
								String value = utils.getValue(offenseFieldName + fieldName + RBConstants.CHAR_DOT + i);
								valueList.add(value);
							}
						}
						automaticEscalationConditionMap.put(fieldName, valueList);
					}
				}
			}
		}

		for (String key : automaticEscalationConditionMap.keySet()) {
			List<String> expectedValueList = automaticEscalationConditionMap.get(key);

			if (null != expectedValueList && !expectedValueList.isEmpty()) {
				String offenseValue = null;

				Boolean isOffeseArray = Boolean
						.valueOf(utils.getValue(RBConstants.KEY_OFFENSE_FIELD_NAME + key + RBConstants.KEY_ISARRAY));

				if (isOffeseArray) {
					JsonArray jsonArray = offenseJsonObject.get(key).getAsJsonArray();

					for (String expectedValue : expectedValueList) {

						for (int i = 0; i < jsonArray.size(); i++) {
							offenseValue = jsonArray.get(i).toString()
									.replaceAll(RBConstants.CHAR_DOUBLE_QUOTE, RBConstants.CHAR_EMPTY_STRING)
									.replaceAll(RBConstants.CHAR_NEW_LINE, RBConstants.CHAR_EMPTY_STRING);

							if (null != offenseValue && offenseValue.matches(expectedValue)) {
								return true;
							}
						}
					}
				} else {
					Object object = offenseJsonObject.get(key);

					if (null != object) {
						offenseValue = object.toString()
								.replace(RBConstants.CHAR_DOUBLE_QUOTE, RBConstants.CHAR_EMPTY_STRING)
								.replace(RBConstants.CHAR_NEW_LINE, RBConstants.CHAR_EMPTY_STRING);
					}

					for (String expectedValue : expectedValueList) {

						if (null != offenseValue && offenseValue.matches(expectedValue)) {
							return true;
						}
					}
				}
			}
		}
		return returnFlag;
	}

	/**
	 * Insert newly created incident details in Resilient-Broker database.
	 * 
	 * @param offenseJsonObject
	 *            offense of QRadar offense as JsonObject
	 * @param resilientBrokerDTO
	 *            Object of Resilient-Broker class, containing properties
	 * @param incidentId
	 *            newly created incident id
	 * @param orgId
	 *            Organization Id of newly created incident
	 * @return true if newly created incident saved in db else return false.
	 */
	public boolean addNewlyCreatedIncidentInDb(JsonObject offenseJsonObject, ResilientBrokerDTO resilientBrokerDTO,
			Integer incidentId, Integer orgId) throws ResilientBrokerException {
		logger.debug("Adding newly created incident details and db");
		QRadar qRadar = new QRadar();
		Integer domainId = Integer.parseInt(offenseJsonObject.get(RBConstants.KEY_DOMAIN_ID).toString());
		qRadar.setDomain_id(domainId);
		qRadar.setDomain_name(offenseJsonObject.get(RBConstants.KEY_DOMAIN_ID).toString());

		if (null != offenseJsonObject.get(RBConstants.KEY_DESCRIPTION)) {
			qRadar.setDescription(offenseJsonObject.get(RBConstants.KEY_DESCRIPTION).toString()
					.replace(RBConstants.CHAR_DOUBLE_QUOTE, RBConstants.CHAR_EMPTY_STRING)
					.replace(RBConstants.CHAR_NEW_LINE, RBConstants.CHAR_EMPTY_STRING));
		}
		qRadar.setIncident_id(incidentId);
		qRadar.setOffense_id(Long.parseLong(offenseJsonObject.get(RBConstants.KEY_ID).toString()));
		qRadar.setOrg_name(utils.getValue(domainId + RBConstants.CHAR_EMPTY_STRING));
		qRadar.setOrg_id(orgId);
		qRadar.setLast_updated_time(Long.parseLong(offenseJsonObject.get("last_updated_time").toString()));
		qRadar.setIncident_status(RBConstants.KEY_DB_OPEN);
		qRadar.setOffense_status(RBConstants.KEY_DB_OPEN);
		qRadar.setOffense_note_ids(RBConstants.CHAR_EMPTY_STRING);
		qRadar.setIncident_note_ids(RBConstants.CHAR_EMPTY_STRING);
		updateDatabase(qRadar, resilientBrokerDTO);
		return true;
	}

	/**
	 * If Resilient incident is CLOSED then close respective offense in QRadar
	 * 
	 * @param fullIncidentDataDTO
	 *            Resilient incident fullIncidentDataDTO object
	 * @param qRadar
	 *            Resilient-Broker qRadar object
	 * @param resilientBrokerDTO
	 *            Resilient-Broker resilientBrokerDTO object
	 * @param qRadarService
	 *            QRadarService object
	 * @param resilientBrokerDAO
	 *            Resilient-Broker resilientBrokerDAO object
	 * @throws ResilientBrokerException
	 *             if any error occurs
	 */
	public boolean closeOffenseIfIncidentIsClosed(FullIncidentDataDTO fullIncidentDataDTO, QRadar qRadar,
			ResilientBrokerDTO resilientBrokerDTO, QRadarService qRadarService, ResilientBrokerDAO resilientBrokerDAO)
			throws ResilientBrokerException {
		boolean ifClosed = false;
		if (null != fullIncidentDataDTO && fullIncidentDataDTO.getResolutionId() != null
				&& fullIncidentDataDTO.getEndDate() != null) {
			Integer incidentId = qRadar.getIncident_id();
			Long offenseId = qRadar.getOffense_id();
			logger.info("Incident Id: {} is closed.", incidentId);
			logger.info("Closing QRadar offense Id: {}", offenseId);
			JsonObject jsonObject = qRadarService.putOffenseStatusClosed(qRadar, resilientBrokerDTO,
					fullIncidentDataDTO.getResolutionSummary().toString());

			if ((jsonObject.get(RBConstants.KEY_STATUS) != null
					&& !jsonObject.get(RBConstants.KEY_STATUS).toString().equals(RBConstants.KEY_OFFENSE_STATUS_CLOSED))
					|| (jsonObject.get(RBConstants.KEY_CODE) != null && jsonObject.get(RBConstants.KEY_CODE).toString().equals("1008"))) {
				qRadar.setIncident_status(RBConstants.KEY_DB_CLOSE);
				qRadar.setOffense_status(RBConstants.KEY_DB_CLOSE);
				resilientBrokerDAO.update(qRadar);
				ifClosed = true;
			}
		} else {
			logger.info("Incident Id {} is not yet closed.", qRadar.getIncident_id());
		}
		return ifClosed;
	}

	/**
	 * If found a new Note in Resilient incident then add this comment as new Note
	 * in respective QRadar offense
	 * 
	 * @param qRadar
	 *            Resilient-Broker qRadar object
	 * @param resilientBrokerDTO
	 *            Resilient-Broker resilientBrokerDTO object
	 * @param resilientService
	 *            ResilientService service object
	 * @param qRadarService
	 *            QRadarService service object
	 * @param resilientBrokerDAO
	 *            Resilient-Broker resilientBrokerDAO object
	 * @throws ResilientBrokerException
	 *             if any error occurs
	 */
	public boolean addNoteInOffenseIfNewNoteFoundInIncident(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO,
			ResilientService resilientService, QRadarService qRadarService, ResilientBrokerDAO resilientBrokerDAO)
			throws ResilientBrokerException {
		boolean ifNewNote = false;
		CommentDTO[] commentDTOArray = resilientService.getIncidentNotes(resilientBrokerDTO, qRadar.getDomain_id(),
				qRadar.getIncident_id(), qRadar.getOrg_id());

		if (null != commentDTOArray && commentDTOArray.length != 0) {
			List<Integer> resilientCommentIds = getResilientCommentIds(qRadar);
			logger.info("Found {} notes in resilient incident", commentDTOArray.length);

			for (CommentDTO commentDTO : commentDTOArray) {
				Integer noteId = commentDTO.getId();
				String noteText = (String) commentDTO.getText();

				if (!resilientCommentIds.isEmpty()) {
					/**
					 * check commentId already added into local db
					 */
					boolean isIdExist = resilientCommentIds.contains(noteId);
					//logger.info("Is incident notes Id {} exist in localdb?{}", noteId, isIdExist);

					if (!isIdExist) {
						/**
						 * Add this note into QRadar
						 */
						JsonObject jsonObject = qRadarService.postOffenseNote(noteText, qRadar, resilientBrokerDTO);
						updateIncedentNoteId(qRadar, noteId,
								Integer.parseInt(jsonObject.get(RBConstants.KEY_ID).toString()), resilientBrokerDAO);
						ifNewNote=true;
					}
				} else {
					/**
					 * Add this note into QRadar
					 */
					JsonObject jsonObject = qRadarService.postOffenseNote(noteText, qRadar, resilientBrokerDTO);
					updateIncedentNoteId(qRadar, noteId,
							    Integer.parseInt(jsonObject.get(RBConstants.KEY_ID).toString()), resilientBrokerDAO);
					ifNewNote=true;
				}
			}
		}
		return ifNewNote;
	}

	/**
	 * If QRadar offense is CLOSED then close respective incident in Resilient
	 * 
	 * @param offenseJsonObject
	 *            offense object from QRadar
	 * @param qRadar
	 *            Resilient-Broker qRadar object
	 * @param resilientBrokerDTO
	 *            Resilient-Broker resilientBrokerDTO object
	 * @param resilientService
	 *            ResilientService service object
	 * @param resilientBrokerDAO
	 *            Resilient-Broker resilientBrokerDAO object
	 * @throws ResilientBrokerException
	 *             if any error occurs
	 */
	public boolean closeIncidentIfOffenseIsClosed(JsonObject offenseJsonObject, QRadar qRadar,
			ResilientBrokerDTO resilientBrokerDTO, ResilientService resilientService,
			ResilientBrokerDAO resilientBrokerDAO) throws ResilientBrokerException {
		boolean ifClosed=false;
		String status = offenseJsonObject.get(RBConstants.KEY_STATUS).toString()
				.replaceAll(RBConstants.CHAR_DOUBLE_QUOTE, RBConstants.CHAR_EMPTY_STRING);

		if (status != null && status.equals(RBConstants.KEY_OFFENSE_STATUS_CLOSED)) {
			//logger.info("Offense id: {} is CLOSED.", qRadar.getOffense_id());
			Integer offenseClosingReasonId = Integer.parseInt(offenseJsonObject.get(RBConstants.KEY_CLOSING_REASON_ID)
					.toString().replaceAll(RBConstants.CHAR_DOUBLE_QUOTE, RBConstants.CHAR_EMPTY_STRING));
			FullIncidentDataDTO fullIncidentDataDTO = resilientService.updateIncidentToClose(qRadar, resilientBrokerDTO,
					offenseClosingReasonId);

			if (null != fullIncidentDataDTO && fullIncidentDataDTO.getResolutionId() != null) {
				qRadar.setIncident_status(RBConstants.KEY_DB_CLOSE);
				qRadar.setOffense_status(RBConstants.KEY_DB_CLOSE);
				resilientBrokerDAO.update(qRadar);
				ifClosed=true;
			}
		} else {
			//logger.info("Offense id {} is NOT YET CLOSED.", qRadar.getOffense_id());
		}
		return ifClosed;
	}

	/**
	 * If found a new Note in QRadar offense then add this note in respective
	 * Resilient incident
	 * 
	 * @param offenseJsonObject
	 *            offense object
	 * @param qRadar
	 *            Resilient-Broker qRadar object
	 * @param resilientBrokerDTO
	 *            Resilient-Broker resilientBrokerDTO object
	 * @param qRadarService
	 *            QRadarService service object
	 * @param resilientService
	 *            ResilientService service object
	 * @param resilientBrokerDAO
	 *            Resilient-Broker resilientBrokerDAO object
	 * @throws ResilientBrokerException
	 *             if any error occurs
	 */
	public boolean addNoteInIncidentIfNewNoteFoundInOffense(JsonObject offenseJsonObject, QRadar qRadar,
			ResilientBrokerDTO resilientBrokerDTO, QRadarService qRadarService, ResilientService resilientService,
			ResilientBrokerDAO resilientBrokerDAO) throws ResilientBrokerException {
		boolean ifNewNote=false;
		String status = offenseJsonObject.get(RBConstants.KEY_STATUS).toString()
				.replaceAll(RBConstants.CHAR_DOUBLE_QUOTE, RBConstants.CHAR_EMPTY_STRING);
		try {
			if (status != null && !status.equals(RBConstants.KEY_OFFENSE_STATUS_CLOSED)) {
				JsonArray offenseNoteJsonArray = qRadarService.getOffenseNotes(qRadar, resilientBrokerDTO);
				if (null != offenseNoteJsonArray && offenseNoteJsonArray.size() != 0) {
					logger.info("Found {} notes in QRadar offense id {}", offenseNoteJsonArray.size(),
							qRadar.getOffense_id());
					List<Integer> offenseNoteIds = getQRadarNoteIds(qRadar);

					for (int i = 0; i < offenseNoteJsonArray.size(); i++) {
						JsonObject noteJsonObject = offenseNoteJsonArray.get(i).getAsJsonObject();
						Integer offenseNoteId = Integer.parseInt(noteJsonObject.get(RBConstants.KEY_ID).toString());
						String offenseNoteText = noteJsonObject.get(RBConstants.KEY_NOTE_TEXT).toString();
						CommentDTO commentDTO = null;

						if (!offenseNoteIds.isEmpty()) {
							/**
							 * check noteId is already added into local db
							 */
							boolean isIdExist = offenseNoteIds.contains(offenseNoteId);
							logger.info("Is offense notes Id {} exist? {}", offenseNoteId, isIdExist);

							if (!isIdExist) {
								logger.info("Offense Notes Id {} does not exist ", offenseNoteId);
								/**
								 * Add this note into QRadar
								 */
								commentDTO = resilientService.postIncidentNote(offenseNoteText, qRadar,
										resilientBrokerDTO);
								ifNewNote=true;
							}
						} else {
							logger.info("Offense Notes Id {} does not exist ", offenseNoteId);
							/**
							 * Add this note into QRadar
							 */
							commentDTO = resilientService.postIncidentNote(offenseNoteText, qRadar, resilientBrokerDTO);
							ifNewNote=true;
						}
						if (null != commentDTO) {
							Integer incidentNoteId = commentDTO.getId();
							updateIncedentNoteId(qRadar, incidentNoteId, offenseNoteId, resilientBrokerDAO);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception occured in addNoteInIncidentIfNewNoteFoundInOffense(). Context: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}
		return ifNewNote;
	}
	
	/**
	 * If found change in event_count, flow_count, category_count or description
	 *  				in QRadar offense then add update incident description
	 * Resilient incident
	 * 
	 * @param offenseJsonObject
	 *            offense object
	 * @param qRadar
	 *            Resilient-Broker qRadar object
	 * @throws ResilientBrokerException
	 *             if any error occurs
	 */

	public boolean updateDescriptionIfCountsChanged(JsonObject offenseJsonObject, QRadar qRadar)throws ResilientBrokerException
	{
		boolean ifUpdated=false;
		String fieldPrefix = utils.getValue(RBConstants.KEY_INCIDENT_FIELD_PREFIX);
		
		if (fieldPrefix == null || fieldPrefix.length() == 0) {
			logger.error(
					"Resilent Incident Field Prefix (resilent.incident.field.prefix) not found in application.properties");
			throw new ResilientBrokerException("Resilent Incident Field Prefix not found in application.properties");
		}
		String desc = reslientUtils.getDescription(offenseJsonObject, utils, fieldPrefix);
		desc = desc.replaceAll("[^\\w\\s.:-]","");
		
		if (!desc.equalsIgnoreCase(qRadar.getDescription()))
		{		
				qRadar.setDescription(desc);
				resilientBrokerDAO.update(qRadar);
				ifUpdated=true;
		}

		return ifUpdated;
	}
	/**
	 * Update Resilient-Broker database
	 * 
	 * @param qRadar
	 *            object of QRadar class for updating in db
	 * @param resilientBrokerDTO
	 *            object of ResilientBrokerDTO class, contains application
	 *            properties
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	private void updateDatabase(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO) throws ResilientBrokerException {
		//logger.info("Saving qRadar in database: {}", qRadar);

		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			//logger.info("resilient.broker.api.post.qradars:" + resilientBrokerDTO.getApiPostQradars());
			String url = resilientBrokerDTO.getApiPostQradars();
			HttpPost postRequest = new HttpPost(url);
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(qRadar);
			StringEntity input = new StringEntity(jsonInString);
			input.setContentType(RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			postRequest.setEntity(input);
			HttpResponse response = client.execute(postRequest);

			if (response.getStatusLine().getStatusCode() != 201) {
				logger.error("Error in updating offence Id: {} & incident Id: {} in ResilientBroker database", qRadar.getOffense_id(), qRadar.getIncident_id());
				throw new ResilientBrokerException(
						"Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));
			StringBuilder stringBuilder = new StringBuilder();
			String output;

			while ((output = bufferedReader.readLine()) != null) {
				stringBuilder.append(output);
			}
			//logger.info("Resilient-Broker database update response: {}", stringBuilder);
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException>> offence Id: {} & incident Id: {} in ResilientBroker database. Context: {}", qRadar.getOffense_id(), qRadar.getIncident_id(), e);
			throw new ResilientBrokerException(e.getMessage());
		} catch (IOException e) {
			logger.error("IOException>> offence Id: {} & incident Id: {} in ResilientBroker database. Context: {}", qRadar.getOffense_id(), qRadar.getIncident_id(), e);
			throw new ResilientBrokerException(e.getMessage());
		}
	}

	/**
	 * Get list of Resilient comment ids
	 * 
	 * @param qRadar
	 *            Instance of QRadar class
	 * @return list of instance ids
	 */
	private List<Integer> getResilientCommentIds(QRadar qRadar) {
		List<Integer> resilientCommentIds = new ArrayList<>();
		String[] resilientCommentIdsString = qRadar.getIncident_note_ids().split(RBConstants.CHAR_COMMA);

		if (resilientCommentIdsString != null && resilientCommentIdsString.length > 0) {

			for (String id : resilientCommentIdsString) {

				if (null != id && id.trim().length() > 0) {
					resilientCommentIds.add(Integer.parseInt(id));
				}
			}
		}
		return resilientCommentIds;
	}

	/**
	 * update instanceId and offenseId in Resilient-Broker database.
	 * 
	 * @param qRadar
	 *            Instance of QRadar class
	 * @param incidentNoteId
	 *            Resilient instance note Id
	 * @param offenseNoteId
	 *            QRadar offense note Id
	 * @param resilientBrokerDAO
	 *            Instance of ResilientBrokerDAO class
	 * @return Instance of QRadar object
	 */
	public QRadar updateIncedentNoteId(QRadar qRadar, Integer incidentNoteId, Integer offenseNoteId,
			ResilientBrokerDAO resilientBrokerDAO) {
		String incidentNoteIdStr = qRadar.getIncident_note_ids();
		String offenseNoteIdStr = qRadar.getOffense_note_ids();

		if (incidentNoteIdStr == null || incidentNoteIdStr.trim().length() == 0) {
			incidentNoteIdStr = incidentNoteId + RBConstants.CHAR_EMPTY_STRING;
		} else {
			incidentNoteIdStr = incidentNoteIdStr + RBConstants.CHAR_COMMA + incidentNoteId;
		}

		if (offenseNoteIdStr == null || offenseNoteIdStr.trim().length() == 0) {
			offenseNoteIdStr = offenseNoteId + RBConstants.CHAR_EMPTY_STRING;
		} else {
			offenseNoteIdStr = offenseNoteIdStr + RBConstants.CHAR_COMMA + offenseNoteId;
		}

		qRadar.setIncident_note_ids(incidentNoteIdStr);
		qRadar.setOffense_note_ids(offenseNoteIdStr);

		resilientBrokerDAO.update(qRadar);
		return qRadar;
	}

	/**
	 * get list of QRadar offense Ids
	 * 
	 * @param qRadar
	 *            Instance of QRadar class
	 * @return List of QRadar offense Ids
	 */
	private List<Integer> getQRadarNoteIds(QRadar qRadar) {
		List<Integer> qradarNoteIds = new ArrayList<>();
		String[] qradarNoteIdsString = qRadar.getOffense_note_ids().split(RBConstants.CHAR_COMMA);

		if (qradarNoteIdsString != null && qradarNoteIdsString.length > 0) {

			for (String id : qradarNoteIdsString) {

				if (id != null && id.trim().length() != 0) {
					qradarNoteIds.add(Integer.parseInt(id));
				}
			}
		}
		return qradarNoteIds;
	}


	/**
	 * Insert newly pulled offense details in Resilient-Broker database.
	 * 
	 * @param offenseJsonObject
	 *            offense of QRadar offense as JsonObject
	 * @param resilientBrokerDTO
	 *            Object of Resilient-Broker class, containing properties
	 * @return true if newly created incident saved in db else return false.
	 */
	public boolean addNewlyPulledOffenseInDb(JsonObject offenseJsonObject , ResilientBrokerDTO resilientBrokerDTO) throws ResilientBrokerException {
				
		QRadar qRadar = new QRadar();
		Integer domainId = Integer.parseInt(offenseJsonObject.get(RBConstants.KEY_DOMAIN_ID).toString());
		qRadar.setDomain_id(domainId);
		qRadar.setDomain_name(offenseJsonObject.get(RBConstants.KEY_DOMAIN_ID).toString());
	
		if (null != offenseJsonObject.get(RBConstants.KEY_DESCRIPTION)) {
			qRadar.setDescription(offenseJsonObject.get(RBConstants.KEY_DESCRIPTION).toString()
					.replace(RBConstants.CHAR_DOUBLE_QUOTE, RBConstants.CHAR_EMPTY_STRING)
					.replace(RBConstants.CHAR_NEW_LINE, RBConstants.CHAR_EMPTY_STRING));
		} 
		if (offenseJsonObject.get("status").getAsString().equals(RBConstants.KEY_DB_CLOSE)) {
			qRadar.setIncident_id(RBConstants.DEFAULT_ORGID_FOR_CLOSED_OFFENCE);
			qRadar.setOrg_name(RBConstants.DEFAULT_ORG_FOR_CLOSED_OFFENCE+domainId);
			qRadar.setOrg_id(RBConstants.DEFAULT_ORGID_FOR_CLOSED_OFFENCE);
			qRadar.setIncident_status(RBConstants.KEY_DB_CLOSE);
			qRadar.setOffense_status(RBConstants.KEY_DB_CLOSE);
		} else {
			qRadar.setIncident_id(RBConstants.DEFAULT_ORGID_FOR_OPENED_OFFENCE);
			qRadar.setOrg_name(RBConstants.DEFAULT_ORG_FOR_OPENED_OFFENCE);
			qRadar.setOrg_id(RBConstants.DEFAULT_ORGID_FOR_OPENED_OFFENCE);
			//qRadar.setIncident_status(RBConstants.KEY_DB_OPEN);
			qRadar.setOffense_status(RBConstants.KEY_DB_OPEN);
		}
		qRadar.setOffense_id(Long.parseLong(offenseJsonObject.get(RBConstants.KEY_ID).toString()));
		qRadar.setLast_updated_time(Long.parseLong(offenseJsonObject.get("last_updated_time").toString()));
		qRadar.setOffense_note_ids(RBConstants.CHAR_EMPTY_STRING);
		qRadar.setIncident_note_ids(RBConstants.CHAR_EMPTY_STRING);
		updateDatabase(qRadar, resilientBrokerDTO);
		return true;
	}

	/**
	 * Update existing offense record with newly created incident details in Resilient-Broker database.
	 * 
	 * @param offenseJsonObject
	 *            offense of QRadar offense as JsonObject
	 * @param resilientBrokerDTO
	 *            Object of Resilient-Broker class, containing properties
	 * @param incidentId
	 *            newly created incident id
	 * @param orgId
	 *            Organization Id of newly created incident
	 * @return true if newly created incident saved in db else return false.
	 */
	public boolean updateDBwithIncidentDetails(JsonObject offenseJsonObject, ResilientBrokerDTO resilientBrokerDTO,
			FullIncidentDataDTO fullIncidentData) throws ResilientBrokerException {
		//logger.debug("Adding newly created incident details and db");
		Long offenseId = Long.parseLong(offenseJsonObject.get(RBConstants.KEY_ID).toString());
		QRadar qRadar = resilientBrokerDAO.findOffense(offenseId);
		Integer domainId = qRadar.getDomain_id();		
		qRadar.setIncident_id(fullIncidentData.getId());
		qRadar.setOrg_name(utils.getValue(domainId + RBConstants.CHAR_EMPTY_STRING));
		qRadar.setOrg_id(fullIncidentData.getOrgId());
		qRadar.setIncident_status(RBConstants.KEY_DB_OPEN);
		qRadar.setDescription(fullIncidentData.getDescription().toString());
		resilientBrokerDAO.updateOffense(qRadar);
		logger.debug("Updated in db. Offence: {}, Incident: {}, Org: {}", offenseId, fullIncidentData.getId(), qRadar.getOrg_name() );
		return true;
	}
}