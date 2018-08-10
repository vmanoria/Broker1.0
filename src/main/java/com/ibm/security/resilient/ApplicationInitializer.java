package com.ibm.security.resilient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.ibm.security.resilient.common.AESEncryption;
import com.ibm.security.resilient.common.GlobalProperties;
import com.ibm.security.resilient.common.Utils;
import com.ibm.security.resilient.exception.ResilientBrokerException;

/**
 * This class is for initializing Resilient-Broker application. This class will
 * check loaded application properties are correct or not for running
 * Resilient-Broker application. If any property value is missing or incorrect
 * in application.properties file then app initialization will FAIL and
 * Resilient-Broker application will get terminated.
 * 
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since Sept 08th, 2017
 *
 */
@Component
public class ApplicationInitializer {
	private static Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);
	@Autowired
	private ApplicationContext context;
	@Autowired
	private GlobalProperties globalProperties;
	@Autowired
	private AESEncryption aesEncryption;
	@Autowired
	private Utils utils;
	private String propertiesErrorMessage = "{} has null or empty value in application.properties file.";

	/**
	 * Initilize Resilient-Broker app. Reall all required propertiy files and
	 * validate them.
	 * 
	 * @throws ResilientBrokerException
	 *             if Input/Output Exception occurs
	 */
	public void init() throws ResilientBrokerException {
		logger.info("Intializing Resilinet-Broker app...");
		boolean flag = true;

		/**
		 * Check Resilient properties values
		 */
		if (!isResilientPropertiesValueExist(globalProperties)) {
			flag = false;
		}

		/**
		 * Check QRadar properties values
		 */
		if (!isQRadarPropertiesValueExist(globalProperties)) {
			flag = false;
		}

		if (flag) {
			logger.info("Intialization PASSED Resilinet-Broker app...");
		} else {
			logger.error("Intialization FAILED Resilinet-Broker app...");
			((ConfigurableApplicationContext) context).close();
			System.exit(1);
		}
	}

	/**
	 * to validate Resilient properties in application.properties file.
	 * 
	 * @param globalProperties
	 *            object of GlobalProperties class. springframework will
	 *            load @PropertySource with GlobalProperties entities as
	 *            provided @Value keys.
	 * @return true, if all Resilient properties are correct, else false
	 * @throws IOException
	 *             if Input/Output exception occurs
	 */
	private boolean isResilientPropertiesValueExist(GlobalProperties globalProperties) throws ResilientBrokerException {
		boolean flag = true;

		if (utils.isNullOrEmpty(globalProperties.getResilientUrl())) {
			logger.error(propertiesErrorMessage, "resilient.url");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getResilientEmail())) {
			logger.error(propertiesErrorMessage, "resilient.email");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getResilientPassword())) {
			logger.error(propertiesErrorMessage, "resilient.password");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getResilientKeystorePath())) {
			logger.error(propertiesErrorMessage, "resilient.keystore.path");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getResilientKeystorePassPhrase())) {
			logger.error(propertiesErrorMessage, "resilient.keystore.pass.phrase");
			flag = false;
		}

		if (utils.isNullOrEmpty(globalProperties.getResilientApiGetIncidents())) {
			logger.error(propertiesErrorMessage, "resilient.api.get.incidents.orgid.id");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getResilientApiGetIncidentsNotes())) {
			logger.error(propertiesErrorMessage, "resilient.api.get.incidents.notes");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getResilientApiPutIncidentsUpdate())) {
			logger.error(propertiesErrorMessage, "resilient.api.put.incidents.update");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getResilientApiPostIncidentsNote())) {
			logger.error(propertiesErrorMessage, "resilient.api.post.incidents.note");
			flag = false;
		}

		if (!isResilientServiceAccessable(globalProperties)) {
			flag = false;
		}

		return flag;
	}

	/**
	 * checks if Resilient application URL is accessable from Resilient-Broker
	 * machine.
	 * 
	 * @param globalProperties
	 *            object of GlobalProperties class. springframework will
	 *            load @PropertySource with GlobalProperties entities as
	 *            provided @Value keys.
	 * @return true if Resilient application URL is accessable from Resilient-Broker
	 *         machine, else false
	 * @throws IOException
	 *             if Input/Output exception occurs
	 */
	private boolean isResilientServiceAccessable(GlobalProperties globalProperties) throws ResilientBrokerException {
		boolean flag = true;
		String resilientPasswordPlainText = null;

		try {
			resilientPasswordPlainText = aesEncryption.decrypt(globalProperties.getResilientPassword());
			logger.debug("resilient.password decryption worked.");
		} catch (Exception e) {
			logger.error("Failed to decrypt resilient.password. {}", e);
			flag = false;
		}
		try {
			aesEncryption.decrypt(globalProperties.getResilientKeystorePassPhrase());
			logger.debug("resilient.keystore.pass.phrase decryption worked.");
		} catch (Exception e) {
			logger.error("Failed to decrypt resilient.keystore.pass.phrase. {}", e);
			flag = false;
		}
		if (resilientPasswordPlainText != null) {
			boolean isServiceUp = utils.isServiceUp(globalProperties.getResilientUrl(),
					globalProperties.getResilientEmail(), resilientPasswordPlainText);
			logger.info("Is Resilient service Up? {}", isServiceUp);

			if (!isServiceUp) {
				logger.error(
						"Resilient Server details are incorrect. or Resilient Server {} is not accessable from Resilinet-Broker app.",
						globalProperties.getResilientUrl());
				flag = false;
			}
		} else {
			logger.error(
					"Resilient Server details are incorrect. or Resilient Server {} is not accessable from Resilinet-Broker app.",
					globalProperties.getResilientUrl());
			flag = false;
		}
		return flag;
	}

	/**
	 * to validate QRadar properties in application.properties file.
	 * 
	 * @param globalProperties
	 *            object of GlobalProperties class. springframework will
	 *            load @PropertySource with GlobalProperties entities as
	 *            provided @Value keys.
	 * @return true, if all QRadar properties are correct, else false
	 * @throws IOException
	 *             if Input/Output exception occurs
	 */
	private boolean isQRadarPropertiesValueExist(GlobalProperties globalProperties) throws ResilientBrokerException {
		boolean flag = true;

		if (utils.isNullOrEmpty(globalProperties.getQradarUrl())) {
			logger.error(propertiesErrorMessage, "qradar.url");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarUser())) {
			logger.error(propertiesErrorMessage, "qradar.user");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarPassword())) {
			logger.error(propertiesErrorMessage, "qradar.password");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarApiOffensesFilterId())) {
			logger.error(propertiesErrorMessage, "qradar.api.offenses.filter.id");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarApiOffensesFilterLastUpdatedTime())) {
			logger.error(propertiesErrorMessage, "qradar.api.offenses.filter.last.updated.time");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarApiOffensesStatusUpdate())) {
			logger.error(propertiesErrorMessage, "qradar.api.offenses.status.update");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarApiPostOffensesNotes())) {
			logger.error(propertiesErrorMessage, "qradar.api.post.offenses.notes");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarApiGetOffenses())) {
			logger.error(propertiesErrorMessage, "qradar.api.get.offenses");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarApiGetOffensesNotes())) {
			logger.error(propertiesErrorMessage, "qradar.api.get.offenses.notes");
			flag = false;
		}
		if (utils.isNullOrEmpty(globalProperties.getQradarApiOffensesSourceAddressId())) {
			logger.error(propertiesErrorMessage, "qradar.api.offenses.source.address.id");
			flag = false;
		}
		if (!isQRadarServiceAccessable(globalProperties)) {
			flag = false;
		}
		return flag;
	}

	/**
	 * checks if QRadar application URL is accessable from Resilient-Broker machine.
	 * 
	 * @param globalProperties
	 *            object of GlobalProperties class. springframework will
	 *            load @PropertySource with GlobalProperties entities as
	 *            provided @Value keys.
	 * @return true if QRadar application URL is accessable from Resilient-Broker
	 *         machine, else false
	 * @throws IOException
	 *             if Input/Output exception occurs
	 */
	private boolean isQRadarServiceAccessable(GlobalProperties globalProperties) throws ResilientBrokerException {
		boolean flag = true;
		String qradarPasswordPlainText = null;

		try {
			qradarPasswordPlainText = aesEncryption.decrypt(globalProperties.getQradarPassword());
			logger.debug("qradar.password decryption works");
		} catch (Exception e) {
			logger.error("Failed to decrypt qradar.password {}", e);
			flag = false;
		}

		if (null != qradarPasswordPlainText) {
			boolean isServiceUp = utils.isServiceUp(globalProperties.getQradarUrl(), globalProperties.getQradarUser(),
					qradarPasswordPlainText);
			logger.info("Is QRadar ServiceUp? {}", isServiceUp);

			if (!isServiceUp) {
				logger.error(
						"QRadar Server details are incorrect. or QRadar Server {} is not accessable from Resilinet-Broker app.",
						globalProperties.getQradarUrl());
				flag = false;
			}
		} else {
			logger.error("QRadar application details are incorrect.");
			flag = false;
		}
		return flag;
	}
}
