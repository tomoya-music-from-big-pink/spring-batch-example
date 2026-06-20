package com.example.demo.listener;

import java.util.List;

import javax.sql.DataSource;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.domain.MemberWithFullName;

public class RegisteredRecordLoggingListener implements StepExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(RegisteredRecordLoggingListener.class);

	private final JdbcOperations jdbcOperations;

	public RegisteredRecordLoggingListener(DataSource dataSource) {
		this.jdbcOperations = new JdbcTemplate(dataSource);
	}

	@Override
	public @Nullable ExitStatus afterStep(StepExecution stepExecution) {
		String sql = "SELECT id, first_name, last_name, full_name FROM member ORDER BY id";
		List<MemberWithFullName> resultList = this.jdbcOperations.query(sql, (rs, rowNum) -> {
			return new MemberWithFullName(rs.getString("id"), rs.getString("first_name"), rs.getString("last_name"),
					rs.getString("full_name"));
		});

		for (MemberWithFullName result : resultList) {
			logger.info(result.toString());
		}

		return ExitStatus.COMPLETED;
	}

}
