package com.alterna.store.store.orders.service;

import com.alterna.store.store.catalog.entity.VariantEntity;
import com.alterna.store.store.catalog.repository.VariantRepository;
import com.alterna.store.store.inventory.entity.InventoryBalanceEntity;
import com.alterna.store.store.inventory.repository.InventoryBalanceRepository;
import com.alterna.store.store.orders.dto.*;
import com.alterna.store.store.orders.entity.*;
import com.alterna.store.store.orders.enums.OrderChannel;
import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.orders.mapper.OrderMapper;
import com.alterna.store.store.orders.repository.OrderItemRepository;
import com.alterna.store.store.orders.repository.OrderRepository;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import com.alterna.store.store.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final VariantRepository variantRepository;
	private final InventoryBalanceRepository inventoryBalanceRepository;
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
		order = orderRepository.save(order);
		OrderStatusHistoryEntity hist = OrderStatusHistoryEntity.builder()
				.order(order)
				.fromStatus(null)
				.toStatus(OrderStatus.PENDING)
				.reason("Order created")
				.build();
		order.getStatusHistory().add(hist);
		orderRepository.save(order);
		return orderMapper.toResponse(order);
	}

	@Transactional(readOnly = true)
	public OrderResponse getById(Long id) {
		OrderEntity e = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order", id));
		List<OrderItemEntity> items = orderItemRepository.findByOrderIdInWithVariant(List.of(id));
		e.setItems(items);
		return orderMapper.toResponse(e);
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> listByStatus(OrderStatus status) {
		List<OrderEntity> orders = status != null
				? orderRepository.findByStatusOrderByCreatedAtDesc(status)
				: orderRepository.findAllByOrderByCreatedAtDesc();
		if (orders.isEmpty()) return Collections.emptyList();
		List<Long> orderIds = orders.stream().map(OrderEntity::getId).toList();
		List<OrderItemEntity> itemsWithVariant = orderItemRepository.findByOrderIdInWithVariant(orderIds);
		var itemsByOrderId = itemsWithVariant.stream().collect(Collectors.groupingBy(i -> i.getOrder().getId()));
		for (OrderEntity o : orders) {
			o.setItems(itemsByOrderId.getOrDefault(o.getId(), Collections.emptyList()));
		}
		return orders.stream().map(orderMapper::toResponse).toList();
	}
}
