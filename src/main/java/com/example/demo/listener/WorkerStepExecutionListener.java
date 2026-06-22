package com.example.demo.listener;

import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.ExecutionContext;

public class WorkerStepExecutionListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext executionContext = stepExecution.getExecutionContext();
		// TODO Auto-generated method stub
		StepExecutionListener.super.beforeStep(stepExecution);
	}

}
