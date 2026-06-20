package com.example.demo.configuration;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.domain.Member;
import com.example.demo.domain.MemberWithFullName;

@Configuration
public class BatchConfiguration {

	@Bean
	public FlatFileItemReader<Member> itemReader() {
		return new FlatFileItemReaderBuilder<Member>().name("memberReader")
				.resource(new ClassPathResource("bon_jovi.csv")).delimited().names("id", "firstName", "lastName")
				.targetType(Member.class).linesToSkip(1).build();
	}

	@Bean
	public ItemProcessor<Member, MemberWithFullName> itemProcessor() {
		return item -> {
			return new MemberWithFullName(item.id(), item.firstName(), item.lastName(),
					String.format("%s %s", item.firstName(), item.lastName()));
		};
	}

	@Bean
	public ItemWriter<MemberWithFullName> itemWriter() {
		return chunk -> {
			chunk.forEach(System.out::println);
		};
	}

	@Bean
	public Step memberStep(JobRepository jobRepository, ItemReader<Member> itemReader,
			ItemProcessor<Member, MemberWithFullName> itemProcessor, ItemWriter<MemberWithFullName> itemWriter) {
		return new StepBuilder("memberStep", jobRepository).<Member, MemberWithFullName>chunk(2).reader(itemReader)
				.processor(itemProcessor)
				.writer(itemWriter).build();
	}

	@Bean
	public Job memberJob(JobRepository jobRepository, Step step) {
		return new JobBuilder("memberJob", jobRepository).start(step).build();
	}

}
