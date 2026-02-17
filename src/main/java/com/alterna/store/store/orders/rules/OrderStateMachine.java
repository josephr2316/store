package com.alterna.store.store.orders.rules;

import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.shared.exception.ValidationException;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Reglas de transición de estados del pedido.
 * No se puede pasar a SHIPPED sin estar CONFIRMED, etc.
 */
public final class OrderStateMachine {

	private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
			OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
			OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
			OrderStatus.PREPARING, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
			OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED),
			OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class),
			OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class)
	);

	private OrderStateMachine() {}

	public static void validateTransition(OrderStatus from, OrderStatus to) {
		if (from == to) {
			throw new ValidationException("Order is already in status " + to);
		}
		Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.get(from);
		if (allowed == null || !allowed.contains(to)) {
			throw new ValidationException("Transition from " + from + " to " + to + " is not allowed");
		}
	}
}
