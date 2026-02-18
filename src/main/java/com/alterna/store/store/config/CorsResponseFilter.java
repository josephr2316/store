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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String origin = request.getHeader("Origin");
		// Allow any Vercel deployment and localhost (this filter covers error responses too)
		boolean allow = origin != null && !origin.isEmpty()
				&& (origin.endsWith(".vercel.app")
						|| origin.startsWith("http://localhost:")
						|| origin.startsWith("http://127.0.0.1:"));
		if (allow) {
			response.setHeader("Access-Control-Allow-Origin", origin);
		} else if (origin == null || origin.isEmpty()) {
			// No origin header (e.g. same-origin or server-to-server) — no header needed
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
