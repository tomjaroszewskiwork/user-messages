package com.user.message.api;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
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
public class UserMessageApi {

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
		UserMessageEntity message = getMessageEntity(userId, messageId);
		return Response.ok(message).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/")
	// TODO response type needs to be array, should I add prev/next?
	@ApiOperation(value = "Returns a list of user messages, sorted by generation date", response = UserMessageEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Message not found") })
	public Response getMessageList(
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "Page", allowableValues = "range[1,]") @DefaultValue("0") @QueryParam("page") final Integer page,
			@ApiParam(value = "Page size", allowableValues = "range[1,100]") @DefaultValue("50") @QueryParam("size") final Integer size) {

		// Validates page size and page limit
		if (page < 0) {
			throw new ClientErrorException("page must be a positive intger", Response.Status.BAD_REQUEST);
		}
		if (size < 0 || size > 100) {
			throw new ClientErrorException("size must be between 1 and 100", Response.Status.BAD_REQUEST);
		}

		int offsetStart = page * size;
		int offsetEnd = (page + 1) * size;
		TypedQuery<UserMessageEntity> query = messageStore.createQuery(
				"SELECT m FROM UserMessageEntity m WHERE m.userId = :userId ORDER BY m.generatedAt DESC",
				UserMessageEntity.class);
		query.setParameter("userId", userId);
		query.setFirstResult(offsetStart);
		query.setMaxResults(offsetEnd);
		List<UserMessageEntity> userMessages = query.getResultList();

		return Response.ok(userMessages).build();
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
		UserMessageEntity message = new UserMessageEntity();
		message.setMessage(newMessage.getMessage());
		message.setGeneratedAt(new Date());
		message.setUserId(userId);
		messageStore.persist(message);
		messageStore.flush();

		// Returns location
		Long messageId = message.getMessageId();
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		builder.path(Long.toString(messageId));
		return Response.created(builder.build()).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{userId}/messages/{messageId}")
	@ApiOperation(value = "Removes the message", response = UserMessageEntity.class)
	@ApiResponses(value = { @ApiResponse(code = 404, message = "Message not found") })
	public Response deleteMessage(
			@ApiParam(value = "User id", example = "bob.dole", required = true) @PathParam("userId") final String userId,
			@ApiParam(value = "Message id", example = "3434523", required = true) @PathParam("messageId") final Long messageId) {

		UserMessageEntity message = getMessageEntity(userId, messageId);

		messageStore.remove(message);
		messageStore.flush();

		return Response.status(Response.Status.NO_CONTENT).build();
	}

	private UserMessageEntity getMessageEntity(String userId, Long messageId) {
		UserMessageEntity message = messageStore.find(UserMessageEntity.class, messageId);
		// Checks null or user not matching
		if (message == null || !message.getUserId().equals(userId)) {
			throw new ClientErrorException("No message found with id={" + messageId + "} for the given user",
					Response.Status.NOT_FOUND);
		}
		return message;
	}

}
