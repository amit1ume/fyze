package com.samartha.fyze.common.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public abstract class BaseClass {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	@SneakyThrows
	public String toString() {
		try {
			return objectMapper.writeValueAsString(this);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException("BaseClass.toString JsonProcessingException: " + e.getMessage(), e);
		}
	}

}
