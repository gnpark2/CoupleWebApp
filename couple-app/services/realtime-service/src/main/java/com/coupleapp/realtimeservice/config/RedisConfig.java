package com.coupleapp.realtimeservice.config;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
@Configuration public class RedisConfig{@Bean public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory f){return new StringRedisTemplate(f);}
}
