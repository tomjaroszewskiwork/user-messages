package com.user.message.api;

import com.user.message.entity.UserMessageEntity;

/**
 * Formatter for fun facts, like if the message is a palindrome
 */
public class FunFactsFormatter {

	private String message;

	public FunFactsFormatter(UserMessageEntity message) {
		this.message = message.getMessage();
	}

	public boolean isPalindrome() {
		int n = message.length();
		for (int i = 0; i < n / 2; ++i) {
			if (message.charAt(i) != message.charAt(n - i - 1))
				return false;
		}
		return true;
	}

	public boolean isExciting() {
		return message.indexOf("!") > -1 || message.equals(message.toUpperCase());
	}

	public boolean isSad() {
		return message.indexOf(":(") > -1 || message.indexOf(":-(") > -1;
	}

}
