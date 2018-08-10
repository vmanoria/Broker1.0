package com.ibm.security.resilient.common;

import com.ibm.security.resilient.model.QRadar;

/**
 * QRadar entity class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
public class TransactionLog {
    
	private Long dbId;
    
  	private Long offense_id;
  
    private Integer domain_id;
    
    private String domain_name;
    
    private String offense_status;
    
    private String offense_note_ids;
  	
    private Long last_updated_time;
    
    private String offense_description;
	
    private Integer incident_id;
    
    private String incident_status;
  	
  	private String incident_note_ids;
    
    private String org_name;
    
    private Integer org_id;
    
    private String incident_name;
    
    private String incident_description;
    
    private String transaction_status;
    
    private long timetaken;

    public TransactionLog() {
    	//default TransactionLog constructor.
    }

    public void setTransactionLog(QRadar q) {
    	//default TransactionLog constructor.
    	dbId=q.getId();
    	offense_id=q.getOffense_id();
    	domain_id=q.getDomain_id();
    	domain_name = q.getDomain_name();
    	offense_status=q.getOffense_status();
        offense_note_ids=q.getOffense_note_ids();
      	last_updated_time=q.getLast_updated_time();
        offense_description=q.getDescription();
    	incident_id=q.getIncident_id();
        incident_status=q.getIncident_status();
      	incident_note_ids=q.getIncident_note_ids();
        org_name=q.getOrg_name();
        org_id=q.getOrg_id();
    }
    
    public void addTransaction(QRadar q, String status, int tt ){
    	this.setTransactionLog(q);
    	transaction_status=status;
    	timetaken=tt;
    }
    
    
	public Integer getDomain_id() {
		return domain_id;
	}

	public String getDomain_name() {
		return domain_name;
	}

	public Long getDbId() {
		return dbId;
	}

	public Integer getIncident_id() {
		return incident_id;
	}

	public String getIncident_note_ids() {
		return incident_note_ids;
	}

	public String getIncident_status() {
		return incident_status;
	}

	public Long getLast_updated_time() {
		return last_updated_time;
	}

	public Long getOffense_id() {
		return offense_id;
	}

	public String getOffense_note_ids() {
		return offense_note_ids;
	}

	public String getOffense_status() {
		return offense_status;
	}

	public Integer getOrg_id() {
		return org_id;
	}

	public String getOrg_name() {
		return org_name;
	}

	public void setDomain_id(Integer domain_id) {
		this.domain_id = domain_id;
	}

	public void setDomain_name(String domain_name) {
		this.domain_name = domain_name;
	}

	public void setDbId(Long id) {
		this.dbId = id;
	}

	public void setIncident_id(Integer incident_id) {
		this.incident_id = incident_id;
	}

	public void setIncident_note_ids(String incident_note_ids) {
		this.incident_note_ids = incident_note_ids;
	}

	public void setIncident_status(String incident_status) {
		this.incident_status = incident_status;
	}

	public void setLast_updated_time(Long last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	public void setOffense_id(Long offense_id) {
		this.offense_id = offense_id;
	}

	public void setOffense_note_ids(String offense_note_ids) {
		this.offense_note_ids = offense_note_ids;
	}

	public void setOffense_status(String offense_status) {
		this.offense_status = offense_status;
	}

	public void setOrg_id(Integer org_id) {
		this.org_id = org_id;
	}

	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}

	@Override
	public String toString() {
		return "[DB id=" + dbId + ", offense_id=" + offense_id + ", domain_id=" + domain_id + ", domain_name="
				+ domain_name + ",\n offense_status=" + offense_status + ", offense_note_ids=" + offense_note_ids
				+ ", last_updated_time=" + last_updated_time + ", incident_id=" + incident_id + ",\n incident_status="
				+ incident_status + ", incident_note_ids=" + incident_note_ids + ", org_name=" + org_name + ", org_id="
				+ org_id + ", \nOffence_description=" + offense_description + ", \nIncident_description= "+ incident_description +
				",\n Incident_name= "+ incident_name + ", \nTransaction_status="+ transaction_status+ ", Timetaken= "+ timetaken+"]";
	}

	public String printFacts() {
		return "DB id=" + dbId + ", offense_id=" + offense_id + ", domain_id=" + domain_id + ", incident_id=" + incident_id + ", org_id="
				+ org_id + ", Transaction_status="+ transaction_status+ ", Timetaken(millisec)= "+ timetaken;
	}

	public String getOffense_description() {
		return offense_description;
	}

	public void setOffense_description(String offense_description) {
		this.offense_description = offense_description;
	}

	public String getIncident_description() {
		return incident_description;
	}

	public void setIncident_description(String incident_description) {
		this.incident_description = incident_description;
	}

	public String getTransaction_status() {
		return transaction_status;
	}

	public void setTransaction_status(String transaction_status) {
		this.transaction_status = transaction_status;
	}

	public long getTimetaken() {
		return timetaken;
	}

	public void setTimetaken(long timetaken) {
		this.timetaken = timetaken;
	}

	public String getIncident_name() {
		return incident_name;
	}

	public void setIncident_name(String incident_name) {
		this.incident_name = incident_name;
	}
}