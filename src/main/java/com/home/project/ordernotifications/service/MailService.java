package com.home.project.ordernotifications.service;

public interface MailService {

	void sendMail(String to, String subject, String body);

}
