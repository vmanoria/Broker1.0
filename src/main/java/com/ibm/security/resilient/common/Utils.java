package com.ibm.security.resilient.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ibm.security.resilient.exception.ResilientBrokerException;

/**
 * Resilient-Broker utility class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@Component
public class Utils {
	/**
	 * Utils logger instance
	 */
	private Logger logger = LoggerFactory.getLogger(Utils.class);
	/**
	 * Resilient-Broker QRadar utility instance
	 */
	@Autowired
	private QRadarUtils qRadarUtils;

	/**
	 * This method accepts the key and returns the value from config.properties
	 * 
	 * @param key
	 *            key name to get value from application.properties file
	 * @return the value of key
	 */
	public String getValue(String key) {
		logger.debug("Getting value for {}, from application.properties file.", key);
		String value = null;
		Properties prop = new Properties();
		String filePath = new File("application.properties").getAbsolutePath();

		try (InputStream input = new FileInputStream(filePath)) {
			prop.load(input);
			value = prop.getProperty(key);
		} catch (IOException ex) {
			value=null;
			logger.error("Failed to read value for {}, from application.properties file. Context: {}", key, ex);
		}
		return value;
	}

	/**
	 * Read CloseableHttpResponse object, parse the response body (content) and
	 * returns as String object.
	 * 
	 * @param response
	 *            CloseableHttpResponse object
	 * @return response body (content) as String object
	 * @throws ResilientBrokerException
	 *             if Input/Output exception occurs
	 */
	public String getResponseBody(CloseableHttpResponse response) throws ResilientBrokerException {
		String body = null;

		if (null != response && response.getEntity() != null) {

			try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));) {
				StringBuilder stringBuilder = new StringBuilder();
				String line = RBConstants.CHAR_EMPTY_STRING;

				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				body = stringBuilder.toString();
			} catch (IOException ioe) {
				logger.error("Failed to read Http response body. Context: {}", ioe);
				throw new ResilientBrokerException(ioe.getMessage());
			}
		}
		return body;
	}

	/**
	 * Close CloseableHttpResponse and CloseableHttpClient objects.
	 * 
	 * @param response
	 *            Object of CloseableHttpResponse class for closing.
	 * @param client
	 *            Object of CloseableHttpClient class for closing.
	 * @throws ResilientBrokerException
	 *             if any Input/Output exception occures.
	 */
	public void cleanUp(CloseableHttpResponse response, CloseableHttpClient client) throws ResilientBrokerException {

		if (null != response) {

			try {
				response.close();
			} catch (IOException e) {
				throw new ResilientBrokerException(e.getMessage());
			}
		}

		if (null != client) {

			try {
				client.close();
			} catch (IOException e) {
				throw new ResilientBrokerException(e.getMessage());
			}
		}
	}

	/**
	 * checks passed string for null or empty
	 * 
	 * @param string
	 *            String object to be checked
	 * @return true if passed String object is null or empty, else true
	 */
	public boolean isNullOrEmpty(String string) {
		boolean flag = false;

		if (string == null || string.trim().length() == 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * checks the service is up and accessable or not, by calling simple HttpGet
	 * request on the given url
	 * 
	 * @param url
	 *            service url to be checked
	 * @param user
	 *            username for calling service
	 * @param password
	 *            password for calling service
	 * @return
	 *         <p>
	 *         true if the HttpGet request returns HTTP_STATUS_CODE 200, else false.
	 *         There might be following reason for not getting response code 200.
	 *         </p>
	 *         <p>
	 *         <ul>
	 *         <li>Passed url resource is not running</li>
	 *         <li>Passed url resource is not accessable from Resilient-Broker
	 *         machine</li>
	 *         <li>Username is incorrect</li>
	 *         <li>Password is incorrect</li>
	 *         </ul>
	 *         </p>
	 * 
	 * @throws IOException
	 *             if the Input/Output exception occurs
	 */
	public boolean isServiceUp(String url, String user, String password) throws ResilientBrokerException {
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		boolean flag = false;

		try {
			HttpGet request = new HttpGet(url);
			String mix = Base64.getEncoder().encodeToString((user + RBConstants.CHAR_COLON + password).getBytes());
			request.setHeader(RBConstants.KEY_AUTHORIZATION, RBConstants.KEY_BASIC + mix);
			request.setHeader(RBConstants.KEY_CONTENT_TYPE, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			request.setHeader(RBConstants.KEY_ACCEPT, RBConstants.CONTENT_TYPE_APPLICATION_JSON);
			client = qRadarUtils.getCloseableHttpClient();
			response = client.execute(request);

			if (null != response) {
				StatusLine sl = response.getStatusLine();
				Integer httpStatusCode = sl.getStatusCode();

				if (httpStatusCode != 200) {
					logger.error("URL: {} IS NOT accessable from Resilient-Broker app. Getting {} HTTP STATUS CODE",
							url, httpStatusCode);
					return false;
				}
			}
			flag = true;
		} catch (IOException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			logger.error("Exception in isServiceUp(). Context: {}", e);
			throw new ResilientBrokerException(e.getMessage());
		} finally {
			cleanUp(response, client);
		}
		return flag;
	}
}
