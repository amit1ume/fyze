package com.samartha.fyze.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.samartha.fyze.adwyzr.dto.base.response.ApiResponse;
import com.samartha.fyze.adwyzr.dto.base.response.ErrorDetails;
import com.samartha.fyze.adwyzr.dto.base.response.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private static final String API_ERROR_MESSAGE = "API Error";

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.error("GlobalExceptionHandler: MethodArgumentNotValidException: {}", ex.getMessage(), ex);

		// Extracting field errors and constructing a list of ValidationError objects
		List<ValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> ValidationError.builder()
						.code(fieldError.getCode() != null ? fieldError.getCode() : "UNKNOWN")
						.field(fieldError.getField()).desc(fieldError.getDefaultMessage() != null
								? fieldError.getDefaultMessage() : "No description")
						.rejectedValue(fieldError.getRejectedValue()).build())
				.toList();

		ApiResponse<Void> body = ApiResponse.<Void>builder().message(API_ERROR_MESSAGE)
				.error(new ErrorDetails("InvalidRequest", ex.getMessage())).build();

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
		log.error("GlobalExceptionHandler: HttpMessageNotReadableException: {}", ex.getMessage(), ex);
		Throwable cause = ex.getCause();

		// Handle invalid enum or value format exception
		if (cause instanceof InvalidFormatException) {
			InvalidFormatException invalidFormatException = (InvalidFormatException) cause;
			String fieldName = invalidFormatException.getPath().isEmpty() ? "Unknown Field"
					: invalidFormatException.getPath().get(0).getFieldName();
			String validValues = invalidFormatException.getTargetType().getEnumConstants() != null
					? String.join(", ", (CharSequence[]) invalidFormatException.getTargetType().getEnumConstants())
					: "Unknown Values";
			String errorMessage = String.format("Invalid value '%s' for field '%s'. Accepted values are [%s]",
					invalidFormatException.getValue(), fieldName, validValues);
			ApiResponse<Void> body = ApiResponse.<Void>builder().message(API_ERROR_MESSAGE)
					.error(new ErrorDetails("InvalidRequest", errorMessage)).build();
			return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
		}

		// Handle missing non-nullable parameter exception
		if (cause instanceof MismatchedInputException) {
			MismatchedInputException mismatchedInputException = (MismatchedInputException) cause;
			String fieldName = mismatchedInputException.getPath().isEmpty() ? "unknown field"
					: mismatchedInputException.getPath().get(0).getFieldName();
			String errorMessage = String.format("Missing required field '%s'. This field cannot be null.", fieldName);
			ApiResponse<Void> body = ApiResponse.<Void>builder().message(API_ERROR_MESSAGE)
					.error(new ErrorDetails("InvalidRequest", errorMessage)).build();
			return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
		}

		// Default error message for other cases of HttpMessageNotReadableException
		String defaultMessage = "Malformed JSON request or invalid input";
		ApiResponse<Void> body = ApiResponse.<Void>builder().message(API_ERROR_MESSAGE)
				.error(new ErrorDetails("InvalidRequest", defaultMessage)).build();
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
		log.error("GlobalExceptionHandler: Exception: {}", ex.getMessage(), ex);
		ApiResponse<Void> body = ApiResponse.<Void>builder().message(API_ERROR_MESSAGE)
				.error(new ErrorDetails("InternalServerError", ex.getMessage())).build();
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

}
