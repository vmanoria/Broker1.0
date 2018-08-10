package com.ibm.security.resilient.exception;

/**
 * Resilient-Broker exception class
 * 
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 *
 */
public class ResilientBrokerException extends Exception {
	
	private static final long serialVersionUID = 9174016648129138071L;

	/**
	 * Custom ResilientBrokerException exception
	 * @param s
	 */
	public ResilientBrokerException(String msg) {
		super(msg);
	}
}