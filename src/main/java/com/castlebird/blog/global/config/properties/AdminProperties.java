package com.castlebird.blog.global.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
    @NotBlank(message = "ADMIN 이름은 필수입니다.")
    String username,

    @NotBlank(message = "ADMIN 비밀번호는 필수입니다.")
    String password,

    @NotBlank(message = "ADMIN 이메일은 필수입니다.")
    String email,

    @NotBlank(message = "ADMIN 닉네임은 필수입니다.")
    String nickname
) {

}
