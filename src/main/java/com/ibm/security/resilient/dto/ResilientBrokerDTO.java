package com.ibm.security.resilient.dto;

/**
 * ResilientBrokerDTO bean class
 * 
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
public class ResilientBrokerDTO {
	// Resilient Server properties
	private String resilientUrl;
	private String resilientEmail;
	private String resilientPassword;
	private String resilientKeystorePath;
	private String resilientKeystorePassPhrase;
	private String resilientApiGetIncidents;
	private String resilientApiGetIncidentsNotes;
	private String resilientApiPutIncidentsUpdate;
	private String resilientApiPostIncidentsNote;
	private String resilientOrgName;
	// QRadar Server properties
	private String qradarApiOffensesFilterId;
	private String qradarApiOffensesFilterLastUpdatedTime;
	private String qradarApiOffensesSourceAddressId;
	private String qradarApiOffensesStatusUpdate;
	private String qradarApiPostOffensesNotes;
	private String qradarApiGetOffenses;
	private String qradarApiGetOffensesNotes;
	private String qradarUrl;
	private String qradarUser;
	private String qradarPassword;
	// Resilient Broker properties
	private String apiPostQradars;
	// Extra
	private String userFname;
	private String userLname;
	private Integer incId;
	private String resilientNewNote;

	private Boolean enableAutomaticEscalationCondition;

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

	public String getResilientNewNote() {
		return resilientNewNote;
	}

	public void setResilientNewNote(String resilientNewNote) {
		this.resilientNewNote = resilientNewNote;
	}

	public String getResilientOrgName() {
		return resilientOrgName;
	}

	public void setResilientOrgName(String resilientOrgName) {
		this.resilientOrgName = resilientOrgName;
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

	public String getUserFname() {
		return userFname;
	}

	public void setUserFname(String userFname) {
		this.userFname = userFname;
	}

	public String getUserLname() {
		return userLname;
	}

	public void setUserLname(String userLname) {
		this.userLname = userLname;
	}

	public Integer getIncId() {
		return incId;
	}

	public void setIncId(Integer incId) {
		this.incId = incId;
	}
}