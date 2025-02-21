package com.home.project.ordernotifications.controller;

import java.time.LocalDateTime;
//import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.home.project.ordernotifications.model.Notification;
import com.home.project.ordernotifications.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

//	@GetMapping("/order/{orderId}")
//	public ResponseEntity<List<Notification>> getNotificationsByOrderId(@PathVariable String orderId) {
//		List<Notification> notifications = notificationService.getNotificationsByOrderId(orderId);
//		return ResponseEntity.ok(notifications);
//	}

	@GetMapping("/order/{orderId}")
	public Page<Notification> getNotificationsByOrderId(@PathVariable String orderId, Pageable pageable) {
		return notificationService.getNotificationsByOrderId(orderId, pageable);
	}

	@PostMapping("/resend/{notificationId}")
	public ResponseEntity<String> resendNotification(@PathVariable UUID notificationId) {
		notificationService.resendNotification(notificationId);
		return ResponseEntity.ok("Notification resent successfully");
	}

	@PostMapping("/resend/unsent")
	public ResponseEntity<String> resendUnsentNotifications() {
		notificationService.resendUnsentNotifications();
		return ResponseEntity.ok("Unsent notifications resent successfully");
	}

	@GetMapping("/filter")
	public Page<Notification> filterNotifications(
			@RequestParam String status,
			@RequestParam LocalDateTime startDate,
			@RequestParam LocalDateTime endDate,
			Pageable pageable) {
		return notificationService.getNotificationsByStatusAndCreatedAtBetween(status, startDate, endDate, pageable);
	}
}
