package com.castlebird.blog.global.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
    @NotEmpty(message = "CORS 허용 origin은 하나 이상 필요합니다.")
    List<@NotBlank(message = "CORS 허용 origin은 비어 있을 수 없습니다.") String>
    allowedOrigins,

    @NotNull(message = "Refresh token 만료 시간은 필수입니다.")
    Duration refreshTokenExpiration,

    @NotBlank(message = "Refresh token cookie 이름은 필수입니다.")
    String cookieName,

    @NotNull(message = "Refresh token cookie security 설정은 필수입니다.")
    Boolean cookieSecure
) {


}
