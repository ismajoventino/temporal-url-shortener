package com.ismael.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ismael.dto.UrlRequestDto;
import com.ismael.dto.UrlResponseDto;
import com.ismael.service.UrlService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

	private final UrlService urlService;
	
	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}
	
	@PostMapping("/shorten")
	public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody UrlRequestDto request){
		UrlResponseDto response = urlService.shortenUrl(request);
		
		String baseUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();
		
		String completeUrl = baseUrl + "/" + response.shortUrl();
		
		UrlResponseDto responseWithFullUrl = new UrlResponseDto(
                response.originalUrl(),
                completeUrl,
                response.expiresAt()
        );
		
		return ResponseEntity.status(HttpStatus.CREATED).body(responseWithFullUrl);
	}
	
	
}
