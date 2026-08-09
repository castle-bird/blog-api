package com.castlebird.blog.global.config;

import com.castlebird.blog.global.config.properties.AdminProperties;
import com.castlebird.blog.global.config.properties.JwtProperties;
import com.castlebird.blog.global.config.properties.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@EnableConfigurationProperties({
    SecurityProperties.class,
    JwtProperties.class,
    AdminProperties.class
})
public class AppConfig {

}
