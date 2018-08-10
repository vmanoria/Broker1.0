package com.ibm.security.resilient.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.security.resilient.model.QRadar;

/**
 * Resilient-Broker JPA repository class.
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@Repository
public interface QRadarRepository extends JpaRepository<QRadar, Long> {
	
	@Query("select q.offense_id from QRadar q ORDER BY q.offense_id DESC" )
    List<Long> findAllOffenseIds();
	
	@Query("select q.last_updated_time from QRadar q ORDER BY q.last_updated_time DESC" )
    List<Long> findAllLastUpdatedTime();
	
	@Query("from QRadar q WHERE q.offense_status = 'OPEN' AND q.incident_status = 'OPEN' AND q.incident_id > 0 ORDER BY q.offense_id DESC" )
    List<QRadar> findAllOpenOffenses();
	
	@Query("from QRadar q WHERE q.id = ?1")
	QRadar findOne(Long Id);
	
	@Query("from QRadar q WHERE q.offense_id = ?1")
	QRadar findOffense(Long offId);

	@Query("from QRadar q WHERE q.incident_id = ?1")
	QRadar findIncident(Integer incId);
	
	@Query("select q.offense_id from QRadar q where q.incident_id = -1 ORDER BY q.offense_id" )
	List<Long> findFailedOffenses();
	
	@Transactional
	@Modifying
	@Query ("update QRadar q set q.incident_id = :#{#qRadar.incident_id}, q.org_id = :#{#qRadar.org_id},q.description = :#{#qRadar.description}, q.incident_status = :#{#qRadar.incident_status}, q.org_name = :#{#qRadar.org_name} where q.offense_id = :#{#qRadar.offense_id}")
	void updateOffense(@Param("qRadar") QRadar qRadar); 
	
	@Transactional
	@Modifying
	@Query ("update QRadar q set q.incident_id = ?2, q.org_id = ?3, q.org_name = ?4 where q.offense_id = ?1")
	int updateIncident(Long offId, Integer incidentId, Integer orgId, String orgName);

	@Transactional
	@Modifying
	@Query ("delete from QRadar q where q.id = ?1")
	void delete(Long id);	
	
	@Transactional
	@Modifying
	@Query ("delete from QRadar q where q.incident_id = ?1")
	void deleteDummy(Long id);	

}