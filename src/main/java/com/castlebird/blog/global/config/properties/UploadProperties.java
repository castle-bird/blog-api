package com.castlebird.blog.global.config.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.upload")
public record UploadProperties(
    @NotBlank(message = "이미지 저장 base-path는 필수입니다.")
    String basePath,

    @NotBlank(message = "이미지 접근 base-url은 필수입니다.")
    String baseUrl
) {

}
