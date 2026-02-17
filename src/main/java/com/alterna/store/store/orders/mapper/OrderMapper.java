package com.alterna.store.store.orders.mapper;

import com.alterna.store.store.orders.dto.*;
import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.entity.OrderItemEntity;
import com.alterna.store.store.orders.entity.OrderStatusHistoryEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

	public OrderResponse toResponse(OrderEntity e) {
		return OrderResponse.builder()
				.id(e.getId())
				.externalId(e.getExternalId())
				.channel(e.getChannel())
				.status(e.getStatus())
				.customerName(e.getCustomerName())
				.customerPhone(e.getCustomerPhone())
				.customerEmail(e.getCustomerEmail())
				.shippingAddress(e.getShippingAddress() != null ? e.getShippingAddress().getAddress() : null)
				.shippingCity(e.getShippingAddress() != null ? e.getShippingAddress().getCity() : null)
				.shippingRegion(e.getShippingAddress() != null ? e.getShippingAddress().getRegion() : null)
				.shippingPostalCode(e.getShippingAddress() != null ? e.getShippingAddress().getPostalCode() : null)
				.totalAmount(e.getTotalAmount())
				.currency(e.getCurrency())
				.notes(e.getNotes())
				.createdAt(e.getCreatedAt())
				.items(e.getItems() != null ? e.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()) : Collections.emptyList())
				.build();
	}

	public OrderItemResponse toItemResponse(OrderItemEntity i) {
		return OrderItemResponse.builder()
				.id(i.getId())
				.variantId(i.getVariant().getId())
				.variantSku(i.getVariant().getSku())
				.quantity(i.getQuantity())
				.unitPrice(i.getUnitPrice())
				.build();
	}

	public OrderHistoryResponse toHistoryResponse(OrderStatusHistoryEntity h) {
		return OrderHistoryResponse.builder()
				.id(h.getId())
				.fromStatus(h.getFromStatus())
				.toStatus(h.getToStatus())
				.reason(h.getReason())
				.changedBy(h.getChangedBy())
				.changedAt(h.getChangedAt())
				.build();
	}
}
