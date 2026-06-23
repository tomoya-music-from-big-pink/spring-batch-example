package com.example.demo.listener;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;

import com.example.demo.datasource.MemberRoutingDataSource;

public class WorkerStepExecutionListener implements StepExecutionListener {

	private final MemberRoutingDataSource memberRoutingDataSource;

	public WorkerStepExecutionListener(MemberRoutingDataSource memberRoutingDataSource) {
		this.memberRoutingDataSource = memberRoutingDataSource;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution.getExecutionContext();

		String dbname = executionContext.getString("dbname");
		String host = executionContext.getString("host");
		String username = executionContext.getString("username");
		String password = executionContext.getString("password");

		this.memberRoutingDataSource.addDataSource(dbname, host, username, password);

		MemberRoutingDataSource.setDataSourceKey(dbname);
	}

	@Override
	public @Nullable ExitStatus afterStep(StepExecution stepExecution) {
		MemberRoutingDataSource.removeDataSourceKey();

		return ExitStatus.COMPLETED;
	}

}
