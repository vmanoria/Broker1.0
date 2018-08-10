package com.ibm.security.resilient.exception;

/**
 * Resilient-Broker custom exception class
 * 
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 *
 */
public class InvalidInputException extends Exception {
	
	private static final long serialVersionUID = 9174016648129138071L;
	
	/**
	 * Custom InvalidInputException exception
	 * @param s
	 */
	public InvalidInputException(String msg){
        super(msg);
    }
}