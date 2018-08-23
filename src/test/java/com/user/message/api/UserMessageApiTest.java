package com.user.message.api;

import org.junit.Test;
import org.springframework.http.HttpMethod;

import com.user.message.test.ApiTest;

/**
 * Tests for {@link UserMessageApi}
 */
public class UserMessageApiTest extends ApiTest {

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
		assertURLEquals(HttpMethod.GET, "/users/test/messages/100", 404);
	}
	
//	/**
//	 * Tests adding a message with no body
//	 */
//	@Test
//	public void addMessageBadTest() {
//		
//	}
//	
//	/**
//	 * Tests adding a message
//	 */
//	@Test
//	public void addMessageTest() {
//		
//	}
	
	/**
	 * Tests deleting a message that does not exists
	 */
	@Test
	public void deleteMessageNotFound() {
		assertURLEquals(HttpMethod.DELETE, "/users/test/messages/1", 404);
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
