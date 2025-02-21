package com.home.project.ordernotifications.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrResponse {
	private int status;
	private String message;
}
