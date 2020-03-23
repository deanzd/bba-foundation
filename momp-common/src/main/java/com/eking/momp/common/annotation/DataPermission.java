package com.eking.momp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DataPermission {
	Source source();
	
	String key() default "id";
	
	enum Source {
		USER,
		MODEL,
		ENTITY
	}
}
