package com.castlebird.blog.user.dto.request;

import com.castlebird.blog.user.entity.UserRole;

public record CreateAdminRequest(
  String username,
  String password,
  String email,
  String nickname,
  UserRole role
) {}
