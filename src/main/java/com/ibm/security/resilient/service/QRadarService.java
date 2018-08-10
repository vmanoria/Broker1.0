package com.ibm.security.resilient.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;
import com.ibm.security.resilient.model.QRadar;

/**
 * QRadar services interface. This Interface contains QRadar service function.
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 10th, 2017
 */
public interface QRadarService {

	/**
	 * Fetch all the offenses from QRadar application, which are greater than passed
	 * maxOffenseId offense Id.
	 * 
	 * @param maxOffenseId
	 *            max offense Id found in Resilinet-Broker database.
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO class. Which contains all
	 *            properties.
	 * @return Array of offenses found in QRadar grater offense_id than passed
	 *         maxOffenseId
	 * @throws ResilientBrokerException
	 *             if any Input/Output exception occurs
	 */
	public JsonArray getOffencesFromQRadar(Long maxOffenseId, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException;

	/**
	 * Fetch source_ip form QRadar application, for passed sourceAddressId.
	 * 
	 * @param sourceAddressId
	 *            source_address_id of the QRadar offense
	 * @return source_ip of the sourceAddressId
	 * @throws ResilientBrokerException
	 *             if any Input/Output exception occurs
	 */
	public String getSourceIPFromQRadar(String sourceAddressId, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException;

	/**
	 * Update QRadar offense with status CLOSED and also add a Note (Closing reason)
	 * in the same offense before closing it.
	 * 
	 * @param qRadar
	 *            Instance of QRadar class.
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO containing application properties
	 * @param resolutionSummary
	 *            ResolutionSummary will be added as Closing Note in offense before
	 *            closing it.
	 * @return QRadar offense object, after closing it
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	public JsonObject putOffenseStatusClosed(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO,
			String resolutionSummary) throws ResilientBrokerException;

	/**
	 * Create a new Note in QRadar offense.
	 * 
	 * @param noteText
	 *            offense note text
	 * @param qRadar
	 *            Instance of QRadar
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO. Containing application properties.
	 * @return offense object
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	public JsonObject postOffenseNote(String noteText, QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException;

	/**
	 * Fetch offense from QRadar based on parameter passed.
	 * 
	 * @param qRadar
	 *            Instance of QRadar
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO. Containing application properties.
	 * @return offense object
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	public JsonObject getOffense(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO) throws ResilientBrokerException;

	/**
	 * Fetch offense note from QRadar based on parameter passed.
	 * 
	 * @param qRadar
	 *            Instance of QRadar
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO. Containing application properties.
	 * @return offense object
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	public JsonArray getOffenseNotes(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException;

	/**
	 * Fetch all the offenses from QRadar application, which are greater than passed
	 * lastUpdatedTime last_updated_time time.
	 * 
	 * @param lastUpdatedTime
	 *            QRadar offense last_updated_time
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO. Containing application properties.
	 * @return Array of offenses found in QRadar grater last_updated_time than
	 *         passed last_updated_time
	 * @throws ResilientBrokerException
	 *             if any exception occurs
	 */
	public JsonArray getLatestUpdatedOffencesFromQRadar(Long lastUpdatedTime, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException;
}