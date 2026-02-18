package com.alterna.store.store.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Ensures prepareThreshold=0 is set on HikariCP DataSource for pgBouncer/Supabase compatibility.
 * Primary fix is in application.properties (spring.datasource.hikari.data-source-properties.prepareThreshold=0).
 * This bean acts as a secondary failsafe applied BEFORE HikariCP initialises the pool.
 */
@Component
public class DataSourcePoolerFixConfig implements BeanPostProcessor {

	/**
	 * Runs BEFORE bean initialisation (before HikariCP creates the connection pool),
	 * ensuring prepareThreshold=0 is applied in time.
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof HikariDataSource hikari) {
			String url = hikari.getJdbcUrl();
			if (url != null && url.startsWith("jdbc:postgresql:") && !url.contains("prepareThreshold")) {
				String fixed = url.contains("?") ? url + "&prepareThreshold=0" : url + "?prepareThreshold=0";
				hikari.setJdbcUrl(fixed);
			}
			// Also force via DataSource properties as belt-and-suspenders
			hikari.addDataSourceProperty("prepareThreshold", "0");
		}
		return bean;
	}
}
