package com.alterna.store.store.orders.dto;

import com.alterna.store.store.orders.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class OrderHistoryResponse {
	private Long id;
	private OrderStatus fromStatus;
	private OrderStatus toStatus;
	private String reason;
	private String changedBy;
	private Instant changedAt;
}
