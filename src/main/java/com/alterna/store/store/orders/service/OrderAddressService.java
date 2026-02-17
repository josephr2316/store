package com.alterna.store.store.orders.service;

import com.alterna.store.store.orders.dto.OrderAddressUpdateRequest;
import com.alterna.store.store.orders.dto.OrderResponse;
import com.alterna.store.store.orders.entity.AddressEmbeddable;
import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.repository.OrderRepository;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderAddressService {

	private final OrderRepository orderRepository;
	private final OrderService orderService;

	@Transactional
	public OrderResponse updateAddress(Long orderId, OrderAddressUpdateRequest req) {
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
		AddressEmbeddable addr = order.getShippingAddress() != null ? order.getShippingAddress() : new AddressEmbeddable();
		if (req.getShippingAddress() != null) addr.setAddress(req.getShippingAddress());
		if (req.getShippingCity() != null) addr.setCity(req.getShippingCity());
		if (req.getShippingRegion() != null) addr.setRegion(req.getShippingRegion());
		if (req.getShippingPostalCode() != null) addr.setPostalCode(req.getShippingPostalCode());
		order.setShippingAddress(addr);
		orderRepository.save(order);
		return orderService.getById(orderId);
	}
}
