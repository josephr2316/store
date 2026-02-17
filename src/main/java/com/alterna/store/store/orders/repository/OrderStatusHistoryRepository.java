package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistoryEntity, Long> {
	List<OrderStatusHistoryEntity> findByOrderIdOrderByChangedAtDesc(Long orderId);
}
