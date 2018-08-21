package com.user.message.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
	 * Setup API calls
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
	 * Does a strict comparison for the API output. Makes sure that the output is an
	 * exact match.
	 * 
	 * @param url
	 * @param expectedFile
	 */
	public void assertStrictURLEquals(String url, String expectedFile) {
		assertURLEquals(url, 200, HttpMethod.GET, expectedFile, JSONCompareMode.STRICT);
	}

	/**
	 * Calls the URL.
	 * 
	 * Compares the API output. Does a lose JSON comparison, extra fields in the
	 * return will not fail the test.
	 * 
	 * @param url
	 * @param expectedFile
	 */
	public void assertURLEquals(String url, String expectedFile) {
		assertURLEquals(url, 200, HttpMethod.GET, expectedFile, JSONCompareMode.STRICT_ORDER);
	}

	/**
	 * Calls the URL.
	 * 
	 * Tests the HTTP code of the API.
	 * 
	 * @param url
	 * @param expectedCode
	 */
	public void assertURLEquals(String url, int expectedCode) {
		assertURLEquals(url, expectedCode, HttpMethod.GET, null, JSONCompareMode.STRICT_ORDER);
	}

	/**
	 * Calls the URL.
	 * 
	 * Compares the API output. Does a lose JSON comparison, extra fields in the
	 * return will not fail the test.
	 * 
	 * @param url
	 * @param expectedFile
	 */
	public void assertHeaderEquals(String url, String expectedFile) {
		assertHeaderEquals(url, 200, HttpMethod.GET, expectedFile, JSONCompareMode.STRICT_ORDER);
	}

	/**
	 * @param expectedFile
	 * @param responeBody
	 */
	public void assertStrictResponseBodyEquals(String expectedFile, String responeBody) {
		assertResponseBodyEquals(expectedFile, responeBody, JSONCompareMode.STRICT);
	}

	/**
	 * @param expectedFile
	 * @param responeBody
	 */
	public void assertResponseBodyEquals(String expectedFile, String responeBody) {
		assertResponseBodyEquals(expectedFile, responeBody, JSONCompareMode.STRICT_ORDER);
	}

	/**
	 * Calls the URL
	 * 
	 * Checks the expected code, compares the expectedFile (skipped if null) and
	 * compares using the compareMode.
	 * 
	 * @param url
	 * @param expectedCode
	 * @param method
	 * @param expectedFile
	 * @param compareMode
	 * @return response entity
	 */
	public ResponseEntity<String> assertURLEquals(String url, int expectedCode, HttpMethod method, String expectedFile,
			JSONCompareMode compareMode) {

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
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

	/**
	 * Checks the expected code, compares the expectedFile (skipped if null) and
	 * compares using the compareMode.
	 * 
	 * @param url
	 * @param expectedCode
	 * @param method
	 * @param expectedFile
	 * @param compareMode
	 * @return response entity
	 */
	public ResponseEntity<String> assertHeaderEquals(String url, int expectedCode, HttpMethod method,
			String expectedFile, JSONCompareMode compareMode) {

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		String urlWithPort = "http://localhost:" + port + url;
		ResponseEntity<String> response = restTemplate.exchange(urlWithPort, method, entity, String.class);

		assertEquals(expectedCode, response.getStatusCode().value());

		HttpHeaders responseHttpHeader = response.getHeaders();

		assertNotNull(responseHttpHeader);

		if (expectedFile != null) {
			try {
				// Response Header
				JSONObject responseJsonHeader = new JSONObject(responseHttpHeader);
				responseJsonHeader.remove("Date"); // TODO
				String responseHeader = responseJsonHeader.toString();

				//
				JsonNode json = JsonFileReader.getJsonNode(fixtureFolder, expectedFile);
				assertNotNull("Missing JSON fixture file " + expectedFile + ".json", json);
				String expected = json.toString();

				assertNotNull(responseHeader);
				assertNotNull(expected);

				JSONAssert.assertEquals(expected, responseHeader, compareMode);
			} catch (Exception e) {
				org.junit.Assert.fail(e.getMessage());
			}
		}
		return response;
	}

	/**
	 * Compare body result
	 * 
	 * @param expectedFile
	 * @param responeBody
	 * @param compareMode
	 */
	public void assertResponseBodyEquals(String expectedFile, String responeBody, JSONCompareMode compareMode) {

		if (expectedFile != null) {
			try {
				JsonNode json = JsonFileReader.getJsonNode(fixtureFolder, expectedFile);
				assertNotNull("Missing JSON fixture file " + expectedFile + ".json", json);
				String expected = json.toString();
				JSONAssert.assertEquals(expected, responeBody, compareMode);
			} catch (Exception e) {
				org.junit.Assert.fail(e.getMessage());
			}
		}
	}

}