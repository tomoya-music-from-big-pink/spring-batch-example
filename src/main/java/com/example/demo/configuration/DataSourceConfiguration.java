package com.example.demo.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DataSourceConfiguration {

	@Bean("dataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean("transactionManager")
	@Primary
	public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean("businessDataSource")
	@ConfigurationProperties(prefix = "spring.business.datasource")
	public DataSource businessDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean("businessTransactionManaber")
	public PlatformTransactionManager businessTransactionManager(
			@Qualifier("businessDataSource") DataSource businessDataSource) {
		return new DataSourceTransactionManager(businessDataSource);
	}

}
