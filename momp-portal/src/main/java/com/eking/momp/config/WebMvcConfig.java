package com.eking.momp.config;

import java.util.Locale;

import com.eking.momp.common.MompProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	@Autowired
	private MompProperties mompProperties;

	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver resolver = new CookieLocaleResolver();
		resolver.setCookieName(mompProperties.getLocaleCookieName());
		resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
		return resolver;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).allowedMethods(
				RequestMethod.GET.toString(), RequestMethod.POST.toString(), RequestMethod.DELETE.toString(),
				RequestMethod.PUT.toString(), RequestMethod.PATCH.toString());
	}

}
