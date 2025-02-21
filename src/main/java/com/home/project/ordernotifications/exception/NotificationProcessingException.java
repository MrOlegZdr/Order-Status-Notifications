package com.home.project.ordernotifications.exception;

public class NotificationProcessingException extends RuntimeException {

	private static final long serialVersionUID = 5915896442312976245L;

	public NotificationProcessingException(String message) {
		super(message);
	}

	public NotificationProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}
