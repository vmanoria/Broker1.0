package com.ibm.security.resilient.dao;

import java.util.Collection;
import java.util.List;

import com.ibm.security.resilient.model.QRadar;

/**
 * QRadarService service interface
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
public interface ResilientBrokerDAO {

	/**
	 * Fetch all QRadar from database
	 * 
	 * @return Collection of QRadar object
	 */
	Collection<QRadar> findAll();

	/**
	 * This method fetch list of all offense_id from database
	 * 
	 * @return Collection of Long
	 */
	Collection<Long> findAllOffenseIds();

	/**
	 * This method fetch list of last_updated_time from database
	 * 
	 * @return Collection of Long
	 */
	Collection<Long> findAllLastUpdatedTime();

	/**
	 * This method fetch QRadar object based on passed id from database
	 * 
	 * @return QRadar object
	 */
	QRadar findOne(Long id);

	/**
	 * This method fetch QRadar object based on passed offense id from database
	 * 
	 * @return QRadar object
	 */
	QRadar findOffense(Long id);

	/**
	 * This method fetch QRadar object based on passed incident id from database
	 * 
	 * @return QRadar object
	 */
	QRadar findIncident(Integer id);

	/**
	 * This method creates QRadar object in database
	 * 
	 * @return QRadar object
	 */
	/**
	 * This method fetches QRadar object based on passed offense id from database and updates incident details
	 * 
	 * @return QRadar object
	 */
	void updateOffense(QRadar qRadar);

	/**
	 * This method fetches QRadar object based on passed incident id from database and updates offense details
	 * 
	 * @return QRadar object
	 */
	int updateIncident(Long offId, Integer incidentId, Integer orgId, String orgName);

	/**
	 * This method creates QRadar object in database
	 * 
	 * @return QRadar object
	 */

	QRadar create(QRadar qRadar);

	/**
	 * This method updates QRadar object in database
	 * 
	 * @return QRadar object
	 */
	QRadar update(QRadar qRadar);

	/**
	 * This method deletes QRadar object in database, based on passed id
	 */
	void delete(Long id);

	void evictCache();

	/**
	 * Fetch all QRadar from database
	 * 
	 * @return Collection of QRadar object
	 */
	List<QRadar> findAllOpenOffenses();

	List<Long> findFailedOffenses();

	void deleteDummy(Long id);
}