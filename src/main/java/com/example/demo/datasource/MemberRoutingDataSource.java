package com.example.demo.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MemberRoutingDataSource extends AbstractRoutingDataSource {

	private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<>();

	private final Map<String, DataSource> dataSourceMap = new HashMap<>();

	public void addDataSource(String key, String connectionString) {

	}

	@Override
	protected @Nullable Object determineCurrentLookupKey() {
		return dataSourceKey.get();
	}

	public static void setDataSourceKey(String key) {
		dataSourceKey.set(key);
	}

	public static void removeDataSourceKey() {
		dataSourceKey.remove();
	}

}
