package com.home.project.ordernotifications.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.project.ordernotifications.exception.NotificationProcessingException;
import com.home.project.ordernotifications.model.Notification;
import com.home.project.ordernotifications.model.OrderEvent;
import com.home.project.ordernotifications.repository.NotificationRepository;
import com.home.project.ordernotifications.service.impl.NotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

	private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
	private final MailService mailService = mock(MailService.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	private final KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);

	private final NotificationServiceImpl notificationService = new NotificationServiceImpl(notificationRepository,
			objectMapper, mailService, kafkaTemplate);

	@Captor
	private ArgumentCaptor<Notification> notificationCaptor;

	@Test
	void testProcessNotification_Success() throws Exception {
		OrderEvent event = new OrderEvent("12345", "ORDER_SHIPPED", "user@example.com");
		String message = objectMapper.writeValueAsString(event);

		doNothing().when(mailService).sendMail(anyString(), anyString(), anyString());

		notificationService.processNotification(message);
		verify(notificationRepository).save(notificationCaptor.capture());

		Notification savedNotification = notificationCaptor.getValue();
		assertEquals("12345", savedNotification.getOrderId());
		assertEquals("ORDER_SHIPPED", savedNotification.getStatus());
		assertEquals("user@example.com", savedNotification.getRecipient());

		verify(mailService)
				.sendMail(eq("user@example.com"), eq("Order status changed"),
						eq("Your order 12345 is in status: ORDER_SHIPPED"));
		verify(kafkaTemplate, never()).send(anyString(), anyString());
	}

	@Test
	void testProcessNotification_Failure_DLQ() throws Exception {

//		OrderEvent event = new OrderEvent("12345", "ORDER_SHIPPED", "user@example.com");
//		String message = objectMapper.writeValueAsString(event);

		String message = "{\"orderId\":\"123\", \"status\":\"SHIPPED\", \"recipient\":\"user@example.com\"}";

		when(objectMapper.readValue(anyString(), OrderEvent.class)).thenThrow(new Exception("Parsing exception"));

//		NotificationProcessingException exception = assertThrows(NotificationProcessingException.class,
//				() -> notificationService.processNotification(message));

//		doThrow(new RuntimeException("Database error")).when(notificationRepository).save(any());

//		assertEquals("Failed to process notification", exception.getMessage());

		verify(kafkaTemplate).send(eq("order-status-dlq"), eq(message));
	}
}
