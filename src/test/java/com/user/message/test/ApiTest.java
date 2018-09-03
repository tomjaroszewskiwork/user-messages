package com.user.message.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
//import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.user.message.app.UserMessagesApp;

/**
 * Base class for API test cases
 */
@Ignore
@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest(classes = UserMessagesApp.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:test.properties")
public class ApiTest extends ApiCallTest {

	@LocalServerPort
	private int port;

	/**
	 * Sets up API for testing
	 */
	@Before
	public void setup() {
		// The JSON fixtures and test class name will match, except the Test suffix
		String fixtureFolder = getClass().getSimpleName();
		fixtureFolder = fixtureFolder.substring(0, fixtureFolder.length() - 4);
		setupCaller(port, fixtureFolder);
	}
}