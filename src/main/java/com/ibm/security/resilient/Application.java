package com.ibm.security.resilient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Resilient-Broker application main class
 *
 * @author sushilve@in.ibm.com
 * @version 1.0.1
 * @since July 24th, 2017
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
@EnableScheduling
public class Application extends SpringBootServletInitializer {

	/**
	 * Resilient-Broker main method.
	 * 
	 * @param args
	 *            command line argument for Resilient-Broker app
	 * @throws Exception
	 *             if any exception occurs
	 */
	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		context.getBean(ApplicationInitializer.class).init();
	}

	@Bean
	public CacheManager cacheManager() {
		return new GuavaCacheManager("qRadar");
	}
}