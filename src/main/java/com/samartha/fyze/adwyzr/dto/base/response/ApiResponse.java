package com.samartha.fyze.adwyzr.dto.base.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

	T data;

	String message;

	ErrorDetails error;

}
