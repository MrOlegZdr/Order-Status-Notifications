package com.home.project.ordernotifications.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrResponse> handleGeneralException(Exception e) {
		ErrResponse errResponse = new ErrResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Unexpected error: " + e.getMessage());
		return new ResponseEntity<ErrResponse>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MailSendingException.class)
	public ResponseEntity<ErrResponse> handleMailSendingException(MailSendingException e) {
		ErrResponse errResponse = new ErrResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Mail sending failed: " + e.getMessage());
		return new ResponseEntity<ErrResponse>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NotificationProcessingException.class)
	public ResponseEntity<ErrResponse> handleNotificationProcessingException(NotificationProcessingException e) {
		ErrResponse errResponse = new ErrResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Notification processing failed: " + e.getMessage());
		return new ResponseEntity<ErrResponse>(errResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NotificationNotFoundException.class)
	public ResponseEntity<ErrResponse> handleNotificationNotFound(NotificationNotFoundException e) {
		ErrResponse errResponse = new ErrResponse(HttpStatus.NOT_FOUND.value(),
				"Notification not found: " + e.getMessage());
		return new ResponseEntity<>(errResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ArgumentNotValidException.class)
	public ResponseEntity<ErrResponse> handleValidationError(ArgumentNotValidException e) {
		ErrResponse errResponse = new ErrResponse(HttpStatus.BAD_REQUEST.value(),
				"Validation error: " + e.getMessage());
		return new ResponseEntity<>(errResponse, HttpStatus.BAD_REQUEST);
	}
}
