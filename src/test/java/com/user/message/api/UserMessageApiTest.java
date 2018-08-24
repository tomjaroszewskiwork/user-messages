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
		assertURLEquals(HttpMethod.GET, "/v1/users/test/messages/1", 404);
		assertURLEquals(HttpMethod.GET, "/v1/users/test/messages/notInteger", 400);
		assertURLEquals(HttpMethod.GET, "/v1/users/test/messages/-1", 404);
		assertURLEquals(HttpMethod.GET, "/v1/users/test/messages/page=-1", 400);
		// Message does not belong to fun.dude
		assertURLEquals(HttpMethod.GET, "/v1/users/fun.dude/messages/100", 404);
	}

	/**
	 * Tests pulling a message
	 */
	@Test
	public void getMessageTest() {
		assertURLEquals(HttpMethod.GET, "/v1/users/tom.j/messages/100", 200, "message");
	}

	/**
	 * Tests for bad message list
	 */
	@Test
	public void getMessageListBadTest() {
		assertURLEquals(HttpMethod.GET, "/v1/users/tom.j/messages/page=-1", 400);
		assertURLEquals(HttpMethod.GET, "/v1/users/tom.j/messages/size=0", 400);
		assertURLEquals(HttpMethod.GET, "/v1/users/tom.j/messages/size=101", 400);
	}

	/**
	 * Tests for pulling message list
	 */
	@Test
	public void getMessageListTest() {
		assertURLEquals(HttpMethod.GET, "/v1/users/tom.j/messages/", 200, "all-messages");
		assertURLEquals(HttpMethod.GET, "/v1/users/new.user/messages/", 200, "empty-list");
		assertURLEquals(HttpMethod.GET, "/v1/users/tom.j/messages?page=100", 200, "empty-list");
		assertURLEquals(HttpMethod.GET, "/v1/users/tom.j/messages?size=4", 200, "partial-list-start");
		assertURLEquals(HttpMethod.GET, "/v1/users/tom.j/messages?size=1&page=5", 200, "partial-list-end");
	}

	/**
	 * Tests adding a message with no body
	 */
	@Test
	public void addMessageBadTest() throws Exception {
		assertURLEquals(HttpMethod.POST, "/v1/users/test/messages/", 400);
		assertURLEquals(HttpMethod.POST, "/v1/users/test/messages/", "", 400);
		MessageBodyFormatter badMessage = new MessageBodyFormatter();
		String body = mapper.writeValueAsString(badMessage);
		assertURLEquals(HttpMethod.POST, "/v1/users/test/messages/", body, 400);
	}

	/**
	 * Tests adding a message
	 */
	@Test
	public void addMessageTest() throws Exception {
		MessageBodyFormatter newMessage = new MessageBodyFormatter();
		newMessage.setMessage("new test value!");
		String body = mapper.writeValueAsString(newMessage);
		ResponseEntity<String> response = assertURLEquals(HttpMethod.POST, "/v1/users/test/messages/", body, 201);
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
		assertURLEquals(HttpMethod.DELETE, "/v1/users/test/messages/1", null, 404);
		// Message does not belong to fun.dude
		assertURLEquals(HttpMethod.DELETE, "/v1/users/fun.dude/messages/100", null, 404);
	}

	/**
	 * Tests deleting a message
	 */
	@Test
	public void deleteMessageTest() {
		assertURLEquals(HttpMethod.DELETE, "/v1/users/bob.dole/messages/200", 204);
		assertURLEquals(HttpMethod.GET, "/v1/users/bob.dole/messages/200", 404);
	}

	/**
	 * Tests for bad fun facts
	 */
	@Test
	public void getFunFactsBadTest() {
		assertURLEquals(HttpMethod.GET, "/v1/users/fun.dude/messages/1/fun-facts", 404);
		assertURLEquals(HttpMethod.GET, "/v1/users/fun.dude/messages/notInteger/fun-facts", 400);
		// Message does not belong to fun.dude
		assertURLEquals(HttpMethod.GET, "/v1/users/fun.dude/messages/100/fun-facts", 404);
	}

	/**
	 * Tests for getting fun facts
	 */
	@Test
	public void getFunFactsTest() {
		assertURLEquals(HttpMethod.GET, "/v1/users/fun.dude/messages/150/fun-facts", 200, "sad-facts");
		assertURLEquals(HttpMethod.GET, "/v1/users/fun.dude/messages/151/fun-facts", 200, "exciting-facts");
		assertURLEquals(HttpMethod.GET, "/v1/users/fun.dude/messages/152/fun-facts", 200, "palindrome-facts");
	}
}
