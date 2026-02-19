package com.alterna.store.store.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adds CORS headers to every response (including 500 errors) so the browser
 * does not block the response. Runs before the request is processed.
 * <p>
 * Allowed origins are configured via {@code app.cors.allowed-origin-patterns}:
 * <ul>
 *   <li>{@code *} — allow any origin (solves CORS for everyone: Vercel, custom domains, localhost)</li>
 *   <li>Comma-separated list — e.g. {@code https://myapp.vercel.app,https://*.vercel.app}; {@code *} in a pattern matches any suffix (e.g. {@code *.vercel.app})</li>
 * </ul>
 * In production (e.g. Railway) set {@code APP_CORS_ALLOWED_ORIGIN_PATTERNS=*} to allow all frontends, or list specific origins.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsResponseFilter extends OncePerRequestFilter {

	@Value("${app.cors.allowed-origin-patterns:*}")
	private String allowedOriginPatterns;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String origin = request.getHeader("Origin");
		boolean allow = isOriginAllowed(origin);
		if (allow && origin != null && !origin.isEmpty()) {
			response.setHeader("Access-Control-Allow-Origin", origin);
		}
		// No origin header (e.g. same-origin or server-to-server) — no Allow-Origin needed
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept, X-Requested-With");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Max-Age", "3600");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Returns true if the given origin is allowed by configured patterns.
	 * "*" allows any origin; otherwise comma-separated list of patterns (exact or suffix like *.vercel.app).
	 */
	private boolean isOriginAllowed(String origin) {
		if (origin == null || origin.isEmpty()) {
			return false;
		}
		String patterns = allowedOriginPatterns == null ? "" : allowedOriginPatterns.trim();
		if ("*".equals(patterns)) {
			return true;
		}
		List<String> list = Arrays.stream(patterns.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
		for (String pattern : list) {
			if (pattern.startsWith("*.")) {
				String suffix = pattern.substring(1);
				if (origin.endsWith(suffix)) {
					return true;
				}
			} else if (pattern.equals(origin)) {
				return true;
			}
		}
		return false;
	}
}
