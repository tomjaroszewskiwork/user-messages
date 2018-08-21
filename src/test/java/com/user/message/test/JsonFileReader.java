package com.user.message.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Read test case JSON fixture
 *
 */
public class JsonFileReader {

	private final static String JSON_EXTENSION = ".json";
	private final static String RESOURCE_DIR = "json-fixtures/";

	/**
	 * Reads in a JSON node object from a file that contains JSON.
	 * 
	 * @param subDir
	 * @param key
	 * 
	 * @return JSOn node read in from the file
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public static JsonNode getJsonNode(String subDir, String key) throws JsonProcessingException, IOException {
		String SubDir = subDir + "/";
		InputStream is = getStream(SubDir, key, JSON_EXTENSION);
		if (is == null) {
			return null;
		}
		Reader reader = new InputStreamReader(is);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(reader, JsonNode.class);
	}

	private static InputStream getStream(String SubDir, String key, String extension) {
		String name = RESOURCE_DIR + SubDir + key + extension;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		InputStream resourceStream = loader.getResourceAsStream(name);
		return resourceStream;
	}

}
