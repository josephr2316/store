package com.alterna.store.store.orders.service;

import com.alterna.store.store.inventory.entity.InventoryBalanceEntity;
import com.alterna.store.store.inventory.repository.InventoryBalanceRepository;
import com.alterna.store.store.orders.dto.OrderTransitionRequest;
import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.entity.OrderItemEntity;
import com.alterna.store.store.orders.entity.OrderStatusHistoryEntity;
import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.orders.mapper.OrderMapper;
import com.alterna.store.store.orders.repository.OrderItemRepository;
import com.alterna.store.store.orders.repository.OrderRepository;
import com.alterna.store.store.orders.repository.OrderStatusHistoryRepository;
import com.alterna.store.store.orders.rules.OrderStateMachine;
import com.alterna.store.store.orders.entity.AddressEmbeddable;
import com.alterna.store.store.shared.exception.ConflictException;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import com.alterna.store.store.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTransitionService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final OrderStatusHistoryRepository historyRepository;
	private final InventoryBalanceRepository inventoryBalanceRepository;
	private final OrderMapper orderMapper;
	@Lazy
	private final OrderService orderService;

	@Transactional
	public com.alterna.store.store.orders.dto.OrderResponse transition(Long orderId, OrderTransitionRequest req) {
		if (req == null || req.getToStatus() == null) {
			throw new ValidationException("Estado de transición no válido.");
		}
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
		// Load items with variant to avoid LazyInitializationException (no lazy access in reserve/release)
		List<OrderItemEntity> items = orderItemRepository.findByOrderIdInWithVariant(List.of(orderId));
		order.setItems(items != null ? items : List.of());
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
		// Save history directly (avoids touching lazy statusHistory collection)
		historyRepository.save(OrderStatusHistoryEntity.builder()
				.order(order)
				.fromStatus(from)
				.toStatus(to)
				.reason(req.getReason() != null ? req.getReason() : "")
				.build());
		try {
			return orderService.getById(orderId);
		} catch (Exception ex) {
			log.warn("getById after transition failed for order {}, returning from in-memory order: {}", orderId, ex.getMessage());
			return orderMapper.toResponse(order);
		}
	}

	private void reserveStock(OrderEntity order) {
		List<OrderItemEntity> items = order.getItems();
		if (items == null) return;
		for (OrderItemEntity item : items) {
			if (item == null || item.getVariant() == null) {
				throw new ValidationException("El pedido tiene un artículo sin variante válida. Revisa el pedido.");
			}
			Long variantId = item.getVariant().getId();
			int qty = item.getQuantity() != null ? item.getQuantity() : 0;
			if (qty <= 0) continue;
			InventoryBalanceEntity bal = inventoryBalanceRepository.findByVariantId(variantId)
					.orElseThrow(() -> new ConflictException("No hay inventario para la variante " + variantId + ". Crea el balance en Caja."));
			int stock = bal.getQuantity() != null ? bal.getQuantity() : 0;
			int reserved = bal.getReserved() != null ? bal.getReserved() : 0;
			int available = stock - reserved;
			if (available < qty) {
				throw new ConflictException("Stock insuficiente para " + (item.getVariant().getSku() != null ? item.getVariant().getSku() : "variante " + variantId) + ". Disponible: " + available);
			}
			bal.setReserved(reserved + qty);
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
		List<OrderItemEntity> items = order.getItems();
		if (items == null) return;
		for (OrderItemEntity item : items) {
			if (item == null || item.getVariant() == null) continue;
			Long variantId = item.getVariant().getId();
			int qty = item.getQuantity() != null ? item.getQuantity() : 0;
			inventoryBalanceRepository.findByVariantId(variantId).ifPresent(bal -> {
				int current = bal.getReserved() != null ? bal.getReserved() : 0;
				bal.setReserved(Math.max(0, current - qty));
				inventoryBalanceRepository.save(bal);
			});
		}
	}
}
