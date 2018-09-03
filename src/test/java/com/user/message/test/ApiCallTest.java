package com.user.message.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Methods for calling the API and comparing the results
 */
public class ApiCallTest {
	private static final TestRestTemplate restTemplate = new TestRestTemplate();
	private int port;
	private String fixtureFolder;

	/**
	 * Setup API call for testing
	 * 
	 * @param port
	 * @param fixtureFolder
	 */
	public void setupCaller(int port, String fixtureFolder) {
		this.port = port;
		this.fixtureFolder = fixtureFolder;
	}

	/**
	 * Calls the URL.
	 * 
	 * Compares the API output response body to the expected file. Tests the HTTP status codes. 
	 * 
	 * Does a lose JSON comparison, extra fields in the return will not fail the test.
	 * 
	 * @param method 
	 * @param url
	 * @param expectedCode 
	 * @param expectedFile
	 * @return response from the api
	 */
	public ResponseEntity<String> assertURLEquals(HttpMethod method, String url, int expectedCode,
			String expectedFile) {
		return assertURLEquals(method, url, null, expectedCode, expectedFile, JSONCompareMode.STRICT_ORDER);
	}

	/**
	 * Calls the URL.
	 * 
	 * Tests the HTTP status code of the API. Useful for error testing.
	 * 
	 * @param method
	 * @param url
	 * @param expectedCode
	 * @return response from the api
	 */
	public ResponseEntity<String> assertURLEquals(HttpMethod method, String url, int expectedCode) {
		return assertURLEquals(method, url, null, expectedCode, null, JSONCompareMode.STRICT_ORDER);
	}

	/**
	 * Calls the URL with a body.
	 * 
	 * Tests the HTTP status code of the API. Useful for error testing.
	 * 
	 * @param method 
	 * @param url 
	 * @param body 
	 * @param expectedCode 
	 * @return response from the api
	 */
	public ResponseEntity<String> assertURLEquals(HttpMethod method, String url, String body, int expectedCode) {
		return assertURLEquals(method, url, body, expectedCode, null, JSONCompareMode.STRICT);
	}

	
	// Base method for calling the API and doing comparison tests
	private ResponseEntity<String> assertURLEquals(HttpMethod method, String url, String body, int expectedCode,
			String expectedFile, JSONCompareMode compareMode) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(body, headers);
		String urlWithPort = "http://localhost:" + port + url;
		ResponseEntity<String> response = restTemplate.exchange(urlWithPort, method, entity, String.class);
		assertEquals(expectedCode, response.getStatusCode().value());

		if (expectedFile != null) {
			try {
				JsonNode json = JsonFileReader.getJsonNode(fixtureFolder, expectedFile);
				assertNotNull("Missing JSON fixture file " + expectedFile + ".json", json);
				String expected = json.toString();
				String respondBody = response.getBody();
				JSONAssert.assertEquals(expected, respondBody, compareMode);
			} catch (Exception e) {
				org.junit.Assert.fail(e.getMessage());
			}
		}
		return response;
	}
}