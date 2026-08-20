package com.castlebird.blog.global.config;

import com.castlebird.blog.global.config.properties.UploadProperties;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final UploadProperties uploadProperties;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String location = "file:" + Path.of(uploadProperties.basePath()).toAbsolutePath() + "/";

    registry.addResourceHandler("/images/**")
        .addResourceLocations(location);
  }
}
