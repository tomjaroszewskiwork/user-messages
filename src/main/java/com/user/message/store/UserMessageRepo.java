package com.user.message.store;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

/**
 * Handles storing and retrieving user messages from the database
 */
@Transactional
@Repository
public class UserMessageRepo {

	@PersistenceContext
	private EntityManager messageStore;

	/**
	 * Gets the user message. Returns null if not found or if message does not
	 * belong to the given user.
	 * 
	 * @param expectedUserId
	 * @param messageId
	 * @return users message
	 */
	public UserMessageEntity getMessage(String expectedUserId, Long messageId) {
		UserMessageEntity foundMessage = messageStore.find(UserMessageEntity.class, messageId);
		if (foundMessage != null && !foundMessage.getUserId().equals(expectedUserId)) {
			return null;
		}
		return foundMessage;
	}

	/**
	 * Gets a list user messages based on the size and offset, sorted by generation
	 * date. Page, size and buffer are used to pageinated the results.
	 * 
	 * @param userId
	 * @param page
	 * @param size
	 * @return list of user messages + one extra message if it exists
	 */
	public List<UserMessageEntity> getMessagesForUser(String userId, int page, int size) {
		int offsetStart = page * size - 1;
		TypedQuery<UserMessageEntity> query = messageStore.createQuery(
				"SELECT m FROM UserMessageEntity m WHERE m.userId = :userId ORDER BY m.generatedAt DESC",
				UserMessageEntity.class);
		query.setParameter("userId", userId);
		query.setFirstResult(offsetStart);
		query.setMaxResults(size + 1);
		return query.getResultList();
	}

	/**
	 * Adds a message to the store
	 * 
	 * @param userId
	 * @param messageString
	 * @return newly created message entity
	 */
	public UserMessageEntity addMessage(String userId, String messageString) {
		UserMessageEntity newMessage = new UserMessageEntity();
		newMessage.setMessage(messageString);
		newMessage.setGeneratedAt(new Date());
		newMessage.setUserId(userId);
		messageStore.persist(newMessage);
		messageStore.flush();
		return newMessage;
	}

	/**
	 * Deletes the message from the store. Before deletion makes sure that the
	 * message exists and it belongs to the given user.
	 * 
	 * @param expectedUserId
	 * @param messageId
	 * @return deleted message entity
	 */
	public UserMessageEntity deleteMessage(String expectedUserId, Long messageId) {
		UserMessageEntity messageToDelete = getMessage(expectedUserId, messageId);
		if (messageToDelete != null) {
			messageStore.remove(messageToDelete);
			messageStore.flush();
		}
		return messageToDelete;
	}

}
