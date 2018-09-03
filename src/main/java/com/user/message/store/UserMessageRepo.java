package com.user.message.store;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

@Transactional
@Repository
public class UserMessageRepo {

	@PersistenceContext
	private EntityManager messageStore;

	/**
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
	 * date
	 * 
	 * @param userId
	 * @param page
	 * @param buffer
	 * @return list of user messages
	 */
	public List<UserMessageEntity> getMessagesForUser(String userId, int page, int size, int buffer) {
		int offsetStart = page * size;
		int offsetEnd = (page + 1) * size + buffer;
		TypedQuery<UserMessageEntity> query = messageStore.createQuery(
				"SELECT m FROM UserMessageEntity m WHERE m.userId = :userId ORDER BY m.generatedAt DESC",
				UserMessageEntity.class);
		query.setParameter("userId", userId);
		query.setFirstResult(offsetStart);
		query.setMaxResults(offsetEnd);
		return query.getResultList();
	}

	/**
	 * Adds a message to the store
	 * 
	 * @param userId
	 * @param messageString
	 * @return
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
	 * Removes the message from the store
	 * 
	 * @param messageId
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
