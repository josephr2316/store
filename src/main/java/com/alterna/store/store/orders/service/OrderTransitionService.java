package com.alterna.store.store.orders.service;

import com.alterna.store.store.inventory.entity.InventoryBalanceEntity;
import com.alterna.store.store.inventory.repository.InventoryBalanceRepository;
import com.alterna.store.store.orders.dto.OrderTransitionRequest;
import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.entity.OrderItemEntity;
import com.alterna.store.store.orders.entity.OrderStatusHistoryEntity;
import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.orders.mapper.OrderMapper;
import com.alterna.store.store.orders.repository.OrderRepository;
import com.alterna.store.store.orders.rules.OrderStateMachine;
import com.alterna.store.store.orders.entity.AddressEmbeddable;
import com.alterna.store.store.shared.exception.ConflictException;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import com.alterna.store.store.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderTransitionService {

	private final OrderRepository orderRepository;
	private final InventoryBalanceRepository inventoryBalanceRepository;
	private final OrderMapper orderMapper;
	@Lazy
	private final OrderService orderService;

	@Transactional
	public com.alterna.store.store.orders.dto.OrderResponse transition(Long orderId, OrderTransitionRequest req) {
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
		OrderStatus from = order.getStatus();
		OrderStatus to = req.getToStatus();
		OrderStateMachine.validateTransition(from, to);
		if (to == OrderStatus.SHIPPED || to == OrderStatus.DELIVERED) {
			requireShippingAddress(order);
		}
		if (to == OrderStatus.CONFIRMED) {
			reserveStock(order);
		}
		if (to == OrderStatus.CANCELLED && hasReservedStock(from)) {
			releaseReservation(order);
		}
		order.setStatus(to);
		orderRepository.save(order);
		OrderStatusHistoryEntity hist = OrderStatusHistoryEntity.builder()
				.order(order)
				.fromStatus(from)
				.toStatus(to)
				.reason(req.getReason())
				.build();
		order.getStatusHistory().add(hist);
		orderRepository.save(order);
		return orderService.getById(orderId);
	}

	private void reserveStock(OrderEntity order) {
		for (OrderItemEntity item : order.getItems()) {
			InventoryBalanceEntity bal = inventoryBalanceRepository.findByVariantId(item.getVariant().getId())
					.orElseThrow(() -> new ConflictException("No inventory for variant " + item.getVariant().getId()));
			int available = bal.getQuantity() - bal.getReserved();
			if (available < item.getQuantity()) {
				throw new ConflictException("Insufficient stock for variant " + item.getVariant().getSku());
			}
			bal.setReserved(bal.getReserved() + item.getQuantity());
			inventoryBalanceRepository.save(bal);
		}
	}

	/** Release reserved stock when cancelling from any state that had reservation. */
	private boolean hasReservedStock(OrderStatus from) {
		return from == OrderStatus.CONFIRMED || from == OrderStatus.PREPARING || from == OrderStatus.SHIPPED;
	}

	private void requireShippingAddress(OrderEntity order) {
		AddressEmbeddable addr = order.getShippingAddress();
		if (addr == null || addr.getAddress() == null || addr.getAddress().isBlank()) {
			throw new ValidationException("El pedido debe tener dirección de envío antes de marcar como Enviado o Entregado.");
		}
	}

	private void releaseReservation(OrderEntity order) {
		for (OrderItemEntity item : order.getItems()) {
			inventoryBalanceRepository.findByVariantId(item.getVariant().getId()).ifPresent(bal -> {
				bal.setReserved(Math.max(0, bal.getReserved() - item.getQuantity()));
				inventoryBalanceRepository.save(bal);
			});
		}
	}
}
