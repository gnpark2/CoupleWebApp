package com.coupleapp.coupleservice.config;
import com.coupleapp.common.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
@Configuration public class JwtConfig {
    @Bean public JwtTokenProvider jwtTokenProvider(@Value("${jwt.secret}") String s,@Value("${jwt.access-expiry-ms}") long a,@Value("${jwt.refresh-expiry-ms}") long r){return new JwtTokenProvider(s,a,r);}
}
