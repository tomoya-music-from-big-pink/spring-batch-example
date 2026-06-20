package com.example.demo.configuration;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ItemProcessListener;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.validator.BeanValidatingItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.domain.Member;
import com.example.demo.domain.MemberWithFullName;
import com.example.demo.listener.RegisteredRecordLoggingListener;
import com.example.demo.listener.ValidationErrorLoggingListener;

@Configuration
@EnableJdbcJobRepository
public class BatchConfiguration {

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
					return new MemberWithFullName(item.id(), item.firstName(), item.lastName(),
							String.format("%s %s", item.firstName(), item.lastName()));
				}));
	}

	@Bean
	public ItemWriter<MemberWithFullName> itemWriter(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<MemberWithFullName>().dataSource(dataSource).sql(
				"INSERT INTO member (id, first_name, last_name, full_name) VALUES (:id, :firstName, :lastName, :fullName)")
				.beanMapped().build();
	}

	@Bean
	public ItemProcessListener<Member, MemberWithFullName> itemProcessListener() {
		return new ValidationErrorLoggingListener();
	}

	@Bean
	public StepExecutionListener stepExecutionListener(DataSource dataSource) {
		return new RegisteredRecordLoggingListener(dataSource);
	}

	@Bean
	public Step memberStep(JobRepository jobRepository, ItemReader<Member> itemReader,
			ItemProcessor<Member, MemberWithFullName> itemProcessor,
			ItemProcessListener<Member, MemberWithFullName> itemProcessListener,
			ItemWriter<MemberWithFullName> itemWriter, StepExecutionListener stepExecutionListener) {
		return new StepBuilder("memberStep", jobRepository).<Member, MemberWithFullName>chunk(2).reader(itemReader)
				.processor(itemProcessor).listener(itemProcessListener).writer(itemWriter)
				.listener(stepExecutionListener).faultTolerant().retryLimit(1).skip(ValidationException.class).build();
	}

	@Bean
	public Job memberJob(JobRepository jobRepository, Step step) {
		return new JobBuilder("memberJob", jobRepository).start(step).build();
	}

}
