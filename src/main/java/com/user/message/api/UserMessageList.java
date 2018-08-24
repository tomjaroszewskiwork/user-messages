package com.user.message.api;

import java.util.List;

import com.user.message.store.UserMessageEntity;

public class UserMessageList {

	private List<UserMessageEntity> messages;
	private boolean hasMore;

	public UserMessageList(List<UserMessageEntity> messages, boolean hasMore) {
		this.messages = messages;
		this.hasMore = hasMore;
	}

	public List<UserMessageEntity> getMessages() {
		return messages;
	}

	public boolean getHasMore() {
		return hasMore;
	}
}
