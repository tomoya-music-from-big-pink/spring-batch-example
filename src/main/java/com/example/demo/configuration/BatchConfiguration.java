package com.example.demo.configuration;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.CompositeStepExecutionListener;
import org.springframework.batch.core.listener.ItemProcessListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.domain.Member;
import com.example.demo.domain.MemberWithFullName;
import com.example.demo.listener.RegisteredRecordLoggingListener;
import com.example.demo.listener.ValidationErrorLoggingListener;
import com.example.demo.listener.WorkerStepExecutionListener;
import com.example.demo.partitioner.ReservationsDevidePartitioner;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJdbcJobRepository
@Import(value = DataSourceConfiguration.class)
public class BatchConfiguration {

	@Bean
	public Partitioner partitioner(@Qualifier("dataSource") DataSource dataSource) {
		return new ReservationsDevidePartitioner(dataSource);
	}

	@Bean
	public FlatFileItemReader<Member> itemReader() {
		return new FlatFileItemReaderBuilder<Member>().name("memberReader")
				.resource(new ClassPathResource("bon_jovi.csv")).delimited().names("id", "firstName", "lastName")
				.targetType(Member.class).linesToSkip(1).build();
	}

	@Bean
	public ItemProcessor<Member, MemberWithFullName> itemProcessor() throws Exception {
		BeanValidatingItemProcessor<Member> beanValidatingItemProcessor = new BeanValidatingItemProcessor<>();
		beanValidatingItemProcessor.setFilter(false);
		beanValidatingItemProcessor.afterPropertiesSet();

		return new CompositeItemProcessor<Member, MemberWithFullName>(
				List.of(beanValidatingItemProcessor, (ItemProcessor<Member, MemberWithFullName>) item -> {
					return new MemberWithFullName(Integer.parseInt(item.id()), item.firstName(), item.lastName(),
							String.format("%s %s", item.firstName(), item.lastName()));
				}));
	}

	@Bean
	public ItemWriter<MemberWithFullName> itemWriter(EntityManagerFactory entityManagerFactory) {
		return new JpaItemWriterBuilder<MemberWithFullName>().entityManagerFactory(entityManagerFactory)
				.usePersist(true).build();
	}

	@Bean
	public ItemProcessListener<Member, MemberWithFullName> itemProcessListener() {
		return new ValidationErrorLoggingListener();
	}

	@Bean
	public StepExecutionListener stepExecutionListener(@Qualifier("businessDataSource") DataSource businessDataSource) {
		StepExecutionListener[] listeners = new StepExecutionListener[] { new WorkerStepExecutionListener(),
				new RegisteredRecordLoggingListener(businessDataSource) };
		CompositeStepExecutionListener stepExecutionListener = new CompositeStepExecutionListener();
		stepExecutionListener.setListeners(listeners);

		return stepExecutionListener;
	}

	@Bean("leaderStep")
	public Step leaderStep(JobRepository jobRepository, Partitioner partitioner,
			@Qualifier("workerStep") Step workerStep) {
		return new StepBuilder("leaderStep", jobRepository).partitioner("workerStep", partitioner).step(workerStep)
				.build();
	}

	@Bean("workerStep")
	public Step workerStep(JobRepository jobRepository,
			@Qualifier("businessTransactionManager") PlatformTransactionManager businessTransactionManager,
			ItemReader<Member> itemReader, ItemProcessor<Member, MemberWithFullName> itemProcessor,
			ItemProcessListener<Member, MemberWithFullName> itemProcessListener,
			ItemWriter<MemberWithFullName> itemWriter, StepExecutionListener stepExecutionListener) {
		return new StepBuilder("workerStep", jobRepository).<Member, MemberWithFullName>chunk(2)
				.transactionManager(businessTransactionManager).reader(itemReader).processor(itemProcessor)
				.listener(itemProcessListener).writer(itemWriter).listener(stepExecutionListener).faultTolerant()
				.retryLimit(1).skip(ValidationException.class).build();
	}

	@Bean
	public Job memberJob(JobRepository jobRepository, @Qualifier("leaderStep") Step step) {
		return new JobBuilder("memberJob", jobRepository).start(step).build();
	}

}
