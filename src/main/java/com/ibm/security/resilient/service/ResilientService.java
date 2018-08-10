package com.ibm.security.resilient.service;

import com.co3.dto.comment.json.CommentDTO;
import com.co3.dto.json.FullIncidentDataDTO;
import com.google.gson.JsonObject;
import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;
import com.ibm.security.resilient.model.QRadar;

/**
 * Resilient Service interface
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 17th, 2017
 */
public interface ResilientService {

	/**
	 * creates new Incident in respective Resilient organization and returns
	 * incident_id
	 * 
	 * @param offenseJsonObject
	 *            QRadar offense object
	 * @return resilientBrokerDTO ResilientBrokerDTO object
	 * @throws ResilientBrokerException
	 *             if fails to create new incident
	 */
	public FullIncidentDataDTO createIncident(JsonObject offenseJsonObject, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException;

	/**
	 * fetch incident from Resilient for given incident id and organization id
	 * 
	 * @param resilientBrokerDTO
	 *            ResilientBrokerDTO object
	 * @param domainId
	 *            domain Id of the organization
	 * @param incidentId
	 *            Resilient incident id
	 * @param orgId
	 *            orgId in incident
	 * @return FullIncidentDataDTO instance of FullIncidentDataDTO
	 * @throws ResilientBrokerException
	 *             if failed in fetching incident from Resilient
	 */
	public FullIncidentDataDTO getIncident(ResilientBrokerDTO resilientBrokerDTO, Integer domainId, Integer incidentId,
			Integer orgId) throws ResilientBrokerException;

	/**
	 * Fetch all comments/notes for provided incident details
	 * 
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO
	 * @param domainId
	 *            domainId in Resilient incident
	 * @param incidentId
	 *            incidentId in Resilient incident
	 * @param orgId
	 *            orgId in Resilient incident
	 * @return list of comments/notes for given incident details
	 * @throws ResilientBrokerException
	 *             if failed in fetching comments/notes incident
	 */
	public CommentDTO[] getIncidentNotes(ResilientBrokerDTO resilientBrokerDTO, Integer domainId, Integer incidentId,
			Integer orgId) throws ResilientBrokerException;

	/**
	 * Close Resilient incident
	 * 
	 * @param qRadar
	 *            Instance of QRadar
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO
	 * @param offenseClosingReasonId
	 *            Reason Id for closing incident
	 * @return instance of FullIncidentDataDTO class
	 * @throws ResilientBrokerException
	 *             if fail to close incident
	 */
	public FullIncidentDataDTO updateIncidentToClose(QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO,
			Integer offenseClosingReasonId) throws ResilientBrokerException;

	/**
	 * Create a new Resilient incident note.
	 * 
	 * @param noteText
	 *            incident note text
	 * @param qRadar
	 *            Instance of QRadar
	 * @param resilientBrokerDTO
	 *            Instance of ResilientBrokerDTO
	 * @return Instance of CommentDTO
	 * @throws ResilientBrokerException
	 *             if fail to create note/comment in incident
	 */
	public CommentDTO postIncidentNote(String noteText, QRadar qRadar, ResilientBrokerDTO resilientBrokerDTO)
			throws ResilientBrokerException;

}