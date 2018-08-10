package com.ibm.security.resilient.common.base64codec;

/**
 * ApacheImpl class for Base64 encoding.
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 1st, 2017
 */
public class ApacheImpl {

	public String encode(byte[] data) {
        return org.apache.commons.codec.binary.Base64.encodeBase64String( data );
    }
 
    public byte[] decode(String base64) {
        return org.apache.commons.codec.binary.Base64.decodeBase64( base64 );
    }
}