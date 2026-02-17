package com.alterna.store.store.messaging.controller;

import com.alterna.store.store.messaging.dto.WhatsAppMessageResponse;
import com.alterna.store.store.messaging.service.WhatsAppTemplateService;
import com.alterna.store.store.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
@Tag(name = "WhatsApp")
public class WhatsAppMessageController {

	private final OrderService orderService;
	private final WhatsAppTemplateService templateService;

	@GetMapping("/order/{orderId}/message")
	@Operation(summary = "Get WhatsApp message template for order (with wa.me link)")
	public ResponseEntity<WhatsAppMessageResponse> getOrderMessage(
			@PathVariable Long orderId,
			@RequestParam(required = false) String phone) {
		var order = orderService.getById(orderId);
		String customerPhone = phone != null ? phone : order.getCustomerPhone();
		return ResponseEntity.ok(templateService.buildOrderMessage(order, customerPhone != null ? customerPhone : ""));
	}
}
