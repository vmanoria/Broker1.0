package com.ibm.security.resilient.common;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This is QRadarService utility class. Contains QRadar related functions.
 * 
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since August 29th, 2017
 *
 */
@Component
public class QRadarUtils {
	private Logger logger = LoggerFactory.getLogger(QRadarUtils.class);

	/**
	 * Get object of CloseableHttpClient class with SSL connection Socket, for
	 * executing HTTPS URLs.
	 * 
	 * @return object of CloseableHttpClient class
	 * @throws NoSuchAlgorithmException
	 *             if NoSuchAlgorithmException exception occurs
	 * @throws KeyStoreException
	 *             if KeyStoreException exception occurs
	 * @throws KeyManagementException
	 *             if KeyManagementException exception occurs
	 */
	public CloseableHttpClient getCloseableHttpClient()
			throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		logger.debug("Creating CloseableHttpClient for HTTPS api call.");
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(),
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		/**
		 * CloseableHttpClient client =
		 * HttpClients.custom().setSSLSocketFactory(sslsf).build();
		 */
		return HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}
}