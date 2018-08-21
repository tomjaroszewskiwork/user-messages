package com.user.message.app;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.message.api.UserMessagesApi;
import com.user.message.exception.ApiExceptionMapper;

/**
 * Jersey configuartion
 *
 */
@Component
public class JerseyConfig extends ResourceConfig {

	/**
	 * @param objectMapper
	 */
	@Autowired
	public JerseyConfig(ObjectMapper objectMapper) {
		// Registers all of the API, otherwise wont be wired
		register(UserMessagesApi.class);
		register(ApiExceptionMapper.class);
	}
}