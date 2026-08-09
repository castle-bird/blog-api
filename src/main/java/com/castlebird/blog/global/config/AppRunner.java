package com.castlebird.blog.global.config;

import com.castlebird.blog.user.dto.request.CreateAdminRequest;
import com.castlebird.blog.user.entity.User;
import com.castlebird.blog.user.entity.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.castlebird.blog.global.config.properties.AdminProperties;
import com.castlebird.blog.user.mapper.UserMapper;
import com.castlebird.blog.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

  private final UserRepository userRepository;
  private final AdminProperties adminProperties;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {

    User user = userRepository.findByEmail(adminProperties.email()).orElse(null);

    if (user == null) {
      String encodedPassword = passwordEncoder.encode(adminProperties.password());

      CreateAdminRequest createAdminRequest = new CreateAdminRequest(
          adminProperties.username(),
          encodedPassword,
          adminProperties.email(),
          adminProperties.nickname(),
          UserRole.ADMIN
      );

      User admin = userMapper.toUserFromAdminRequest(createAdminRequest);

      userRepository.save(admin);
    }
  }
}
