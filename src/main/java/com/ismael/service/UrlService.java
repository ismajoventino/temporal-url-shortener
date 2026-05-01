package com.ismael.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.ismael.dto.UrlRequestDto;
import com.ismael.dto.UrlResponseDto;
import com.ismael.entity.UrlEntity;
import com.ismael.repository.UrlRepository;

@Service
public class UrlService {

	private UrlRepository urlRepository;
	
	public UrlService (UrlRepository urlRepository) {
		this.urlRepository = urlRepository;
	}
	
	private String generateRandomString(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder result = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			result.append(characters.charAt(random.nextInt(characters.length())));
		}
		
		return result.toString();
	}
	
	private String generateUniqueHash() {
		String hash;
		
		do {
			hash = generateRandomString(6);
		} while (urlRepository.findByShortHash(hash).isPresent());
		
		return hash;
	}
	
	public UrlResponseDto shortenUrl(UrlRequestDto request) {
		String shortUrl = generateUniqueHash();
		Instant createdAt = Instant.now();
		Instant expiresAt = createdAt.plus(request.expirationHours(), ChronoUnit.HOURS);
		
		UrlEntity entity = new UrlEntity();
		
		entity.setOriginalUrl(request.originalUrl());
		entity.setCreatedAt(createdAt);
		entity.setExpiresAt(expiresAt);
		entity.setShortHash(shortUrl);
		entity.setClickCount(0);
		
		urlRepository.save(entity);
		
		UrlResponseDto response = new UrlResponseDto(request.originalUrl(), shortUrl, expiresAt);
		
		return response;
	}
}
