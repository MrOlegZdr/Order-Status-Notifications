package com.home.project.ordernotifications.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.home.project.ordernotifications.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

	List<Notification> findByOrderId(String orderId);

	Page<Notification> findByOrderId(String orderId, Pageable pageable);

	Page<Notification> findByStatusAndCreatedAtBetween(String status, LocalDateTime startDate, LocalDateTime endDate,
			Pageable pageable);

	List<Notification> findByIsSentFalse();
}
