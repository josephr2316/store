package com.alterna.store.store.orders.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressEmbeddable {
	@Column(name = "shipping_address", columnDefinition = "TEXT")
	private String address;
	@Column(name = "shipping_city")
	private String city;
	@Column(name = "shipping_region")
	private String region;
	@Column(name = "shipping_postal_code")
	private String postalCode;
}
