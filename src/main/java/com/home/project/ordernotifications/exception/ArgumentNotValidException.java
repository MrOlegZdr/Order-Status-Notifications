package com.home.project.ordernotifications.exception;

public class ArgumentNotValidException extends RuntimeException {

	private static final long serialVersionUID = -2953095930838359330L;

	public ArgumentNotValidException(String message) {
		super(message);
	}

	public ArgumentNotValidException(String message, Throwable cause) {
		super(message, cause);
	}
}
