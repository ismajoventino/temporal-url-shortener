package com.ismael.dto;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UrlRequestDto(
		
		@NotBlank(message = "URL cannot be empty")
		@URL(message = "Invalid URL format")
		String originalUrl,
		
		@NotNull(message = "Expiration time is required")
		@Positive(message = "Expiration time must be a positive number of hours")
		Long expirationHours
		
) {}
