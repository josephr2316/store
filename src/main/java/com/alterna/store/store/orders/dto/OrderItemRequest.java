package com.alterna.store.store.orders.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemRequest {
	@NotNull
	private Long variantId;
	@NotNull
	@Positive
	private Integer quantity;
	@NotNull
	private BigDecimal unitPrice;
}
