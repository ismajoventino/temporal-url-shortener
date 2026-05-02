package com.ismael.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ismael.dto.ErrorResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException exception) {
	        
	        ErrorResponseDto error = new ErrorResponseDto(exception.getMessage(), 400);
	
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	    }
	
}
