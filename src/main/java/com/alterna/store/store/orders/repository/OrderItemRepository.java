package com.alterna.store.store.orders.repository;

import com.alterna.store.store.orders.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
	List<OrderItemEntity> findByOrderId(Long orderId);

	/** Load items with variant fetched (avoids LazyInitializationException). Batch by order ids. */
	@Query("SELECT i FROM OrderItemEntity i JOIN FETCH i.variant WHERE i.order.id IN :orderIds")
	List<OrderItemEntity> findByOrderIdInWithVariant(@Param("orderIds") List<Long> orderIds);

	/** Load items with variant and product for order detail (product name, variant name). DISTINCT avoids duplicate rows from multiple JOIN FETCH. */
	@Query("SELECT DISTINCT i FROM OrderItemEntity i JOIN FETCH i.variant v JOIN FETCH v.product WHERE i.order.id IN :orderIds")
	List<OrderItemEntity> findByOrderIdInWithVariantAndProduct(@Param("orderIds") List<Long> orderIds);

	/** Native query for order detail items (avoids JPQL prepared statements with pgBouncer). Row: id, variant_id, sku, name, quantity, unit_price. */
	@Query(value = "SELECT oi.id, oi.variant_id, v.sku, v.name, oi.quantity, oi.unit_price FROM order_items oi JOIN variants v ON v.id = oi.variant_id WHERE oi.order_id = :orderId", nativeQuery = true)
	List<Object[]> findItemRowsByOrderIdNative(@Param("orderId") Long orderId);
}
