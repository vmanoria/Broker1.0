/*package com.ibm.security.resilient.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestPropertySource(properties = {
		"resilient.password=InvalidResilientPassword",
})
public class ApplicationTests {

	private static Logger logger = LoggerFactory.getLogger(ApplicationTests.class);
	
	@Value("${resilient.password}")
	String resilientPassword;
	
	*//**
	 * no exception expected 
	 * @throws Exception 
	 * @throws BeansException 
	 *//*
	@Test(expected = Test.None.class)
	public void app_startup_works() throws BeansException, Exception {
		logger.info("Entering into testcase app_startup_works");
		String[] args = {""};
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        context.getBean(ApplicationStartup.class).init();
		logger.info("Exiting from testcase app_startup_works");
	}
	
	public void test() {
		//System.out.println("+++++++++++++++++++++++++ resilientPassword:" + resilientPassword);
	}
}*/