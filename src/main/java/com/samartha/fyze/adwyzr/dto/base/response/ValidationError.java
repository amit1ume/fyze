package com.samartha.fyze.adwyzr.dto.base.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidationError {

	String code;

	String field;

	String desc;

}
