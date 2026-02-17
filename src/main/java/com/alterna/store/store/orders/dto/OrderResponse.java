package com.alterna.store.store.orders.dto;

import com.alterna.store.store.orders.enums.OrderChannel;
import com.alterna.store.store.orders.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class OrderResponse {
	private Long id;
	private String externalId;
	private OrderChannel channel;
	private OrderStatus status;
	private String customerName;
	private String customerPhone;
	private String customerEmail;
	private String shippingAddress;
	private String shippingCity;
	private String shippingRegion;
	private String shippingPostalCode;
	private BigDecimal totalAmount;
	private String currency;
	private String notes;
	private Instant createdAt;
	private List<OrderItemResponse> items;
}
