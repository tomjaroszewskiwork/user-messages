package com.user.message.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main method for that app, run this to starts
 */
// Spring boot auto configuration magic
@SpringBootApplication
@ComponentScan("com.user.message")
@EntityScan("com.user.message.store")
public class UserMessagesApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(UserMessagesApp.class, args);
	}
}
