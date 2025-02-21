package com.home.project.ordernotifications.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

	private String orderId;
	private String status;
	private String recipient;

}
