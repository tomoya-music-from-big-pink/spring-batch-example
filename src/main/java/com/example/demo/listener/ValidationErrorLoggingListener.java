package com.example.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.ItemProcessListener;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import com.example.demo.domain.Member;
import com.example.demo.domain.MemberWithFullName;

public class ValidationErrorLoggingListener implements ItemProcessListener<Member, MemberWithFullName> {

	private static final Logger logger = LoggerFactory.getLogger(ValidationErrorLoggingListener.class);

	@Override
	public void onProcessError(Member item, Exception e) {
		if (e.getCause() instanceof ValidationException) {
			ValidationException validationException = (ValidationException) e.getCause();
			BindException errors = (BindException) validationException.getCause();
			for (FieldError fieldError : errors.getFieldErrors()) {
				logger.warn("validation error! item = {}, mesage = {}", fieldError.getField(),
						fieldError.getDefaultMessage());
			}
		}
	}

}
