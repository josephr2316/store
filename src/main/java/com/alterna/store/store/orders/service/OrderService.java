package com.alterna.store.store.orders.service;

import com.alterna.store.store.catalog.entity.VariantEntity;
import com.alterna.store.store.catalog.repository.VariantRepository;
import com.alterna.store.store.orders.dto.*;
import com.alterna.store.store.orders.entity.*;
import com.alterna.store.store.orders.enums.OrderChannel;
import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.orders.mapper.OrderMapper;
import com.alterna.store.store.orders.repository.OrderItemRepository;
import com.alterna.store.store.orders.repository.OrderRepository;
import com.alterna.store.store.orders.repository.OrderStatusHistoryRepository;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	/** Max size for IN clause to avoid DB/driver limits (e.g. PostgreSQL). */
	private static final int ORDER_IDS_BATCH_SIZE = 200;

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final OrderStatusHistoryRepository historyRepository;
	private final VariantRepository variantRepository;
	private final OrderMapper orderMapper;

	@Transactional
	public OrderResponse create(OrderCreateRequest req) {
		OrderEntity order = OrderEntity.builder()
				.externalId(req.getExternalId())
				.channel(req.getChannel() != null ? req.getChannel() : OrderChannel.OTHER)
				.status(OrderStatus.PENDING)
				.customerName(req.getCustomerName())
				.customerPhone(req.getCustomerPhone())
				.customerEmail(req.getCustomerEmail())
				.shippingAddress(req.getShippingAddress() != null ? AddressEmbeddable.builder()
						.address(req.getShippingAddress())
						.city(req.getShippingCity())
						.region(req.getShippingRegion())
						.postalCode(req.getShippingPostalCode())
						.build() : null)
				.notes(req.getNotes())
				.currency("USD")
				.totalAmount(BigDecimal.ZERO)
				.build();
		BigDecimal total = BigDecimal.ZERO;
		List<OrderItemEntity> items = new ArrayList<>();
		for (OrderItemRequest ir : req.getItems()) {
			VariantEntity variant = variantRepository.findById(ir.getVariantId())
					.orElseThrow(() -> new ResourceNotFoundException("Variant", ir.getVariantId()));
			OrderItemEntity item = OrderItemEntity.builder()
					.order(order)
					.variant(variant)
					.quantity(ir.getQuantity())
					.unitPrice(ir.getUnitPrice())
					.build();
			items.add(item);
			total = total.add(ir.getUnitPrice().multiply(BigDecimal.valueOf(ir.getQuantity())));
		}
		order.setItems(items);
		order.setTotalAmount(total);
		order = orderRepository.save(order);
		// Save initial status history directly (avoids lazy collection access)
		historyRepository.save(OrderStatusHistoryEntity.builder()
				.order(order)
				.fromStatus(null)
				.toStatus(OrderStatus.PENDING)
				.reason("Order created")
				.build());
		return orderMapper.toResponse(order);
	}

	@Transactional
	public OrderResponse getById(Long id) {
		OrderEntity e = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order", id));
		List<OrderItemEntity> items = orderItemRepository.findByOrderIdInWithVariantAndProduct(List.of(id));
		e.setItems(items);
		return orderMapper.toResponse(e);
	}

	/**
	 * Returns paginated order headers WITHOUT items for fast list rendering.
	 * Tries native SQL first (avoids entity load and pgBouncer issues); falls back to entity-based list if native fails.
	 */
	@Transactional(readOnly = true)
	public Page<OrderResponse> listByStatus(OrderStatus status, int page, int size) {
		PageRequest pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "created_at"));
		try {
			Page<Object[]> pageResult = status != null
					? orderRepository.findSummariesByStatusNative(status.name(), pageable)
					: orderRepository.findAllSummariesNative(pageable);
			return pageResult.map(orderMapper::fromSummaryRow);
		} catch (DataAccessException e) {
			log.warn("Native order list failed, falling back to entity query: {}", e.getMessage());
			Page<OrderEntity> entityPage = status != null
					? orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
					: orderRepository.findAllByOrderByCreatedAtDesc(pageable);
			return entityPage.map(orderMapper::toSummaryResponse);
		}
	}

	/** Convenience overload for backward compat (page 0, 30 items). */
	@Transactional(readOnly = true)
	public Page<OrderResponse> listByStatus(OrderStatus status) {
		return listByStatus(status, 0, 30);
	}
}
