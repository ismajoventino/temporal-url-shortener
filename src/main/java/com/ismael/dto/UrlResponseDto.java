package com.ismael.dto;

import java.time.Instant;

public record UrlResponseDto(
		
		String originalUrl,
		String shortUrl,
		Instant expiresAt
		
) {}
