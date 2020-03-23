package com.eking.momp.common.exception;

import com.eking.momp.common.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class InUsedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ResourceType resourceType;

	private Serializable dataName;

	private ResourceType refResourceType;

	private Serializable refResourceName;

}
