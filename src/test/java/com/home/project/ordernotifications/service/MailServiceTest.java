package com.home.project.ordernotifications.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.home.project.ordernotifications.exception.MailSendingException;
import com.home.project.ordernotifications.service.impl.MailServiceImpl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

//@ExtendWith(MockitoExtension.class)
//@EnableRetry
@SpringJUnitConfig
public class MailServiceTest {

	@Mock
	private JavaMailSender mailSender;

	@InjectMocks
	private MailServiceImpl mailService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mailService = new MailServiceImpl(mailSender);
	}

	@Test
	void testSendMail_Success() throws MessagingException {

		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

		mailService.sendMail("user@example.com", "Test Subject", "Test Body");

		verify(mailSender).send(mimeMessage);
	}

	@Test
	void testSendMail_RetryOnFailure_Success() throws Throwable {

		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

		AtomicInteger attempts = new AtomicInteger(0);

		RetryTemplate retryTemplate = RetryTemplate.builder()
				.maxAttempts(3)
				.fixedBackoff(2000)
				.build();

		retryTemplate.execute(new RetryCallback<Void, Throwable>() {
			@Override
			public Void doWithRetry(RetryContext context) throws MessagingException {
				if (attempts.getAndIncrement() == 0) {
					doThrow(new MailSendException("SMTP error 1"))
							.when(mailSender).send(mimeMessage);
				}
				mailService.sendMail("user@example.com", "Test Subject", "Test Body");
				return null;
			}
		});

//		doThrow(new MailSendException("SMTP error 1"))
//				.doThrow(new MailSendException("SMTP error 2"))
//				.when(mailSender).send(mimeMessage);
//
//		mailService.sendMail("user@example.com", "Test Subject", "Test Body");
//
		verify(mailSender, times(2)).send(mimeMessage);
	}

	@Test
	void testSendMail_FailsAfterRetries() throws Throwable {

		MimeMessage mimeMessage = mock(MimeMessage.class);
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

		RetryTemplate retryTemplate = RetryTemplate.builder()
				.maxAttempts(3)
				.fixedBackoff(2000)
				.build();

		assertThrows(MailSendingException.class, () -> {
			retryTemplate.execute(new RetryCallback<Void, Throwable>() {
				@Override
				public Void doWithRetry(RetryContext context) throws MessagingException {
					doThrow(new MailSendException("SMTP error")).when(mailSender).send(mimeMessage);
					mailService.sendMail("user@example.com", "Test Subject", "Test Body");
					return null;
				}
			});
		});

		verify(mailSender, times(3)).send(mimeMessage);
	}

//	private final JavaMailSender mailSender = mock(JavaMailSender.class);
//	private final MailService mailService = new MailServiceImpl(mailSender);
//
//	@Test
//	void testSendMail_RetryOnFailure() throws MessagingException {
//		MimeMessage mimeMessage = new JavaMailSenderImpl().createMimeMessage();
//		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//		doThrow(new MailSendException("SMTP error"))
//				.doThrow(new MailSendException("SMTP error"))
//				.doNothing()
//				.when(mailSender).send(any(MimeMessage.class));
//
//		mailService.sendMail("user@example.com", "Test Subject", "Test Body");
//
//		verify(mailSender, times(3)).send(any(MimeMessage.class));
//	}

}
