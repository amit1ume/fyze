package com.samartha.fyze.common.exception;

import com.samartha.fyze.adwyzr.dto.base.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse> handleException(Exception e) {
		return new ResponseEntity<>(ApiResponse.<Void>builder().message(e.getMessage()).build(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
