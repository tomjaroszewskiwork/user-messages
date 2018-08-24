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

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Tests for bad message pulling
	 */
	@Test
	public void getMessageBadTest() {
		assertURLEquals(HttpMethod.GET, "/users/test/messages/1", 404);
		assertURLEquals(HttpMethod.GET, "/users/test/messages/notInteger", 400);
		assertURLEquals(HttpMethod.GET, "/users/test/messages/-1", 404);
		assertURLEquals(HttpMethod.GET, "/users/test/messages/page=-1", 400);
	}

	/**
	 * Tests pulling a message
	 */
	@Test
	public void getMessageTest() {
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages/100", 200, "message");
	}

	/**
	 * Tests for bad message list
	 */
	@Test
	public void getMessageListBadTest() {
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages/page=-1", 400);
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages/size=0", 400);
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages/size=101", 400);
	}

	/**
	 * Tests for pulling message list
	 */
	@Test
	public void getMessageListTest() {
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages/", 200, "all-messages");
		assertURLEquals(HttpMethod.GET, "/users/new.user/messages/", 200, "empty-list");
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages?page=100", 200, "empty-list");
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages?size=4", 200, "partial-list-start");
		assertURLEquals(HttpMethod.GET, "/users/tom.j/messages?size=1&page=5", 200, "partial-list-end");
	}

	/**
	 * Tests adding a message with no body
	 */
	@Test
	public void addMessageBadTest() throws Exception {
		assertURLEquals(HttpMethod.POST, "/users/test/messages/", 400);
		assertURLEquals(HttpMethod.POST, "/users/test/messages/", "", 400);
		MessageBodyFormatter badMessage = new MessageBodyFormatter();
		String body = mapper.writeValueAsString(badMessage);
		assertURLEquals(HttpMethod.POST, "/users/test/messages/", body, 400);
	}

	/**
	 * Tests adding a message
	 */
	@Test
	public void addMessageTest() throws Exception {
		MessageBodyFormatter newMessage = new MessageBodyFormatter();
		newMessage.setMessage("new test value!");
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
		assertURLEquals(HttpMethod.DELETE, "/users/bob.dole/messages/200", 204);
		assertURLEquals(HttpMethod.GET, "/users/bob.dole/messages/200", 404);
	}
}