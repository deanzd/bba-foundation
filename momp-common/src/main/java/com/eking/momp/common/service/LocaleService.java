package com.eking.momp.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LocaleService {

    @Autowired
    private MessageSource messageSource;

    public String getLabel(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("label." + code, null, code, locale);
    }

    public String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("message." + code, args, code, locale);
    }

    public String getMenu(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("menu." + code, null, code, locale);
    }
}
