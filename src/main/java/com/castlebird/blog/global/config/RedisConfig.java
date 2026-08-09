package com.castlebird.blog.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisConfig {

  // Redis 캐시 이름별 캐시 시간을 다르게 할때 사용한다.
//  @Bean
//  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
//    return builder -> builder
//        .withCacheConfiguration(
//            "캐시 이름",
//            RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(10))
//        );
//
//  }

  @Bean
  public RedisScript<Void> createRefreshTokenScript() {
    return RedisScript.of(
        new ClassPathResource("redis/refreshToken/create-refresh-token.lua")
    );
  }

  @Bean
  public RedisScript<Long> rotateRefreshTokenScript() {
    return RedisScript.of(
        new ClassPathResource("redis/refreshToken/rotate-refresh-token.lua"),
        Long.class
    );
  }

  @Bean
  public RedisScript<Void> removeRefreshTokenScript() {
    return RedisScript.of(
        new ClassPathResource("redis/refreshToken/remove-refresh-token.lua")
    );
  }
}
