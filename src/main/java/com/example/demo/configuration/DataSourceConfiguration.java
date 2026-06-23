package com.example.demo.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.datasource.MemberRoutingDataSource;

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

	@Bean
	public MemberRoutingDataSource memberRoutingDataSource() {
		return new MemberRoutingDataSource();
	}

	@Bean("businessDataSource")
	@StepScope
	public DataSource businessDataSource() {
		return memberRoutingDataSource().determineTargetDataSource();
	}

	@Bean("entityManagerFactory")
	// @StepScope
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(businessDataSource());
		emf.setPackagesToScan("com.example.demo.domain");
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		emf.setJpaProperties(jpaProperties);

		return emf;
	}

	@Bean("businessTransactionManager")
	// @StepScope
	public PlatformTransactionManager businessTransactionManager() {
		return new JpaTransactionManager(entityManagerFactory().getObject());
	}

}
