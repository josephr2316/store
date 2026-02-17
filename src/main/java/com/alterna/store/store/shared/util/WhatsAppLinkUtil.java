package com.alterna.store.store.shared.util;

import java.util.regex.Pattern;

/**
 * Builds wa.me links for WhatsApp Business.
 * Format: https://wa.me/&lt;number&gt;?text=&lt;url-encoded message&gt;
 */
public final class WhatsAppLinkUtil {

	private static final String BASE = "https://wa.me/";
	private static final Pattern DIGITS = Pattern.compile("\\D");

	private WhatsAppLinkUtil() {}

	/**
	 * Digits only from the phone number (no +, spaces, or dashes).
	 */
	public static String normalizePhone(String phone) {
		if (phone == null) return "";
		return DIGITS.matcher(phone).replaceAll("");
	}

	/**
	 * URL to open chat with pre-filled message text.
	 */
	public static String buildLink(String phone, String message) {
		String num = normalizePhone(phone);
		if (num.isEmpty()) return BASE;
		String encoded = message != null ? java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8) : "";
		return BASE + num + (encoded.isEmpty() ? "" : "?text=" + encoded);
	}
}
