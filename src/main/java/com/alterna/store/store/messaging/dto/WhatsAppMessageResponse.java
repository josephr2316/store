package com.alterna.store.store.messaging.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WhatsAppMessageResponse {
	private String message;
	private String waLink;
}
