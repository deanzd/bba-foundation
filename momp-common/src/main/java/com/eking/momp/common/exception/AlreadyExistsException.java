package com.eking.momp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String label;
}
