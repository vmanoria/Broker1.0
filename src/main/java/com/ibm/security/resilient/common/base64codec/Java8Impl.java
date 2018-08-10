package com.ibm.security.resilient.common.base64codec;

/**
 * Java8Impl class for Base64 encoding.
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 1st, 2017
 */
public class Java8Impl {
	private final java.util.Base64.Decoder mDecoder = java.util.Base64.getDecoder();
    private final java.util.Base64.Encoder mEncoder = java.util.Base64.getEncoder();
 
    public String encode(byte[] data) {
        return mEncoder.encodeToString(data);
    }
 
    public byte[] decode(String base64) {
        return mDecoder.decode(base64);
    }
}
