package com.alterna.store.store.orders.mapper;

import com.alterna.store.store.orders.dto.*;
import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.entity.OrderItemEntity;
import com.alterna.store.store.orders.entity.OrderStatusHistoryEntity;
import com.alterna.store.store.orders.enums.OrderChannel;
import com.alterna.store.store.orders.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
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

	private static long toLong(Object o) {
		if (o == null) return 0L;
		if (o instanceof Number n) return n.longValue();
		try {
			return Long.parseLong(o.toString());
		} catch (Exception ex) {
			return 0L;
		}
	}

	private static int toInt(Object o) {
		if (o == null) return 0;
		if (o instanceof Number n) return n.intValue();
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception ex) {
			return 0;
		}
	}

	private static OrderChannel parseChannel(Object o) {
		if (o == null) return OrderChannel.OTHER;
		try {
			return OrderChannel.valueOf(o.toString().trim().toUpperCase());
		} catch (Exception e) {
			return OrderChannel.OTHER;
		}
	}

	private static OrderStatus parseStatus(Object o) {
		if (o == null) return OrderStatus.PENDING;
		try {
			return OrderStatus.valueOf(o.toString().trim().toUpperCase());
		} catch (Exception e) {
			return OrderStatus.PENDING;
		}
	}

	private static Instant toInstant(Object o) {
		if (o == null) return null;
		if (o instanceof Instant i) return i;
		if (o instanceof java.sql.Timestamp ts) return ts.toInstant();
		if (o instanceof OffsetDateTime odt) return odt.toInstant();
		if (o instanceof java.util.Date d) return d.toInstant();
		return null;
	}

	/** Full response: loads items (use for getById when items from entity). */
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

	/** Build order response with pre-mapped items (e.g. from native query). Does not access e.getItems(). */
	public OrderResponse toResponseWithItems(OrderEntity e, List<OrderItemResponse> items) {
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
				.items(items != null ? items : Collections.emptyList())
				.build();
	}

	/** Map native item row to OrderItemResponse. Row: id, variant_id, sku, name, quantity, unit_price. Defensive for pgBouncer/driver type variations. */
	public OrderItemResponse fromItemRow(Object[] row) {
		if (row == null || row.length < 6) return null;
		return OrderItemResponse.builder()
				.id(row[0] != null ? Long.valueOf(toLong(row[0])) : null)
				.variantId(row[1] != null ? Long.valueOf(toLong(row[1])) : null)
				.variantSku(row[2] != null ? row[2].toString() : "")
				.productName(null)
				.variantName(row[3] != null ? row[3].toString() : (row[2] != null ? row[2].toString() : ""))
				.quantity(toInt(row[4]))
				.unitPrice(toBigDecimal(row[5]))
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

	/**
	 * Map native query row to OrderResponse (summary). Row order: id, external_id, channel, status,
	 * customer_name, customer_phone, customer_email, shipping_address, shipping_city, shipping_region,
	 * shipping_postal_code, total_amount, currency, notes, created_at.
	 */
	public OrderResponse fromSummaryRow(Object[] row) {
		if (row == null || row.length < 15) return null;
		return OrderResponse.builder()
				.id(row[0] != null ? ((Number) row[0]).longValue() : null)
				.externalId(row[1] != null ? row[1].toString() : null)
				.channel(parseChannel(row[2]))
				.status(parseStatus(row[3]))
				.customerName(row[4] != null ? row[4].toString() : null)
				.customerPhone(row[5] != null ? row[5].toString() : null)
				.customerEmail(row[6] != null ? row[6].toString() : null)
				.shippingAddress(row[7] != null ? row[7].toString() : null)
				.shippingCity(row[8] != null ? row[8].toString() : null)
				.shippingRegion(row[9] != null ? row[9].toString() : null)
				.shippingPostalCode(row[10] != null ? row[10].toString() : null)
				.totalAmount(toBigDecimal(row[11]))
				.currency(row[12] != null ? row[12].toString() : null)
				.notes(row[13] != null ? row[13].toString() : null)
				.createdAt(toInstant(row[14]))
				.items(Collections.emptyList())
				.build();
	}

	public OrderItemResponse toItemResponse(OrderItemEntity i) {
		if (i == null) return null;
		if (i.getVariant() == null) {
			return OrderItemResponse.builder()
					.id(i.getId())
					.variantId(null)
					.variantSku("")
					.productName(null)
					.variantName(null)
					.quantity(i.getQuantity() != null ? i.getQuantity() : 0)
					.unitPrice(toBigDecimal(i.getUnitPrice()))
					.build();
		}
		var v = i.getVariant();
		// Do not access v.getProduct() — it is lazy and can cause LazyInitializationException / 500 in production
		String variantName = v.getName() != null ? v.getName() : (v.getSku() != null ? v.getSku() : "");
		return OrderItemResponse.builder()
				.id(i.getId())
				.variantId(v.getId())
				.variantSku(v.getSku() != null ? v.getSku() : "")
				.productName(null)
				.variantName(variantName)
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
