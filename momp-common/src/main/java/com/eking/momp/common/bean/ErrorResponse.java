package com.eking.momp.common.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ErrorResponse {
	private String message;

	private ErrorResponse(String message) {
		this.message = message;
	}

	public static ErrorResponse of(String message) {
		log.error(message);
		return new ErrorResponse(message);
	}

}
