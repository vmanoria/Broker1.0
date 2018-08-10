package com.ibm.security.resilient.common.base64codec;

import javax.xml.bind.DatatypeConverter;

/**
 * JavaXmlImpl class for Base64 encoding.
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 1st, 2017
 */
public class JavaXmlImpl {
	public String encode(byte[] data) {
		return DatatypeConverter.printBase64Binary(data);
	}

	public byte[] decode(String base64) {
		return DatatypeConverter.parseBase64Binary(base64);
	}
}