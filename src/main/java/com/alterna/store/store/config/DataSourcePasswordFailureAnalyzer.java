package com.alterna.store.store.config;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * When the app fails with "no password was provided" (PostgreSQL), suggests using
 * profile "local" and application-local.properties so the error is clear.
 */
public class DataSourcePasswordFailureAnalyzer extends AbstractFailureAnalyzer<Throwable> {

	private static final String NO_PASSWORD_MSG = "no password was provided";

	@Override
	protected FailureAnalysis analyze(Throwable rootFailure, Throwable cause) {
		if (!hasNoPasswordMessage(rootFailure)) {
			return null;
		}
		String description = "The database connection failed: no password was provided.";
		String action = "For local run from IntelliJ: 1) Copy src/main/resources/application-local.properties.example "
				+ "to src/main/resources/application-local.properties. "
				+ "2) Fill in spring.datasource.password (and URL/username if using Supabase). "
				+ "3) In IntelliJ: Run → Edit Configurations → Active profiles = 'local'. Then run again.";
		return new FailureAnalysis(description, action, rootFailure);
	}

	private boolean hasNoPasswordMessage(Throwable t) {
		Throwable current = t;
		while (current != null) {
			if (current.getMessage() != null && current.getMessage().contains(NO_PASSWORD_MSG)) {
				return true;
			}
			current = current.getCause();
		}
		return false;
	}
}
