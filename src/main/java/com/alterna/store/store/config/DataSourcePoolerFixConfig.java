package com.alterna.store.store.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Ensures PostgreSQL JDBC URL has prepareThreshold=0 when using a connection pooler
 * (e.g. Supabase, PgBouncer). Avoids "prepared statement already exists" / JDBC prepare errors.
 */
@Component
public class DataSourcePoolerFixConfig implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof HikariDataSource hikari) {
			String url = hikari.getJdbcUrl();
			if (url != null && url.startsWith("jdbc:postgresql:") && !url.contains("prepareThreshold")) {
				String fixed = url.contains("?") ? url + "&prepareThreshold=0" : url + "?prepareThreshold=0";
				hikari.setJdbcUrl(fixed);
			}
		}
		return bean;
	}
}
