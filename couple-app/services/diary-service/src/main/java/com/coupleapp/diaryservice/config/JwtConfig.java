package com.coupleapp.diaryservice.config;

import com.coupleapp.common.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiry-ms}") long accessMs,
            @Value("${jwt.refresh-expiry-ms}") long refreshMs) {
        return new JwtTokenProvider(secret, accessMs, refreshMs);
    }
}
