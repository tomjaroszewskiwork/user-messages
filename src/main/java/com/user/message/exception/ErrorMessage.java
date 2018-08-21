package com.user.message.exception;

import java.util.Date;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

/**
 * Serialization of error messages that the API can throw
 */
public class ErrorMessage {

	/** contains the same HTTP Status code returned by the server */
	private int statusCode;

	/** message describing the error */
	private String message;

	private Date now = new Date();

	/**
	 * @return HTTP status code
	 */
	public int getStatusCode() {
		return this.statusCode;
	}

	/**
	 * @return Error message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return get the time of when the error was generated
	 */
	public Date getTime() {
		return now;
	}

	/**
	 * @param ex
	 */
	public ErrorMessage(ClientErrorException ex) {
		this.statusCode = ex.getResponse().getStatus();
		this.message = ex.getMessage();
	}

	/**
	 * @param status
	 * @param message
	 */
	public ErrorMessage(Response.Status status, String message) {
		this.statusCode = status.getStatusCode();
		this.message = message;
	}
}