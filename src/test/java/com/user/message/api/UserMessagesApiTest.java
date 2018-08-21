package com.user.message.api;

import org.junit.Test;

import com.user.message.test.ApiTest;

/**
 * Tests for {@link UserMessageApi}
 */
public class UserMessagesApiTest extends ApiTest {

	/**
	 * Tests errors for GET user messages
	 */
	@Test
	public void getMessageNotFoundTest() {
		assertURLEquals("/users/test/messages/1", 404);
	}
}
