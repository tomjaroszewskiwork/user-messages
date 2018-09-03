package com.user.message.api;

import java.util.List;

import com.user.message.store.UserMessageEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Wrapper around a lsit of user messages
 */
@ApiModel(value = "UserMessageList", description = "A list of user messages")
public class UserMessageList {

	private List<UserMessageEntity> messages;
	private boolean hasMore;

	public UserMessageList(List<UserMessageEntity> messages, boolean hasMore) {
		this.messages = messages;
		this.hasMore = hasMore;
	}

	@ApiModelProperty(value = "List of user messages")
	public List<UserMessageEntity> getMessages() {
		return messages;
	}

	@ApiModelProperty(value = "If there are more messages for the user to pull")
	public boolean getHasMore() {
		return hasMore;
	}
}
