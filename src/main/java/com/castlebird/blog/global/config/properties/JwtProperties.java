package com.castlebird.blog.global.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
    @NotNull(message = "Access token 만료 시간은 필수입니다.")
    Duration accessTokenExpiration,

    @NotBlank(message = "JWT secret은 필수입니다.")
    String secret,

    @NotBlank(message = "JWT issuer는 필수입니다.")
    String issuer
) {

}
