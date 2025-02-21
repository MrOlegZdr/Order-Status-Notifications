package com.home.project.ordernotifications.service.impl;

import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.home.project.ordernotifications.exception.MailSendingException;
import com.home.project.ordernotifications.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

	public final JavaMailSender mailSender;

	public MailServiceImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	@Retryable(retryFor = { MessagingException.class, MailSendException.class },
			maxAttempts = 3,
			backoff = @Backoff(delay = 2000))
	public void sendMail(String to, String subject, String body) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);

			mailSender.send(message);
			System.out.println("Email sent to: " + to);

		} catch (MessagingException | MailSendException | ExhaustedRetryException e) {
			System.err.println("Error sending email: " + e.getMessage());
			throw new MailSendingException("Failed to send email", e);
		}
	}

}
