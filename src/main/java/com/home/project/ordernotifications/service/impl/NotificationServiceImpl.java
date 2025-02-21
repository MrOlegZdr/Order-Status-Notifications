package com.home.project.ordernotifications.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.project.ordernotifications.exception.ArgumentNotValidException;
import com.home.project.ordernotifications.exception.NotificationNotFoundException;
import com.home.project.ordernotifications.exception.NotificationProcessingException;
import com.home.project.ordernotifications.model.Notification;
import com.home.project.ordernotifications.model.OrderEvent;
import com.home.project.ordernotifications.repository.NotificationRepository;
import com.home.project.ordernotifications.service.MailService;
import com.home.project.ordernotifications.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final ObjectMapper objectMapper;
	private final MailService mailService;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private static final int MAX_RETRY_COUNT = 3;
	private final Map<String, Integer> retryCounts = new ConcurrentHashMap<String, Integer>();

	public NotificationServiceImpl(
			NotificationRepository notificationRepository,
			ObjectMapper objectMapper,
			MailService mailService,
			KafkaTemplate<String, String> kafkaTemplate) {
		this.notificationRepository = notificationRepository;
		this.objectMapper = objectMapper;
		this.mailService = mailService;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	@KafkaListener(topics = "order-status", groupId = "notification-group")
	@Transactional
	public void processNotification(String message) {
		try {
			System.out.println("Received Kafka message: " + message);
			// Parsing JSON
			OrderEvent event = objectMapper.readValue(message, OrderEvent.class);

			// Form the notification text
			String notificationMessage = String.format(
					"Your order %s is in status: %s", event.getOrderId(), event.getStatus());

			// Saving in DB
			Notification notification = new Notification(
					event.getOrderId(),
					event.getStatus(),
					event.getRecipient(),
					notificationMessage);
			notificationRepository.save(notification);

			// Send email
			mailService.sendMail(event.getRecipient(), "Order status changed", notificationMessage);

			notification.setSent(true);
			notificationRepository.save(notification);

			System.out.println("Notification processed and email sent: " + notification);
		} catch (Exception e) {
			System.err.println("Error processing notification: " + e.getMessage());

			// Send message to DLQ (Dead Letter Queue)
			kafkaTemplate.send("order-status-dlq", message);
			throw new NotificationProcessingException("Failed to process notification", e);
		}
	}

	@Override
	@KafkaListener(topics = "order-status-dlq", groupId = "notification-group-dlq")
	public void handleFailedNotifications(String message) {
		System.out.println("Processing failed notification (from DLQ): " + message);
		String messageId = extractMessageId(message);
		int retryCount = retryCounts.getOrDefault(messageId, 0);
		if (retryCount >= MAX_RETRY_COUNT) {
			System.err.println("Failed to process message from DLQ. Max retries reached for message: " + message);
			return;
		}

		try {
			processNotification(message);
			retryCounts.remove(messageId);
			System.out.println("Successfully reprocessed message from DLQ.");
		} catch (Exception e) {
			retryCounts.put(messageId, retryCount++);
			System.err.println("Failed to process message from DLQ. Attempt: " + retryCount);
		}
	}

	private String extractMessageId(String message) {
		try {
			JsonNode jsonNode = objectMapper.readTree(message);
			return jsonNode.get("orderId").asText();
		} catch (Exception e) {
			throw new ArgumentNotValidException("Failed to extract orderId from message: " + message, e);
		}
	}

	@Override
	@Transactional
	public void resendNotification(UUID notificationId) {

		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(
						() -> new NotificationNotFoundException("Notification not found with ID: " + notificationId));
		if (notification.isSent()) {
			System.out.println("Notification already sent, skipping resend: " + notificationId);
			return;
		}

		try {
			mailService.sendMail(notification.getRecipient(), "Order status changed", notification.getMessage());
			notification.setSent(true);
			notificationRepository.save(notification);
			System.out.println("Notification resent successfully: " + notificationId);
		} catch (Exception e) {
			System.err.println("Error resending notification: " + e.getMessage());
			throw new NotificationProcessingException("Failed to resend notification: " + notificationId, e);
		}

	}

	@Override
	@Transactional
	public void resendUnsentNotifications() {
		List<Notification> unsentNotifications = notificationRepository.findByIsSentFalse();
		for (Notification notification : unsentNotifications) {
			try {
				mailService.sendMail(notification.getRecipient(), "Order status changed", notification.getMessage());
				notification.setSent(true);
				notificationRepository.save(notification);
				System.out.println("Notification resent successfully: " + notification.getId());
			} catch (Exception e) {
				System.err.println("Error resending notification: " + e.getMessage());
				throw new NotificationProcessingException("Failed to resend notification: " + notification.getId(), e);
			}
		}
	}

	@Override
	public List<Notification> getNotificationsByOrderId(String orderId) {
		return notificationRepository.findByOrderId(orderId);
	}

	@Override
	public Page<Notification> getNotificationsByOrderId(String orderId, Pageable pageable) {
		return notificationRepository.findByOrderId(orderId, pageable);
	}

	@Override
	public Page<Notification> getNotificationsByStatusAndCreatedAtBetween(
			String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
		return notificationRepository.findByStatusAndCreatedAtBetween(status, startDate, endDate, pageable);
	}

}
