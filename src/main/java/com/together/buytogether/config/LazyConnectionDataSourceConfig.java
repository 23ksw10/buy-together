package com.together.buytogether.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app.datasource.lazy-proxy", name = "enabled", havingValue = "true")
public class LazyConnectionDataSourceConfig {

	@Bean
	@ConfigurationProperties("spring.datasource.hikari")
	public HikariDataSource hikariDataSource(DataSourceProperties dataSourceProperties) {
		log.info("Creating real HikariDataSource");
		return dataSourceProperties.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean
	@Primary
	public DataSource dataSource(HikariDataSource hikariDataSource) {
		log.info("Wrapping HikariDataSource with LazyConnectionDataSourceProxy");
		return new LazyConnectionDataSourceProxy(hikariDataSource);
	}
}
