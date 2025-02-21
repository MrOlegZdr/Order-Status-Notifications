package com.home.project.ordernotifications.exception;

public class MailSendingException extends RuntimeException {

	private static final long serialVersionUID = 3173689424997166275L;

	public MailSendingException(String message) {
		super(message);
	}

	public MailSendingException(String message, Throwable cause) {
		super(message, cause);
	}

}
