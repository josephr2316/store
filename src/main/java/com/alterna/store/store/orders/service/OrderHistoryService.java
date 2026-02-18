package com.alterna.store.store.orders.service;

import com.alterna.store.store.orders.dto.OrderHistoryResponse;
import com.alterna.store.store.orders.mapper.OrderMapper;
import com.alterna.store.store.orders.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderHistoryService {

	private final OrderStatusHistoryRepository historyRepository;
	private final OrderMapper orderMapper;

	/** Load history by order id without touching lazy collection (avoids LazyInitializationException). */
	@Transactional(readOnly = true)
	public List<OrderHistoryResponse> getHistory(Long orderId) {
		return historyRepository.findByOrderIdOrderByChangedAtDesc(orderId).stream()
				.map(orderMapper::toHistoryResponse)
				.collect(Collectors.toList());
	}
}
