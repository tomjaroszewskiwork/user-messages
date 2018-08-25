package com.user.message.api;

import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leandronunes85.etag.ETag;
import com.user.message.store.UserMessageEntity;
import com.user.message.store.UserMessageRepo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Handles storing and retrieving user messages
 */
@Produces(MediaType.APPLICATION_JSON)
@Component
@Path("v2/users/")
@Api(tags = "user message")
public class UserMessageApi {

	@Autowired
	UserMessageRepo messageRepo;

	/**
	 * Message size limit, twice as good as twitter
	 */
	public static final int MESSAGE_SIZE_LIMIT = 560;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/{messageId}")
	@ApiOperation(value = "Returns a users message", response = UserMessageEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Message not found") })
	public Response getMessage(
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "Message id", example = "3434523", required = true) @PathParam("messageId") final Long messageId) {
		UserMessageEntity message = getMessageEntity(userId, messageId);
		return Response.ok(message).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/")
	@ApiOperation(value = "Returns a paginated list of user messages, sorted by generation date", response = UserMessageList.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Message not found") })
	@ETag
	public Response getMessageList(
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "Which page", allowableValues = "range[1,infinity]") @DefaultValue("0") @QueryParam("page") final Integer page,
			@ApiParam(value = "Page size", allowableValues = "range[1,100]") @DefaultValue("50") @QueryParam("size") final Integer size) {

		// Validates page size and page limit
		if (page < 0) {
			throw new ClientErrorException("page must be a positive intger", Response.Status.BAD_REQUEST);
		}
		if (size < 0 || size > 100) {
			throw new ClientErrorException("size must be between 1 and 100", Response.Status.BAD_REQUEST);
		}

		// Gets one more extra entity to check if we have more messages
		List<UserMessageEntity> userMessages = messageRepo.getMessagesForUser(userId, page, size, 1);
		boolean hasMore = false;
		if (userMessages.size() == size + 1) {
			hasMore = true;
			userMessages = userMessages.subList(0, size);
		}

		return Response.ok(new UserMessageList(userMessages, hasMore)).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/{messageId}/fun-facts")
	@ApiOperation(value = "Returns a fun facts about a message", response = FunFactsFormatter.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Message not found") })
	public Response getFunFacts(
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "Message id", example = "3434523", required = true) @PathParam("messageId") final Long messageId) {
		UserMessageEntity message = getMessageEntity(userId, messageId);
		return Response.ok(new FunFactsFormatter(message)).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/")
	@ApiOperation(value = "Adds a message for the user", response = UserMessageEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Message too long or null") })
	public Response addMessage(@Context UriInfo uriInfo,
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "New message") MessageBodyFormatter newMessage) {

		// Validates the message
		if (newMessage == null || newMessage.getMessage() == null
				|| newMessage.getMessage().length() > MESSAGE_SIZE_LIMIT) {
			throw new ClientErrorException(
					"Message cannot be null or larger then " + MESSAGE_SIZE_LIMIT + " characters",
					Response.Status.BAD_REQUEST);
		}

		// Adds it to the database
		UserMessageEntity message = messageRepo.addMessage(userId, newMessage.getMessage());

		// Returns location
		Long messageId = message.getMessageId();
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		builder.path(Long.toString(messageId));
		return Response.created(builder.build()).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/{messageId}")
	@ApiOperation(value = "Deletes a users message", response = UserMessageEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Message not found") })
	public Response deleteMessage(
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "Message id", example = "3434523", required = true) @PathParam("messageId") final Long messageId) {

		UserMessageEntity message = messageRepo.deleteMessage(userId, messageId);
		if (message == null) {
			throw new ClientErrorException("No message found with id={" + messageId + "} for the given user",
					Response.Status.NOT_FOUND);
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}

	private UserMessageEntity getMessageEntity(String expectedUserId, Long messageId) {
		UserMessageEntity message = messageRepo.getMessage(expectedUserId, messageId);
		if (message == null) {
			throw new ClientErrorException("No message found with id={" + messageId + "} for the given user",
					Response.Status.NOT_FOUND);
		}
		return message;
	}
}