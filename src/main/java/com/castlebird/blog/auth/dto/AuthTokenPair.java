package com.castlebird.blog.auth.dto;

public record AuthTokenPair(
    String accessToken,
    String refreshToken
) {

  public static AuthTokenPair of(
      String accessToken,
      String refreshToken
  ) {
    return new AuthTokenPair(accessToken, refreshToken);
  }
}
