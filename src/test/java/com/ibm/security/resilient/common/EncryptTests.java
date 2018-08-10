package com.ibm.security.resilient.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ibm.security.resilient.Application;
import com.ibm.security.resilient.exception.ResilientBrokerException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class EncryptTests {
	private static Logger logger = LoggerFactory.getLogger(EncryptTests.class);
	@Autowired
	AESEncryption aesEncryption;
	
	@Test
	public void class_compilation_works() throws BeansException, ResilientBrokerException {
		logger.info("Entering into testcase class_compilation_works");
		assertNotNull(aesEncryption);
		logger.info("Exiting from testcase class_compilation_works");
	}
	
	@Test(expected = Test.None.class)
	public void class_loads_works() throws BeansException, ResilientBrokerException {
		logger.info("Entering into testcase class_loads_works");
		assertNotNull(aesEncryption);
		logger.info("Exiting from testcase class_loads_works");
	}
	
	@Test
	public void encrypt_plain_text_works() throws ResilientBrokerException {
		logger.info("Entering into testcase encrypt_plain_text_works");
		String plain_txt = "hello-sushil";
		String encrypt_txt = aesEncryption.encrypt(plain_txt);
		assertEquals(plain_txt, aesEncryption.decrypt(encrypt_txt));
		logger.info("Exiting from testcase encrypt_plain_text_works");
	}
	
	@Test(expected = ResilientBrokerException.class)
	public void encrypt_with_null_plain_text_works() throws ResilientBrokerException {
		logger.info("Entering into testcase encrypt_with_null_plain_text_works");
		String plain_txt = null;
		aesEncryption.encrypt(plain_txt);
		logger.info("Exiting from testcase encrypt_with_null_plain_text_works");
	}
	
	@Test
	public void encrypt_with_empty_plain_text_works() throws ResilientBrokerException {
		logger.info("Entering into testcase encrypt_with_empty_plain_text_works");
		String plain_txt = "";
		String encrypt_txt = aesEncryption.encrypt(plain_txt);
		assertEquals(plain_txt, aesEncryption.decrypt(encrypt_txt));
		logger.info("Exiting from testcase encrypt_with_empty_plain_text_works");
	}
	
	@Test
	public void encrypt_with_special_charater_plain_text_works() throws ResilientBrokerException {
		logger.info("Entering into testcase encrypt_with_special_charater_plain_text_works");
		String plain_txt = "@#Passw0rd&!)";
		String encrypt_txt = aesEncryption.encrypt(plain_txt);
		assertEquals(plain_txt, aesEncryption.decrypt(encrypt_txt));
		logger.info("Exiting from testcase encrypt_with_special_charater_plain_text_works");
	}
}