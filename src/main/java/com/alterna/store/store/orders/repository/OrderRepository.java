package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	List<OrderEntity> findByStatus(OrderStatus status);

	/** All orders ordered by created date (no fetch; use OrderService to attach items). */
	List<OrderEntity> findAllByOrderByCreatedAtDesc();

	/** Orders by status ordered by created date (no fetch). */
	List<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status);

	List<OrderEntity> findByCreatedAtBetween(Instant from, Instant to);
}
