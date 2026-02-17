package com.alterna.store.store.orders.dto;

import com.alterna.store.store.orders.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderTransitionRequest {
	@NotNull
	private OrderStatus toStatus;
	private String reason;
}
