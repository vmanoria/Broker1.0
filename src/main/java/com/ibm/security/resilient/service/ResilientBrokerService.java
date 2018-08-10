package com.ibm.security.resilient.service;

import java.io.PrintWriter;

import com.ibm.security.resilient.dto.ResilientBrokerDTO;
import com.ibm.security.resilient.exception.ResilientBrokerException;

/**
 * Resilient-Broker application services interface
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 10th, 2017
 */
public interface ResilientBrokerService {

	/**
	 * Creates incidents in Resilient application for newly created
	 * offenses in QRadar
	 * 
	 * @param resilientBrokerDTO
	 *            is Resilient-Broker properties object
	 * @param out 
	 * @return 
	 * @throws ResilientBrokerException
	 *             if incident creation fails
	 */
	public void createNewIncedents(ResilientBrokerDTO resilientBrokerDTO, PrintWriter out) throws ResilientBrokerException;

	/**
	 * Updates "Status" and "Comments" from Resilient to QRadar
	 * 
	 * @param resilientBrokerDTO
	 *            is Resilient-Broker properties object
	 * @throws ResilientBrokerException
	 *             if Status update or adding comment fails
	 */
	public void updateStatusAndNotesFromResilient(ResilientBrokerDTO resilientBrokerDTO, PrintWriter out) throws ResilientBrokerException;

	/**
	 * Updates "Status" and "Comments" from QRadar to Resilient
	 * 
	 * @param resilientBrokerDTO
	 *            is Resilient-Broker properties object
	 * @throws ResilientBrokerException
	 *             if Status update or adding comment fails
	 */
	public void updateStatusAndNotesFromQRadar(ResilientBrokerDTO resilientBrokerDTO, PrintWriter out) throws ResilientBrokerException;
}