package com.user.message.api;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.message.test.ApiTest;

/**
 * Tests for {@link UserMessageApi}
 */
public class UserMessageApiTest extends ApiTest {

	private class MessageBody {
		@SuppressWarnings("unused")
		public String message;
	}

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Tests message not found
	 */
	@Test
	public void getMessageNotFoundTest() {
		assertURLEquals(HttpMethod.GET, "/users/test/messages/1", 404);
	}

	/**
	 * Tests pulling a message
	 */
	@Test
	public void getMessageTest() {
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages/100", 200, "message");
	}

	/**
	 * Tests adding a message with no body
	 */
	@Test
	public void addMessageBadTest() throws Exception {
		assertURLEquals(HttpMethod.POST, "/users/test/messages/", 400);
		assertURLEquals(HttpMethod.POST, "/users/test/messages/", "", 400);
		MessageBody badMessage = new MessageBody();
		String body = mapper.writeValueAsString(badMessage);
		assertURLEquals(HttpMethod.POST, "/users/test/messages/", body, 400);
	}

	/**
	 * Tests adding a message
	 */
	@Test
	public void addMessageTest() throws Exception {
		MessageBody newMessage = new MessageBody();
		newMessage.message = "new test value!";
		String body = mapper.writeValueAsString(newMessage);
		ResponseEntity<String> response = assertURLEquals(HttpMethod.POST, "/users/test/messages/", body, 201);
		// Location header needs to point to newly created resource
		String newLocation = response.getHeaders().get("Location").get(0);
		// Strips out the front part of the URL
		newLocation = newLocation.substring("http://localhost:8080".length());
		assertURLEquals(HttpMethod.GET, newLocation, 200, "new");
	}

	/**
	 * Tests deleting a message that does not exists
	 */
	@Test
	public void deleteMessageNotFound() {
		assertURLEquals(HttpMethod.DELETE, "/users/test/messages/1", null, 404);
	}

	/**
	 * Tests deleting a message
	 */
	@Test
	public void deleteMessageTest() {
		assertURLEquals(HttpMethod.DELETE, "/users/tom.j/messages/100", 204);
		assertURLEquals(HttpMethod.GET, "/users/test/messages/100", 404);
	}
}
