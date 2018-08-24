package com.user.message.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Message wrapper for the POST body
 */
@ApiModel(value = "MessageBody", description = "Passed in when creating a new message")
public class MessageBodyFormatter {

	private String message;

	@ApiModelProperty(value = "The new message to be stored")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
