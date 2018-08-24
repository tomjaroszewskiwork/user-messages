package com.user.message.api;

import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.user.message.test.ApiTest;

/**
 * Runs as part of the build so that any new swagger changes are automagically
 * captured the new swagger json
 */
public class SwaggerGenerator extends ApiTest {

	/**
	 * Generates swagger for the API
	 * 
	 * @throws IOException
	 */
	@Test
	public void generateSwagger() throws IOException {
		ResponseEntity<String> response = assertURLEquals(HttpMethod.GET, "swagger.json", 200);
		String respondBody = response.getBody();
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		Object json = mapper.readValue(respondBody, Object.class);
		String prettyJson = mapper.writeValueAsString(json);

		// Writes out the swagger
		PrintWriter out = new PrintWriter("./swagger.json");
		out.println(prettyJson);
		out.close();
	}

}