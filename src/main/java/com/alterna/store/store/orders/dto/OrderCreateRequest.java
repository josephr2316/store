package com.alterna.store.store.orders.dto;

import com.alterna.store.store.orders.enums.OrderChannel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {
	private String externalId;
	private OrderChannel channel;
	private String customerName;
	private String customerPhone;
	private String customerEmail;
	private String shippingAddress;
	private String shippingCity;
	private String shippingRegion;
	private String shippingPostalCode;
	private String notes;
	@NotNull
	@NotEmpty
	@Valid
	private List<OrderItemRequest> items;
}
