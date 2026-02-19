package com.alterna.store.store.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Ensures prepareThreshold=0 is set on HikariCP DataSource for pgBouncer/Supabase compatibility.
 * Primary fix is in application.properties (spring.datasource.hikari.data-source-properties.prepareThreshold=0).
 * This bean runs with highest precedence so the URL is fixed before any other processor or pool init.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataSourcePoolerFixConfig implements BeanPostProcessor {

	/**
	 * Runs BEFORE bean initialisation (before HikariCP creates the connection pool),
	 * ensuring prepareThreshold=0 is applied in time.
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof HikariDataSource hikari) {
			String url = hikari.getJdbcUrl();
			// Only apply to PostgreSQL connections (not H2 in tests, not other JDBC vendors)
			if (url != null && url.startsWith("jdbc:postgresql:")) {
				if (!url.contains("prepareThreshold")) {
					String fixed = url.contains("?") ? url + "&prepareThreshold=0" : url + "?prepareThreshold=0";
					hikari.setJdbcUrl(fixed);
				}
				// Pass as connection property so DriverManager also receives it
				try {
					hikari.addDataSourceProperty("prepareThreshold", "0");
				} catch (IllegalStateException ignored) {
					// Config may already be sealed if pool started before BeanPostProcessor ran
				}
			}
		}
		return bean;
	}
}
