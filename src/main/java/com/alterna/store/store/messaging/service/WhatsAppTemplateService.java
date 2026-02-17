package com.alterna.store.store.messaging.service;

import com.alterna.store.store.messaging.dto.WhatsAppMessageResponse;
import com.alterna.store.store.orders.dto.OrderResponse;
import com.alterna.store.store.shared.util.WhatsAppLinkUtil;
import org.springframework.stereotype.Service;

/**
 * Genera el texto de plantilla para WhatsApp con variables del pedido.
 */
@Service
public class WhatsAppTemplateService {

	private static final String TEMPLATE = """
			Hola %s,
			Tu pedido #%s está %s.
			Total: %s %s.
			Gracias por tu compra.
			""";

	public WhatsAppMessageResponse buildOrderMessage(OrderResponse order, String customerPhone) {
		String customerName = order.getCustomerName() != null ? order.getCustomerName() : "Cliente";
		String message = String.format(TEMPLATE,
				customerName,
				order.getId(),
				order.getStatus().name(),
				order.getTotalAmount(),
				order.getCurrency() != null ? order.getCurrency() : "USD");
		String waLink = WhatsAppLinkUtil.buildLink(customerPhone, message);
		return WhatsAppMessageResponse.builder()
				.message(message)
				.waLink(waLink)
				.build();
	}
}
