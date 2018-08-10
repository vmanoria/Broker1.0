package com.ibm.security.resilient.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * QRadar entity class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@Entity
public class QRadar {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id") 
    private Long id;
    
  	@Column(name = "offense_id")
  	private Long offense_id;
  
  	@Column(name = "domain_id")
    private Integer domain_id;
    
    @Column(name = "domain_name")
    private String domain_name;
    
    @Column(name = "offense_status")
    private String offense_status;
    
    @Column(name = "offense_note_ids")
    private String offense_note_ids;
  	
  	@Column(name = "last_updated_time")
    private Long last_updated_time;
	
  	@Column(name = "incident_id")
    private Integer incident_id;
    
  	@Column(name = "incident_status")
    private String incident_status;
  	
  	@Column(name = "incident_note_ids")
  	private String incident_note_ids;
    
    @Column(name = "org_name")
    private String org_name;
    
    @Column(name = "org_id")
    private Integer org_id;
    
    @Column(name = "description")
    private String description;
    
    public QRadar() {
    	//default QRadar constructor.
    }

	public String getDescription() {
		return description;
	}

	public Integer getDomain_id() {
		return domain_id;
	}

	public String getDomain_name() {
		return domain_name;
	}

	public Long getId() {
		return id;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDomain_id(Integer domain_id) {
		this.domain_id = domain_id;
	}

	public void setDomain_name(String domain_name) {
		this.domain_name = domain_name;
	}

	public void setId(Long id) {
		this.id = id;
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
		return "QRadar [id=" + id + ", offense_id=" + offense_id + ", domain_id=" + domain_id + ", domain_name="
				+ domain_name + ", offense_status=" + offense_status + ", offense_note_ids=" + offense_note_ids
				+ ", last_updated_time=" + last_updated_time + ", incident_id=" + incident_id + ", incident_status="
				+ incident_status + ", incident_note_ids=" + incident_note_ids + ", org_name=" + org_name + ", org_id="
				+ org_id + ", description=" + description + "]";
	}
}