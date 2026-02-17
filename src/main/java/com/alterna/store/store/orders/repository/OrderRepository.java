package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
	List<OrderEntity> findByStatus(OrderStatus status);
	List<OrderEntity> findByCreatedAtBetween(Instant from, Instant to);
}
