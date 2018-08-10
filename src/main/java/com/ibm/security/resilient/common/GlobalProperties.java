package com.ibm.security.resilient.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Resilient-Broker global property class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@Component
@PropertySource("classpath:application.properties")
public class GlobalProperties {
	
	/**
	 * Resilient Server properties
	 */
	@Value("${resilient.url}")
    private String resilientUrl;
    @Value("${resilient.email}")
    private String resilientEmail;
    @Value("${resilient.password}")
    private String resilientPassword;
    @Value("${resilient.keystore.path}")
    private String resilientKeystorePath;
    @Value("${resilient.keystore.pass.phrase}")
    private String resilientKeystorePassPhrase;
    @Value("${resilient.api.get.incidents.orgid.id}")
    private String resilientApiGetIncidents;
    @Value("${resilient.api.get.incidents.notes}")
    private String resilientApiGetIncidentsNotes;
    @Value("${resilient.api.put.incidents.update}")
    private String resilientApiPutIncidentsUpdate;
    @Value("${resilient.api.post.incidents.note}")
    private String resilientApiPostIncidentsNote;
    
    /**
     * QRadar Server properties
     */
    @Value("${qradar.url}")
    private String qradarUrl;
    @Value("${qradar.api.offenses.filter.id}")
    private String qradarApiOffensesFilterId;
    @Value("${qradar.api.offenses.source.address.id}")
    private String qradarApiOffensesSourceAddressId;
    @Value("${qradar.api.offenses.status.update}")
    private String qradarApiOffensesStatusUpdate;
    @Value("${qradar.api.post.offenses.notes}")
    private String qradarApiPostOffensesNotes;
    @Value("${qradar.api.get.offenses}")
    private String qradarApiGetOffenses;
    @Value("${qradar.api.get.offenses.notes}")
    private String qradarApiGetOffensesNotes;
    @Value("${qradar.api.offenses.filter.last.updated.time}")
    private String qradarApiOffensesFilterLastUpdatedTime;
    
    @Value("${qradar.user}")
    private String qradarUser;
    @Value("${qradar.password}")
    private String qradarPassword;
    
    @Value("${enable.automatic.escalation.condition}")
    private Boolean enableAutomaticEscalationCondition;
    
    @Value("${resilient.broker.api.post.qradars}")
    private String apiPostQradars;

    @Value("${resilient.scheduler.incidents.offences.update.triggertime}")    
    private long incidentsOffencesTriggerTime;

    public String getQradarUrl() {
		return qradarUrl;
	}

	public void setQradarUrl(String qradarUrl) {
		this.qradarUrl = qradarUrl;
	}

	public String getResilientUrl() {
		return resilientUrl;
	}

	public void setResilientUrl(String resilientUrl) {
		this.resilientUrl = resilientUrl;
	}

	public String getResilientEmail() {
		return resilientEmail;
	}

	public void setResilientEmail(String resilientEmail) {
		this.resilientEmail = resilientEmail;
	}

	public String getResilientPassword() {
		return resilientPassword;
	}

	public void setResilientPassword(String resilientPassword) {
		this.resilientPassword = resilientPassword;
	}

	public String getResilientKeystorePath() {
		return resilientKeystorePath;
	}

	public void setResilientKeystorePath(String resilientKeystorePath) {
		this.resilientKeystorePath = resilientKeystorePath;
	}

	public String getResilientKeystorePassPhrase() {
		return resilientKeystorePassPhrase;
	}

	public void setResilientKeystorePassPhrase(String resilientKeystorePassPhrase) {
		this.resilientKeystorePassPhrase = resilientKeystorePassPhrase;
	}

	public String getResilientApiGetIncidents() {
		return resilientApiGetIncidents;
	}

	public void setResilientApiGetIncidents(String resilientApiGetIncidents) {
		this.resilientApiGetIncidents = resilientApiGetIncidents;
	}

	public String getQradarApiOffensesFilterId() {
		return qradarApiOffensesFilterId;
	}

