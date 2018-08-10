package com.ibm.security.resilient.service.impl;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.security.resilient.common.QRadarUtils;
import com.ibm.security.resilient.common.RBConstants;
import com.ibm.security.resilient.common.Utils;
import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;
import com.ibm.security.resilient.model.QRadar;
import com.ibm.security.resilient.service.QRadarService;

/**
 * QRadar services implementation class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 10th, 2017
 */
@Service
public class QRadarServiceImpl implements QRadarService {
	/**
	 * QRadarServiceImpl logger instance
	 */
	private Logger logger = LoggerFactory.getLogger(QRadarServiceImpl.class);
	/**
	 * QRadar utility instance
	 */
	@Autowired
	private QRadarUtils qRadarUtils;
	/**
	 * Resilient-Broker utility instance
	 */
	@Autowired
	private Utils utils;

	@Override
	public JsonArray getOffencesFromQRadar(Long maxOffenseId, ResilientBrokerDTO resilientBrokerDTO) throws ResilientBrokerException {
		logger.info("Entering into getOffencesFromQRadar() method with limit: {}", maxOffenseId);
		JsonArray offenseJsonArray = new JsonArray();
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		
		try {
			String url = resilientBrokerDTO.getQradarApiOffensesFilterId() + maxOffenseId;
			String user = resilientBrokerDTO.getQradarUser();
			String password = resilientBrokerDTO.getQradarPassword();
			HttpGet request = new HttpGet(url);
			String mix = Base64.getEncoder().encodeToString((user + RBConstants.CHAR_COLON + password).getBytes());
			request.setHeader(RBConstants.KEY_AUTHORIZATION, RBConstants.KEY_BASIC + mix);
			request.setHeader(RBConstants.KEY_CONTENT_TYPE, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			request.setHeader(RBConstants.KEY_ACCEPT, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			client = qRadarUtils.getCloseableHttpClient();
			response = client.execute(request);
			String responseBody = utils.getResponseBody(response);
			JsonParser parser = new JsonParser();
			offenseJsonArray = parser.parse(responseBody).getAsJsonArray();
		} catch (Exception e) {
			logger.error("Exception in getOffencesFromQRadar(). Context: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}finally {
			utils.cleanUp(response, client);
		}
		logger.info("Exiting from getOffencesFromQRadar() method with limit: {}, and found {} offenses.", maxOffenseId, offenseJsonArray.size());
		return offenseJsonArray;
	}

	@Override
	public String getSourceIPFromQRadar(String sourceAddressId, ResilientBrokerDTO resilientBrokerDTO) throws ResilientBrokerException {
		String sourceIP = null;
		logger.info("Entering into getSourceIPFromQRadar() method with source_address_id: {}", sourceAddressId);
		JsonObject jsonObject = new JsonObject();
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		
		try {
			String url = resilientBrokerDTO.getQradarApiOffensesSourceAddressId() + sourceAddressId;
			String user = resilientBrokerDTO.getQradarUser();
			String password = resilientBrokerDTO.getQradarPassword();
			HttpGet request = new HttpGet(url);
			String mix = Base64.getEncoder().encodeToString((user + RBConstants.CHAR_COLON + password).getBytes());
			request.setHeader(RBConstants.KEY_AUTHORIZATION, RBConstants.KEY_BASIC + mix);
			request.setHeader(RBConstants.KEY_CONTENT_TYPE, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			request.setHeader(RBConstants.KEY_ACCEPT, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			client = qRadarUtils.getCloseableHttpClient();
			response = client.execute(request);
			String responseBody = utils.getResponseBody(response);
			JsonParser parser = new JsonParser();
			jsonObject = parser.parse(responseBody).getAsJsonObject();
			sourceIP = jsonObject.get("source_ip").toString();
		} catch (Exception e) {
			logger.error("Exception in getSourceIPFromQRadar(). Context: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}finally {
			utils.cleanUp(response, client);
		}
		logger.info("Exiting from getSourceIPFromQRadar() method with source_address_id: {}, and found {} source_id.", sourceAddressId, sourceIP);
		return sourceIP;
	}

	@Override
	public JsonObject putOffenseStatusClosed(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO,
			String resolutionSummary) throws ResilientBrokerException {
		logger.info("Entering into putOffenseStatusClosed() method with offenseId: {}", qRadar.getOffense_id());
		/**
		 * Adding note in QRadar offense, before closing the offense.
		 */
		String noteText = "Resilinet Incident has been closed, hence closing offense.\n"
				+"Incidet Resultion Summary is:\n"
				+ resolutionSummary;
		JsonObject noteJsonObject;
		
		try {
			noteJsonObject = postOffenseNote(noteText, qRadar, resilientBrokerDTO);
		} catch (Exception e1) {
			throw new ResilientBrokerException(e1.getMessage());
		}
		
		if (noteJsonObject != null) {
			CloseableHttpClient client = null;
			CloseableHttpResponse response = null; 
			
			try {
				String url = resilientBrokerDTO.getQradarApiOffensesStatusUpdate();
				url = url.replace(RBConstants.KEY_OFFENSES_ID, qRadar.getOffense_id().toString());
				url = url.replace(RBConstants.KEY_REASON_ID, "1");
				String user = resilientBrokerDTO.getQradarUser();
				String password = resilientBrokerDTO.getQradarPassword();

				HttpPost request = new HttpPost(url);
				String mix = Base64.getEncoder().encodeToString((user + RBConstants.CHAR_COLON + password).getBytes());
				request.setHeader(RBConstants.KEY_AUTHORIZATION, RBConstants.KEY_BASIC + mix);
				request.setHeader(RBConstants.KEY_CONTENT_TYPE, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
				request.setHeader(RBConstants.KEY_ACCEPT, RBConstants.CONTENT_TYPE_APPLICATION_JSON);

				client = qRadarUtils.getCloseableHttpClient();
				response = client.execute(request);
				String responseBody = utils.getResponseBody(response);
				
				JsonParser parser = new JsonParser();
				JsonObject jsonObject = parser.parse(responseBody).getAsJsonObject();
				logger.info("Successfully closed offense id:" + qRadar.getOffense_id());
				return jsonObject;
			} catch (Exception e) {
				logger.error("Failed in Closing QRadar offense.", e);
				throw new ResilientBrokerException(e.getMessage());
			} finally {
				utils.cleanUp(response, client);
			}
		}
		return null;
	}

	@Override
	public JsonObject postOffenseNote(String noteText, QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException {
		//logger.info("Entering into postOffenseNote() method with _offense{}", qRadar.getOffense_id());
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		String noteTextValue = noteText;
		
		if (null == noteTextValue) {
			throw new ResilientBrokerException("postOffenseNote: Exception occured while creating a new Note in QRadar offense - Note text in null");
		}
		
		try {
			/**
			 * Preparing request params
			 */
			String urlString = resilientBrokerDTO.getQradarApiPostOffensesNotes();
			urlString = urlString.replace(RBConstants.KEY_OFFENSES_ID, qRadar.getOffense_id().toString());
			noteTextValue = URLEncoder.encode(noteText, RBConstants.CHARSET_UTF8);
			urlString = urlString.replace(RBConstants.KEY_QRADAR_NOTE_TEXT, noteTextValue);
			URL url = new URL(urlString);
			/**
			 * Creating and executing POST Request object
			 */
			HttpPost request = new HttpPost(url.toString());
			String mix = Base64.getEncoder().encodeToString((resilientBrokerDTO.getQradarUser() + RBConstants.CHAR_COLON + resilientBrokerDTO.getQradarPassword()).getBytes());
			request.setHeader(RBConstants.KEY_AUTHORIZATION, RBConstants.KEY_BASIC + mix);
			request.setHeader(RBConstants.KEY_CONTENT_TYPE, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			request.setHeader(RBConstants.KEY_ACCEPT, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			client = qRadarUtils.getCloseableHttpClient();
			response = client.execute(request);
			String responseBody = utils.getResponseBody(response);
			JsonParser parser = new JsonParser();
			JsonObject jsonObject = parser.parse(responseBody).getAsJsonObject();
			logger.info("postOffenseNote:Successfully created new Note on QRadar for offense id: {}", qRadar.getOffense_id());
			return jsonObject;
		} catch (Exception e) {
			logger.error("postOffenseNote:Failed in created new Note on QRadar for offense id: {}, and Context: {}", qRadar.getOffense_id(), e);
			throw new ResilientBrokerException(e.getMessage());
		} finally {
			utils.cleanUp(response, client);
		}
	}

	@Override
	public JsonObject getOffense(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO) throws ResilientBrokerException {
		logger.info("Entering into getOffense() method with offense id: {}", qRadar.getOffense_id());
		JsonObject offenseJsonObject = new JsonObject();
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		
		try {
			String url = resilientBrokerDTO.getQradarApiGetOffenses().replace(RBConstants.KEY_OFFENSES_ID,
					qRadar.getOffense_id().toString());
			String user = resilientBrokerDTO.getQradarUser();
			String password = resilientBrokerDTO.getQradarPassword();
			HttpGet request = new HttpGet(url);
			String mix = Base64.getEncoder().encodeToString((user + RBConstants.CHAR_COLON + password).getBytes());
			request.setHeader(RBConstants.KEY_AUTHORIZATION, RBConstants.KEY_BASIC + mix);
			request.setHeader(RBConstants.KEY_CONTENT_TYPE, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			request.setHeader(RBConstants.KEY_ACCEPT, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			client = qRadarUtils.getCloseableHttpClient();
			response = client.execute(request);
			String responseBody = utils.getResponseBody(response);
			JsonParser parser = new JsonParser();
			offenseJsonObject = parser.parse(responseBody).getAsJsonObject();
			logger.info("Exiting from getOffense() method with offense id: {}", qRadar.getOffense_id());
			return offenseJsonObject;
		} catch (Exception e) {
			logger.error("Failed to fecth QRadar offense. Exception message is: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}finally {
			utils.cleanUp(response, client);
		}
	}

	@Override
	public JsonArray getOffenseNotes(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO) throws ResilientBrokerException {
		logger.info("Entering into getOffenseNotes() method with offense Id: {}", qRadar.getOffense_id());
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		
		try {
			String url = resilientBrokerDTO.getQradarApiGetOffensesNotes().replace(RBConstants.KEY_OFFENSES_ID,
					qRadar.getOffense_id().toString());
			HttpGet request = new HttpGet(url);
			String mix = Base64.getEncoder().encodeToString((resilientBrokerDTO.getQradarUser() + RBConstants.CHAR_COLON + resilientBrokerDTO.getQradarPassword()).getBytes());
			request.setHeader(RBConstants.KEY_AUTHORIZATION, RBConstants.KEY_BASIC + mix);
			request.setHeader(RBConstants.KEY_CONTENT_TYPE, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			request.setHeader(RBConstants.KEY_ACCEPT, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			client = qRadarUtils.getCloseableHttpClient();
			response = client.execute(request);
			String responseBody = utils.getResponseBody(response);
			JsonParser parser = new JsonParser();
			JsonArray offenseNoteJsonArray = parser.parse(responseBody).getAsJsonArray();
			logger.info("Exiting from getOffenseNotes() method with offense Id: {}", qRadar.getOffense_id(),
					", and found {} offenses notes:", offenseNoteJsonArray.size());
			return offenseNoteJsonArray;
		} catch (Exception e) {
			logger.error("Failed to fecth QRadar offense note. Exception message is: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}finally {
			utils.cleanUp(response, client);
		}
	}
	
	@Override
	public JsonArray getLatestUpdatedOffencesFromQRadar(Long lasUpdatedTime, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException {
		logger.info("Entering into getLatestUpdatedOffencesFromQRadar() method with last_updated_time: {}", lasUpdatedTime);
		JsonArray offenseJsonArray = new JsonArray();
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		
		try {
			String url = resilientBrokerDTO.getQradarApiOffensesFilterLastUpdatedTime() + lasUpdatedTime;
			HttpGet request = new HttpGet(url);
			String mix = Base64.getEncoder().encodeToString((resilientBrokerDTO.getQradarUser() + RBConstants.CHAR_COLON + resilientBrokerDTO.getQradarPassword()).getBytes());
			request.setHeader(RBConstants.KEY_AUTHORIZATION, RBConstants.KEY_BASIC + mix);
			request.setHeader(RBConstants.KEY_CONTENT_TYPE, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			request.setHeader(RBConstants.KEY_ACCEPT, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			client = qRadarUtils.getCloseableHttpClient();
			response = client.execute(request);
			String responseBody = utils.getResponseBody(response);
			JsonParser parser = new JsonParser();
			offenseJsonArray = parser.parse(responseBody).getAsJsonArray();
		} catch (Exception e) {
			logger.error("Exception occured in getLatestUpdatedOffencesFromQRadar(). Context: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		}finally {
			utils.cleanUp(response, client);
		}
		logger.info("Exiting from getLatestUpdatedOffencesFromQRadar() method with last_updated_times: {} , and found {} offenses.", lasUpdatedTime,
				offenseJsonArray.size());
		return offenseJsonArray;
	}
}