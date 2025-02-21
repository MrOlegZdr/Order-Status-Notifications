package com.home.project.ordernotifications.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.home.project.ordernotifications.model.Notification;

public interface NotificationService {

	void processNotification(String message);

	void handleFailedNotifications(String message);

	void resendNotification(UUID notificationId);

	void resendUnsentNotifications();

	List<Notification> getNotificationsByOrderId(String orderId);

	Page<Notification> getNotificationsByOrderId(String orderId, Pageable pageable);

	Page<Notification> getNotificationsByStatusAndCreatedAtBetween(
			String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
