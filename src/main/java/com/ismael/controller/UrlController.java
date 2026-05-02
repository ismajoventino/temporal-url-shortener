package com.ismael.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ismael.dto.UrlRequestDto;
import com.ismael.dto.UrlResponseDto;
import com.ismael.service.UrlService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class UrlController {

	private final UrlService urlService;
	
	public UrlController(UrlService urlService) {
		this.urlService = urlService;
	}
	
	@PostMapping("/api/urls/shorten")
	public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody UrlRequestDto request){
		UrlResponseDto response = urlService.shortenUrl(request);
		
		String baseUrl =  ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .build()
                .toUriString();
		
		String completeUrl = baseUrl + "/" + response.shortUrl();
		
		UrlResponseDto responseWithFullUrl = new UrlResponseDto(
                response.originalUrl(),
                completeUrl,
                response.expiresAt(),
                0
        );
		
		return ResponseEntity.status(HttpStatus.CREATED).body(responseWithFullUrl);
	}
	
	@GetMapping("/api/urls/{hash}")
	public ResponseEntity<UrlResponseDto> getUrlStats(@PathVariable String hash) {
	    UrlResponseDto stats = urlService.getUrlStats(hash);
	    return ResponseEntity.ok(stats);
	}
	
	@GetMapping("/{hash}")
	public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash){
		
		String originalUrl = urlService.getOriginalUrl(hash);
		
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
	}
	
	
}
