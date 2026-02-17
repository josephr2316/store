package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
	List<OrderItemEntity> findByOrderId(Long orderId);
}
