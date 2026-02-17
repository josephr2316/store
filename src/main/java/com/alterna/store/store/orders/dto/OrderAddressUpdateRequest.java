package com.alterna.store.store.orders.dto;

import lombok.Data;

@Data
public class OrderAddressUpdateRequest {
	private String shippingAddress;
	private String shippingCity;
	private String shippingRegion;
	private String shippingPostalCode;
}
