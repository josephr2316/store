package com.alterna.store.store.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Adds CORS headers to every response (including 500 errors) so the browser
 * does not block the response. Runs before the request is processed.
 * Frontend: https://store-frontend-olive-sigma.vercel.app
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsResponseFilter extends OncePerRequestFilter {

	private static final List<String> ALLOWED_ORIGINS = List.of(
			"https://store-frontend-olive-sigma.vercel.app",
			"http://localhost:5173",
			"http://localhost:3000",
			"http://127.0.0.1:5173",
			"http://127.0.0.1:3000"
	);

	private static final String ALLOWED_ORIGIN_PATTERN_SUFFIX = ".vercel.app";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String origin = request.getHeader("Origin");
		if (origin != null && !origin.isEmpty()) {
			boolean allow = ALLOWED_ORIGINS.contains(origin)
					|| (origin.startsWith("https://") && origin.endsWith(ALLOWED_ORIGIN_PATTERN_SUFFIX));
			if (allow) {
				response.setHeader("Access-Control-Allow-Origin", origin);
			}
		} else {
			response.setHeader("Access-Control-Allow-Origin", "https://store-frontend-olive-sigma.vercel.app");
		}
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
}
