package com.eking.momp.config;

import com.eking.momp.common.bean.ErrorResponse;
import com.eking.momp.common.service.LocaleService;
import com.eking.momp.org.dto.RoleDto;
import com.eking.momp.org.dto.UserDto;
import com.eking.momp.org.service.RoleService;
import com.eking.momp.org.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private LocaleService localeService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
             .cors()
                .and()
             .formLogin()
                .loginPage("/api/v1/login")
                .permitAll()
                .successHandler((request, response, authentication) -> {
                    String username = authentication.getName();
                    UserDto user = userService.getByUsername(username);
                    RoleDto role = roleService.getById(user.getRoleId());
                    user.setRole(role);

                    writeResponse(response, HttpStatus.OK, user);
                })
                .failureHandler((request, response, exception) ->
                        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "usernameOrPasswordWrong"))
                .and()
             .logout()
                .permitAll()
                .logoutUrl("/api/v1/logout")
                .logoutSuccessHandler((request, response, authentication) ->
                        writeResponse(response, HttpStatus.OK, authentication.getName()))
                .and()
             .authorizeRequests()
                .antMatchers("/",
                        "/instances",
                        "/actuator/**",
                        "/v2/**",//swagger
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/swagger-ui.html/**")
                .permitAll()
                .anyRequest().authenticated()
                .and()
             .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) ->
                        writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "noLogin"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeErrorResponse(response, HttpStatus.FORBIDDEN, "noPermission"));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        super.configure(auth);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String errorMsg)
            throws IOException {
        String msg = localeService.getMessage(errorMsg);
        ErrorResponse errorResponse = ErrorResponse.of(msg);
        writeResponse(response, httpStatus, errorResponse);
    }

    private void writeResponse(HttpServletResponse response, HttpStatus httpStatus, Object content) throws IOException {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json;charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(content);
        PrintWriter out = response.getWriter();
        out.write(json);
    }
}
