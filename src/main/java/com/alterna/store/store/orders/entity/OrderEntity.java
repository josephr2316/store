package com.alterna.store.store.orders.entity;

import com.alterna.store.store.orders.enums.OrderChannel;
import com.alterna.store.store.orders.enums.OrderStatus;
import com.alterna.store.store.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "external_id", length = 100)
	private String externalId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private OrderChannel channel = OrderChannel.OTHER;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private OrderStatus status = OrderStatus.PENDING;

	@Column(name = "customer_name", length = 500)
	private String customerName;

	@Column(name = "customer_phone", length = 50)
	private String customerPhone;

	@Column(name = "customer_email")
	private String customerEmail;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "address", column = @Column(name = "shipping_address", columnDefinition = "TEXT")),
			@AttributeOverride(name = "city", column = @Column(name = "shipping_city")),
			@AttributeOverride(name = "region", column = @Column(name = "shipping_region")),
			@AttributeOverride(name = "postalCode", column = @Column(name = "shipping_postal_code"))
	})
	private AddressEmbeddable shippingAddress;

	@Column(name = "total_amount", precision = 19, scale = 4, nullable = false)
	@Builder.Default
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(length = 3)
	@Builder.Default
	private String currency = "USD";

	@Column(columnDefinition = "TEXT")
	private String notes;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OrderItemEntity> items = new ArrayList<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("changedAt DESC")
	@Builder.Default
	private List<OrderStatusHistoryEntity> statusHistory = new ArrayList<>();
}
