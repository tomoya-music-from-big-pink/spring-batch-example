package com.example.demo.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MemberRoutingDataSource extends AbstractRoutingDataSource {

	private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<>();

	private final Map<String, DataSource> dataSourceMap = new HashMap<>();

	public void addDataSource(String dbname, String host, String username, String password) {
		dataSourceMap.put(dbname,
				DataSourceBuilder.create().url(String.format("jdbc:postgresql://%s:5432/%s", host, dbname))
						.username(username).password(password).driverClassName("org.postgresql.Driver").build());
	}

	@Override
	public void initialize() {
		// no op
	}

	@Override
	protected @Nullable Object determineCurrentLookupKey() {
		return dataSourceKey.get();
	}

	@Override
	public DataSource determineTargetDataSource() {
		return this.dataSourceMap.get(determineCurrentLookupKey());
	}

	public static void setDataSourceKey(String key) {
		dataSourceKey.set(key);
	}

	public static void removeDataSourceKey() {
		dataSourceKey.remove();
	}

}
