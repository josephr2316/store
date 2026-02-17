package com.alterna.store.store.orders.entity;

import com.alterna.store.store.catalog.entity.VariantEntity;
import com.alterna.store.store.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEntity extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private OrderEntity order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variant_id", nullable = false)
	private VariantEntity variant;

	@Column(nullable = false)
	private Integer quantity;

	@Column(name = "unit_price", precision = 19, scale = 4, nullable = false)
	private BigDecimal unitPrice;
}
