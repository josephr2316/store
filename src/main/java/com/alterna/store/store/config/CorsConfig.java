package com.alterna.store.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

	/** Comma-separated origin patterns; default allows all + Vercel. Example: https://myapp.vercel.app,https://*.vercel.app */
	@Value("${app.cors.allowed-origin-patterns:*}")
	private String allowedOriginPatterns = "*";

	@Value("${app.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}")
	private String allowedMethods = "GET,POST,PUT,PATCH,DELETE,OPTIONS";

	@Value("${app.cors.allowed-headers:*}")
	private String allowedHeaders = "*";

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		List<String> patterns = "*".equals(allowedOriginPatterns.trim())
			? List.of("*", "https://*.vercel.app", "https://*--*.vercel.app", "http://localhost:*", "http://127.0.0.1:*")
			: Arrays.stream(allowedOriginPatterns.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
		config.setAllowedOriginPatterns(patterns);
		config.setAllowedMethods(List.of(allowedMethods.split(",")));
		config.setAllowedHeaders(List.of(allowedHeaders.split(",")));
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
