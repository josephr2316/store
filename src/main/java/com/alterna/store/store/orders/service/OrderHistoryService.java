package com.alterna.store.store.orders.service;

import com.alterna.store.store.orders.dto.OrderHistoryResponse;
import com.alterna.store.store.orders.entity.OrderEntity;
import com.alterna.store.store.orders.mapper.OrderMapper;
import com.alterna.store.store.orders.repository.OrderRepository;
import com.alterna.store.store.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;

	@Transactional(readOnly = true)
	public List<OrderHistoryResponse> getHistory(Long orderId) {
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
		return order.getStatusHistory().stream()
				.map(orderMapper::toHistoryResponse)
				.collect(Collectors.toList());
	}
}
