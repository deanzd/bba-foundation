package com.eking.momp.common;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

public class UserContextHoder {
	public static UserContext getUserContext() {
		return (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	public static String getUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public static List<String> getRoles() {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(auth -> auth.startsWith("ROLE_"))
				.map(role -> role.substring(5))
				.collect(Collectors.toList());
	}
}
