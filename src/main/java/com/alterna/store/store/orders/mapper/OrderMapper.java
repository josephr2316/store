package com.alterna.store.store.orders.mapper;

import com.alterna.store.store.orders.dto.*;
import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.entity.OrderItemEntity;
import com.alterna.store.store.orders.entity.OrderStatusHistoryEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

	private static BigDecimal toBigDecimal(Object o) {
		if (o == null) return BigDecimal.ZERO;
		if (o instanceof BigDecimal bd) return bd;
		if (o instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
		try {
			return new BigDecimal(o.toString());
		} catch (Exception ex) {
			return BigDecimal.ZERO;
		}
	}

	/** Full response: loads items (use for getById). */
	public OrderResponse toResponse(OrderEntity e) {
		if (e == null) return null;
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
				.totalAmount(toBigDecimal(e.getTotalAmount()))
				.currency(e.getCurrency())
				.notes(e.getNotes())
				.createdAt(e.getCreatedAt())
				.items(e.getItems() != null ? e.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()) : Collections.emptyList())
				.build();
	}

	/**
	 * Summary response: NEVER accesses lazy collections.
	 * Safe for list endpoints where 400+ orders are returned at once.
	 */
	public OrderResponse toSummaryResponse(OrderEntity e) {
		if (e == null) return null;
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
				.totalAmount(toBigDecimal(e.getTotalAmount()))
				.currency(e.getCurrency())
				.notes(e.getNotes())
				.createdAt(e.getCreatedAt())
				.items(Collections.emptyList())  // explicitly empty — no lazy collection access
				.build();
	}

	public OrderItemResponse toItemResponse(OrderItemEntity i) {
		if (i == null) return null;
		if (i.getVariant() == null) {
			return OrderItemResponse.builder()
					.id(i.getId())
					.variantId(null)
					.variantSku("")
					.quantity(i.getQuantity() != null ? i.getQuantity() : 0)
					.unitPrice(toBigDecimal(i.getUnitPrice()))
					.build();
		}
		return OrderItemResponse.builder()
				.id(i.getId())
				.variantId(i.getVariant().getId())
				.variantSku(i.getVariant().getSku() != null ? i.getVariant().getSku() : "")
				.quantity(i.getQuantity() != null ? i.getQuantity() : 0)
				.unitPrice(toBigDecimal(i.getUnitPrice()))
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
