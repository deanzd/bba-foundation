package com.eking.momp.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eking.momp.common.annotation.DataPermission;
import com.eking.momp.common.annotation.DataPermission.Source;
import com.eking.momp.org.service.UserService;

@Aspect
@Component
public class DataPermissionAspect {
	@Autowired
	private UserService userService;
	
	@Before("@annotation(com.eking.momp.common.annotation.DataPermission)")
	public void before(JoinPoint point) {
		Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        DataPermission annotation = method.getAnnotation(DataPermission.class);
        String key = annotation.key();
        
        String[] argNames = methodSignature.getParameterNames();
		Object[] args = point.getArgs();
		
		Integer id = null;
		if (key.contains(".")) {
	        String[] keyArr = key.split(".");
			for (int i = 0; i < argNames.length; i++) {
				if (argNames[i].equals(keyArr[0])) {
					Object obj = args[i];
					try {
						Field field = obj.getClass().getDeclaredField(keyArr[i]);
						field.setAccessible(true);
						id = field.getInt(obj);
					} catch (Exception e) {
						throw new RuntimeException("Field not found.");
					} 
					break;
				}
			}
		} else {
			for (int i = 0; i < argNames.length; i++) {
				if (argNames[i].equals(key)) {
					id = (Integer) args[i];
					break;
				}
			}
		}
        if (id == null) {
        	throw new RuntimeException("Id not found");
        }
        Source source = annotation.source();
		if (source == Source.USER) {
		}
	}
}
