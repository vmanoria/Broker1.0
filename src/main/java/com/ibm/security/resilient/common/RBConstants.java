package com.ibm.security.resilient.common;

import org.springframework.stereotype.Component;

/**
 * This is Resilient-Broker constants class.
 * 
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 29th, 2017
 *
 */
@Component
public class RBConstants {
	public static final String CHARSET_UTF8 = "UTF8";
	public static final String KEY_ID = "id";
	public static final String KEY_STATUS = "status";
	public static final String KEY_START_TIME = "start_time";
	public static final String KEY_NOTE_TEXT = "note_text";
	public static final String KEY_DOMAIN_ID = "domain_id";
	public static final String KEY_CLOSING_REASON_ID = "closing_reason_id";
	public static final String KEY_URL = "url";
	public static final String KEY_ORG_ID = "ORG_ID";
	public static final String KEY_INCIDENT_ID = "INCIDENT_ID";
	public static final String KEY_OFFENSE_STATUS_CLOSED = "CLOSED";
	public static final String KEY_OFFENSES_ID = "_OFFENSES_ID";
	public static final String KEY_REASON_ID = "_REASON_ID";
	public static final String KEY_QRADAR_NOTE_TEXT = "QRADAR_NOTE_TEXT";
	public static final String KEY_AUTHORIZATION = "Authorization";
	public static final String KEY_CONTENT_TYPE = "Content-Type";
	public static final String KEY_ACCEPT = "Accept";
	public static final String KEY_DB_OPEN = "OPEN";
	public static final String KEY_DB_CLOSE = "CLOSED";
	public static final String KEY_CODE = "code";
	public static final String KEY_INCIDENT_FIELD_PREFIX = "resilent.incident.field.prefix";
	public static final String KEY_INCIDENT_SUMMARY_INCIDENT = "resolution.summary.incident";
	public static final String KEY_NUMBER_OF_OFFENSE_FIELDS = "number.of.offense.fields";
	public static final String KEY_OFFENSE_FIELD = "offense.field.";
	public static final String KEY_RESILIENT_URL = "resilient.url";
	public static final String KEY_RESILIENT_EMAIL = "resilient.email";
	public static final String KEY_RESILIENT_PASSWORD = "resilient.password";
	public static final String KEY_RESILIENT_KEYSTORE_PASS_PHRASE = "resilient.keystore.pass.phrase";
	public static final String KEY_RESILENT_INCIDENT_DESCRIPTION_FIELD_COUNT = "resilent.incident.description.field.count";
	public static final String KEY_RESILENT_INCIDENT_DESCRIPTION_FORMAT = "resilent.incident.description.format";
	public static final String KEY_RESILENT_INCIDENT_DESCRIPTION = "resilent.incident.description.";
	public static final String KEY_RESILIENT_KEYSTORE_PATH = "resilient.keystore.path";
	public static final String KEY_OFFENSE_FIELD_NAME = "offense.field.name.";
	public static final String KEY_RESILENT_INCIDENT_NAME = "resilent.incident.name.";
	public static final String KEY_INCIDENT_NAME_FORMAT = "resilent.incident.name.format";
	public static final String KEY_INCIDENT_NAME_FIELD_COUNT = "resilent.incident.name.field.count";
	public static final String KEY_COUNT = ".count";
	public static final String KEY_ISARRAY = ".isarray";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_URL_RESILIENT_CREATE_INCIDENT = "create_incident";
	public static final String KEY_URL_RESILIENT_POST_INCIDENTS_NOTE = "post_incidents_note";
	public static final String KEY_URL_RESILIENT_GET_INCIDENTS = "get_incidents";
	public static final String KEY_URL_RESILIENT_GET_INCIDENTS_NOTES = "get_incidents_notes";
	public static final String KEY_URL_RESILIENT_PUT_INCIDENTS_UPDATE = "put_incidents_update";
	public static final String KEY_URL_RESILIENT_RESILIENTORGNAME = "resilientOrgName";
	public static final String KEY_URL_RESILIENT_RESILIENTEMAIL = "resilientEmail";
	public static final String KEY_URL_RESILIENT_RESILIENTPASSWORD_PLAIN = "resilientPassword_plain";
	public static final String KEY_URL_RESILIENT_RESILIENTKEYSTOREFILE = "resilientKeystoreFile";
	public static final String KEY_URL_RESILIENT_RESILIENTKEYSTOREPASSPHRASE_PLAIN = "resilientKeystorePassPhrase_plain";
	public static final String KEY_URL_RESILIENT_URLSTRING = "urlString";
	public static final String ERROR_MSG_NO_RECORD_FOUND = "No record found in Resilient-Broker database";
	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	public static final String KEY_BASIC = "Basic ";
	public static final String CHAR_COMMA = ",";
	public static final String CHAR_EMPTY_STRING = "";
	public static final String CHAR_DOUBLE_QUOTE = "\"";
	public static final String CHAR_NEW_LINE = "\\n";
	public static final String CHAR_DOT = ".";
	public static final String CHAR_COLON = ":";
	public static final String DEFAULT_ORG_FOR_CLOSED_OFFENCE = "ClosedOffenceFromDomainID-";
	public static final String DEFAULT_ORG_FOR_OPENED_OFFENCE = "UnknownOrg";
	public static final int DEFAULT_ORGID_FOR_OPENED_OFFENCE = -1;	
	public static final int DEFAULT_ORGID_FOR_CLOSED_OFFENCE = -100;	
	public static final String FAILED_TO_CLOSE_INCIDENT = "Failed to CLOSE incident. Exception message is: {}";
	
	/**
	 * Private constructor for RBConstants class
	 */
	private RBConstants(){
		//private RBConstants constructor
	}
}