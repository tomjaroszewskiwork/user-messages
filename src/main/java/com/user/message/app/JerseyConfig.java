package com.user.message.app;

import javax.annotation.PostConstruct;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.leandronunes85.etag.responsefilter.ETagResponseFilter;
import com.user.message.api.UserMessageApi;
import com.user.message.exception.ApiExceptionMapper;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.util.Json;

/**
 * Jersey configuartion
 *
 */
@Component
public class JerseyConfig extends ResourceConfig {
	private BeanConfig config = new BeanConfig();

	/**
	 * @param objectMapper
	 */
	@Autowired
	public JerseyConfig(ObjectMapper objectMapper) {
		// Registers all of the API, otherwise wont be wired
		register(UserMessageApi.class);
		register(ApiExceptionMapper.class);
		register(ETagResponseFilter.class);
	}

	@PostConstruct
	public void init() {
		this.register(ApiListingResource.class);
		config = new BeanConfig();

		config.setVersion("v1");
		config.setContact("usermessagesupport@bestcompany.com");
		config.setSchemes(new String[] { "https" });
		config.setResourcePackage("com.user.message");
		config.setPrettyPrint(true);

		config.setConfigId("user-messages");
		config.setTitle("User Messages");
		config.setDescription("API for storing and retrieving messages for a user");
		config.setScan(true);

		Json.mapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
	}

	/**
	 * @return shared Jersey configuration
	 */
	public BeanConfig getConfig() {
		return this.config;
	}
}