package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	List<OrderEntity> findByStatus(OrderStatus status);

	/** All orders paginated, ordered by created date. */
	Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

	/** All orders (no pagination — kept for backward compat but avoid for large datasets). */
	List<OrderEntity> findAllByOrderByCreatedAtDesc();

	/** Orders by status, paginated, ordered by created date. */
	Page<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

	/** Orders by status ordered by created date (no fetch). */
	List<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status);

	List<OrderEntity> findByCreatedAtBetween(Instant from, Instant to);
}
