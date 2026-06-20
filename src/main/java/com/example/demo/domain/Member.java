package com.example.demo.domain;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record Member(@NotBlank String id, @NotBlank @Length(max = 20) String firstName,
		@NotBlank @Length(max = 20) String lastName) {
}
