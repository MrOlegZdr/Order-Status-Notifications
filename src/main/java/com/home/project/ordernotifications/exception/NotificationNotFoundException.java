package com.home.project.ordernotifications.exception;

public class NotificationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6589185341059920638L;

	public NotificationNotFoundException(String message) {
		super(message);
	}

	public NotificationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
