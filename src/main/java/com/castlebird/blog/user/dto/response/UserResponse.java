package com.castlebird.blog.user.dto.response;

import com.castlebird.blog.user.entity.User;

public record UserResponse(
    Long id,
    String username,
    String email,
    String nickname
) {

  public static UserResponse of(User user) {
    return new UserResponse(
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getNickname()
    );
  }
}
