package com.ibm.security.resilient.dao.impl;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.security.resilient.dao.ResilientBrokerDAO;
import com.ibm.security.resilient.model.QRadar;
import com.ibm.security.resilient.repository.QRadarRepository;

/**
 * QRadar services implementation class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ResilientBrokerDAOImpl implements ResilientBrokerDAO {
	/**
	 * ResilientBrokerDAOImpl logger instance
	 */
	private Logger logger = LoggerFactory.getLogger(ResilientBrokerDAOImpl.class);
	/**
	 * QRadarRepository instance
	 */
	@Autowired
	QRadarRepository qRadarRepository;

	@Override
	public Collection<QRadar> findAll() {
		logger.info("Fetching all open records from Resilient-Broker database.");
		return qRadarRepository.findAll();
	}

	@Override
	public QRadar findOne(Long Id) {
		return qRadarRepository.findOne(Id);
	}

	@Override
	public QRadar findOffense(Long OffId) {
		return qRadarRepository.findOffense(OffId);
	}
	@Override
	public List<Long> findFailedOffenses() {
		return qRadarRepository.findFailedOffenses();
	}
	@Override
	public QRadar findIncident(Integer IncId) {
		return qRadarRepository.findIncident(IncId);
	}
	@Override
	public void updateOffense(QRadar qRadar) {
		 qRadarRepository.updateOffense(qRadar);
	}
	@Override
	public int updateIncident(Long offId, Integer incidentId, Integer orgId, String orgName){
		return qRadarRepository.updateIncident(offId, incidentId, orgId, orgName);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public QRadar create(QRadar qradar) {
		logger.info("Inserting new record into Resilient-Broker database. _incedent {} and _offense {}",
				qradar.getIncident_id(), qradar.getOffense_id());
		
		if (qradar.getId() != null) {
			return null;
		}
		return qRadarRepository.save(qradar);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public QRadar update(QRadar qRadar) {
		logger.info("Updating record into Resilient-Broker database. _incedent {} and _offense {}",
				qRadar.getIncident_id(), qRadar.getOffense_id());
		QRadar qRadar2 = findOne(qRadar.getId());
		
		if (qRadar2 == null) {
			return null;
		}
		qRadar2.setDescription(qRadar.getDescription());
		qRadar2.setIncident_status(qRadar.getIncident_status());
		qRadar2.setOffense_status(qRadar.getOffense_status());
		qRadar2.setIncident_note_ids(qRadar.getIncident_note_ids());
		qRadar2.setOffense_note_ids(qRadar.getOffense_note_ids());
		return qRadarRepository.save(qRadar2);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(Long id) {
		qRadarRepository.delete(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteDummy(Long id) {
		qRadarRepository.delete(id);
	}

	@Override
	public void evictCache() {
		//can be implemented cache
	}

	@Override
	public Collection<Long> findAllOffenseIds() {
		return qRadarRepository.findAllOffenseIds();
	}

	@Override
	public Collection<Long> findAllLastUpdatedTime() {
		return qRadarRepository.findAllLastUpdatedTime();
	}

	@Override
	public List<QRadar> findAllOpenOffenses() {
		return qRadarRepository.findAllOpenOffenses();
	}
}