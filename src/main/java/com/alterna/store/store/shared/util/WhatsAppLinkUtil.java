package com.alterna.store.store.shared.util;

import java.util.regex.Pattern;

/**
 * Genera enlaces wa.me para WhatsApp Business.
 * Formato: https://wa.me/<número>?text=<mensaje codificado>
 */
public final class WhatsAppLinkUtil {

	private static final String BASE = "https://wa.me/";
	private static final Pattern DIGITS = Pattern.compile("\\D");

	private WhatsAppLinkUtil() {}

	/**
	 * Solo dígitos del número (sin +, espacios, guiones).
	 */
	public static String normalizePhone(String phone) {
		if (phone == null) return "";
		return DIGITS.matcher(phone).replaceAll("");
	}

	/**
	 * URL para abrir chat con texto prellenado.
	 */
	public static String buildLink(String phone, String message) {
		String num = normalizePhone(phone);
		if (num.isEmpty()) return BASE;
		String encoded = message != null ? java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8) : "";
		return BASE + num + (encoded.isEmpty() ? "" : "?text=" + encoded);
	}
}
