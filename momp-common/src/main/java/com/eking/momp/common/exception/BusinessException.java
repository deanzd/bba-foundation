package com.eking.momp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String message;

}
