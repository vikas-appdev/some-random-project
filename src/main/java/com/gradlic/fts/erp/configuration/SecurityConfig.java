package com.gradlic.fts.erp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    private static final String[] PUBLIC_URL = {};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf().disable().cors().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests().requestMatchers(PUBLIC_URL).permitAll();
        http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasAnyAuthority("DELETE:USER");
        http.authorizeHttpRequests().requestMatchers(HttpMethod.DELETE, "/customer/delete/**").hasAnyAuthority("DELETE:CUSTOMER");
        http.exceptionHandling().accessDeniedHandler(null).authenticationEntryPoint(null);
        http.authorizeHttpRequests().anyRequest().authenticated();
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return null;
    }
}
