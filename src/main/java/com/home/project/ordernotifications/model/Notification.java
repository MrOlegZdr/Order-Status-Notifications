package com.home.project.ordernotifications.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "notifications", schema = "order_notifications")
public class Notification {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private String orderId;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "recipient", nullable = false)
	private String recipient;

	@Column(name = "message", nullable = false, columnDefinition = "TEXT")
	private String message;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "is_sent", nullable = false)
	private boolean isSent;

	public Notification(String orderId, String status, String recipient, String message) {
		this.orderId = orderId;
		this.status = status;
		this.recipient = recipient;
		this.message = message;
	}
}
