package com.home.project.ordernotifications.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.home.project.ordernotifications.model.Notification;

@DataJpaTest
public class NotificationRepositoryTest {

	@Autowired
	private NotificationRepository notificationRepository;

	@Test
	void testSaveNotification() {
		Notification notification = new Notification(
				"12345",
				"ORDER_SHIPPED",
				"user@example.com",
				"Your order has been shipped");
		Notification saved = notificationRepository.save(notification);

		assertThat(saved).isNotNull();
		assertThat(saved.getOrderId()).isEqualTo("12345");
		assertThat(saved.getStatus()).isEqualTo("ORDER_SHIPPED");

	}

}
