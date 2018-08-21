package com.user.message.api;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Component;

import com.user.message.entity.UserMessageEntity;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Handles user messages
 */
@Transactional
@Produces(MediaType.APPLICATION_JSON)
@Component
@Path("users/")
public class UserMessagesApi {
	@PersistenceContext
	private EntityManager messageStore;

	private final int MESSAGE_SIZE_LIMIT = 2000;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/{messageId}")
	@ApiOperation(value = "Returns a users message", response = UserMessageEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Message not found") })
	public Response getMessage(
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "Message id", example = "3434523", required = true) @PathParam("messageId") final Long messageId) {

		UserMessageEntity message = messageStore.find(UserMessageEntity.class, messageId);
		// Checks null or user not matching
		if (message == null || !message.getUserId().equals(userId)) {
			throw new ClientErrorException("No message found with id={" + messageId + "} for the given user",
					Response.Status.NOT_FOUND);
		}
		return Response.ok(message).build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/")
	@ApiOperation(value = "Adds a message for the user", response = UserMessageEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Message too long or null") })
	public Response addMessage(
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "New message") MessageFormatter newMessage) {
		// Validates the message
		if (newMessage.getMessage() == null || newMessage.getMessage().length() > MESSAGE_SIZE_LIMIT) {
			throw new ClientErrorException(
					"Message cannot be null or larger then " + MESSAGE_SIZE_LIMIT + " characters",
					Response.Status.BAD_REQUEST);
		}
		UserMessageEntity message = new UserMessageEntity();
		message.setMessage(newMessage.getMessage());
		message.setGeneratedAt(new Date());
		message.setUserId(userId);
		messageStore.persist(message);
		messageStore.flush();
		return Response.ok(message).build();
	}

}
