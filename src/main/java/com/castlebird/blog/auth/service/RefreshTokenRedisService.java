package com.castlebird.blog.auth.service;

import java.time.Instant;

public interface RefreshTokenRedisService {

  /**
   * Redis에 Refresh Token 세션을 생성한다.
   *
   * @param userId    사용자 ID
   * @param tokenHash Refresh Token의 SHA-256 해시
   * @param expiresAt Refresh Token 만료 시각
   */
  void createRefreshTokenSession(
      Long userId,
      String tokenHash,
      Instant expiresAt
  );

  /**
   * Redis에 저장된 Refresh Token을 Rotate한다.
   *
   * @param currentTokenHash Old Refresh Token의 SHA-256 해시
   * @param newTokenHash New Refresh Token의 SHA-256 해시
   * @param newExpiresAt New Refresh Token 만료 시각
   * @return Refresh Token 세션의 사용자 ID
   */
  Long rotateRefreshToken(
      String currentTokenHash,
      String newTokenHash,
      Instant newExpiresAt
  );

  /**
   * Redis에 저장된 Refresh Token을 폐기한다.
   *
   * @param tokenHash 폐기할 Refresh Token의 SHA-256 해시
   */
  void removeRefreshTokenSession(String tokenHash);
}
