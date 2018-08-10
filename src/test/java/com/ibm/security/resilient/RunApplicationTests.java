package com.ibm.security.resilient;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestPropertySource(properties = {
	"server.port=8081"
})
public class RunApplicationTests {

	private static Logger logger = LoggerFactory.getLogger(RunApplicationTests.class);
	
	@Value("${server.port}")
	String serverPort;
	
	/**
	 * 
	 * @throws BeansException
	 * @throws Exception
	 */
	/*@Test(expected = Test.None.class)
	public void app_run_app_works() throws BeansException, Exception {
		logger.info("***** Entering into testcase app_run_app_works");
		String[] args = {""};
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        context.getBean(ApplicationStartup.class).init();
        logger.info("***** Exiting from testcase app_run_app_works");
	}*/
	
	@Test
	public void test() {
		Assert.assertTrue(true);
	}
}







