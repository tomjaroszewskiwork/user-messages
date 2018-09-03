package com.user.message.api;

import com.user.message.store.UserMessageEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Formatter for fun facts, like if the message is a palindrome
 */
@ApiModel(value = "FunFacts", description = "Fun facts about a users message")
public class FunFactsFormatter {

	private String message;

	/**
	 * Constructor
	 * @param message entity
	 */
	public FunFactsFormatter(UserMessageEntity message) {
		this.message = message.getMessage();
	}

	@ApiModelProperty(value = "Is the message an paldindrome")
	public boolean isPalindrome() {
		int n = message.length();
		for (int i = 0; i < n / 2; ++i) {
			if (message.charAt(i) != message.charAt(n - i - 1))
				return false;
		}
		return true;
	}

	@ApiModelProperty(value = "Does the message have exciting content")
	public boolean isExciting() {
		return message.indexOf("!") > -1 || message.equals(message.toUpperCase());
	}

	@ApiModelProperty(value = "Does the message have sad content")
	public boolean isSad() {
		return message.indexOf(":(") > -1 || message.indexOf(":-(") > -1;
	}

}