	public void setQradarApiOffensesFilterId(String qradarApiOffensesFilterId) {
		this.qradarApiOffensesFilterId = qradarApiOffensesFilterId;
	}

	public String getQradarApiOffensesSourceAddressId() {
		return qradarApiOffensesSourceAddressId;
	}

	public void setQradarApiOffensesSourceAddressId(String qradarApiOffensesSourceAddressId) {
		this.qradarApiOffensesSourceAddressId = qradarApiOffensesSourceAddressId;
	}

	public String getQradarUser() {
		return qradarUser;
	}

	public void setQradarUser(String qradarUser) {
		this.qradarUser = qradarUser;
	}

	public String getQradarPassword() {
		return qradarPassword;
	}

	public void setQradarPassword(String qradarPassword) {
		this.qradarPassword = qradarPassword;
	}

	public String getApiPostQradars() {
		return apiPostQradars;
	}

	public void setApiPostQradars(String apiPostQradars) {
		this.apiPostQradars = apiPostQradars;
	}

	public String getQradarApiOffensesStatusUpdate() {
		return qradarApiOffensesStatusUpdate;
	}

	public void setQradarApiOffensesStatusUpdate(String qradarApiOffensesStatusUpdate) {
		this.qradarApiOffensesStatusUpdate = qradarApiOffensesStatusUpdate;
	}

	public String getResilientApiGetIncidentsNotes() {
		return resilientApiGetIncidentsNotes;
	}

	public void setResilientApiGetIncidentsNotes(String resilientApiGetIncidentsNotes) {
		this.resilientApiGetIncidentsNotes = resilientApiGetIncidentsNotes;
	}

	public String getQradarApiPostOffensesNotes() {
		return qradarApiPostOffensesNotes;
	}

	public void setQradarApiPostOffensesNotes(String qradarApiPostOffensesNotes) {
		this.qradarApiPostOffensesNotes = qradarApiPostOffensesNotes;
	}

	public String getQradarApiGetOffenses() {
		return qradarApiGetOffenses;
	}

	public void setQradarApiGetOffenses(String qradarApiGetOffenses) {
		this.qradarApiGetOffenses = qradarApiGetOffenses;
	}
	
	public String getResilientApiPutIncidentsUpdate() {
		return resilientApiPutIncidentsUpdate;
	}

	public void setResilientApiPutIncidentsUpdate(String resilientApiPutIncidentsUpdate) {
		this.resilientApiPutIncidentsUpdate = resilientApiPutIncidentsUpdate;
	}
	
	public String getQradarApiGetOffensesNotes() {
		return qradarApiGetOffensesNotes;
	}

	public void setQradarApiGetOffensesNotes(String qradarApiGetOffensesNotes) {
		this.qradarApiGetOffensesNotes = qradarApiGetOffensesNotes;
	}
	
	public String getResilientApiPostIncidentsNote() {
		return resilientApiPostIncidentsNote;
	}

	public void setResilientApiPostIncidentsNote(String resilientApiPostIncidentsNote) {
		this.resilientApiPostIncidentsNote = resilientApiPostIncidentsNote;
	}
	public Boolean getEnableAutomaticEscalationCondition() {
		return enableAutomaticEscalationCondition;
	}
	public void setEnableAutomaticEscalationCondition(Boolean enableAutomaticEscalationCondition) {
		this.enableAutomaticEscalationCondition = enableAutomaticEscalationCondition;
	}

	public String getQradarApiOffensesFilterLastUpdatedTime() {
		return qradarApiOffensesFilterLastUpdatedTime;
	}

	public void setQradarApiOffensesFilterLastUpdatedTime(String qradarApiOffensesFilterLastUpdatedTime) {
		this.qradarApiOffensesFilterLastUpdatedTime = qradarApiOffensesFilterLastUpdatedTime;
	}

	public long getIncidentsOffencesTriggerTime() {
		return incidentsOffencesTriggerTime;
	}

	public void setIncidentsOffencesTriggerTime(long triggerTimeIncidents) {
		this.incidentsOffencesTriggerTime = triggerTimeIncidents;
	}
}