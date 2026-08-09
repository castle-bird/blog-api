package com.castlebird.blog.auth.service.impl;

import com.castlebird.blog.auth.service.RefreshTokenRedisService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenRedisServiceImpl implements RefreshTokenRedisService {

  private static final int MAX_REFRESH_TOKEN_SESSIONS = 3;
  private static final String TOKEN_KEY_PREFIX = "rt:token:";
  private static final String SESSION_KEY_PREFIX = "rt:session:";
  private static final String USER_SESSIONS_KEY_PREFIX = "rt:user:";

  private final RedisTemplate<String, String> redisTemplate;
  private final RedisScript<Void> createRefreshTokenScript;
  private final RedisScript<Long> rotateRefreshTokenScript;
  private final RedisScript<Void> removeRefreshTokenScript;

  public RefreshTokenRedisServiceImpl(
      RedisTemplate<String, String> redisTemplate,
      @Qualifier("createRefreshTokenScript")
      RedisScript<Void> createRefreshTokenScript,
      @Qualifier("rotateRefreshTokenScript")
      RedisScript<Long> rotateRefreshTokenScript,
      @Qualifier("removeRefreshTokenScript")
      RedisScript<Void> removeRefreshTokenScript
  ) {
    this.redisTemplate = redisTemplate;
    this.createRefreshTokenScript = createRefreshTokenScript;
    this.rotateRefreshTokenScript = rotateRefreshTokenScript;
    this.removeRefreshTokenScript = removeRefreshTokenScript;
  }

  @Override
  public void createRefreshTokenSession(
      Long userId,
      String tokenHash,
      Instant expiresAt
  ) {
    checkExpireTime(expiresAt);
    String sessionId = UUID.randomUUID().toString();

    redisTemplate.execute(
        createRefreshTokenScript,
        List.of(
            TOKEN_KEY_PREFIX + tokenHash,
            SESSION_KEY_PREFIX + sessionId,
            USER_SESSIONS_KEY_PREFIX + userId + ":sessions"
        ),
        sessionId,
        String.valueOf(userId),
        tokenHash,
        String.valueOf(expiresAt.getEpochSecond()),
        String.valueOf(MAX_REFRESH_TOKEN_SESSIONS),
        TOKEN_KEY_PREFIX,
        SESSION_KEY_PREFIX
    );
  }

  @Override
  public Long rotateRefreshToken(
      String currentTokenHash,
      String newTokenHash,
      Instant newExpiresAt
  ) {
    checkExpireTime(newExpiresAt);

    return redisTemplate.execute(
        rotateRefreshTokenScript,
        List.of(
            TOKEN_KEY_PREFIX + currentTokenHash,
            TOKEN_KEY_PREFIX + newTokenHash
        ),
        currentTokenHash,
        newTokenHash,
        String.valueOf(newExpiresAt.getEpochSecond()),
        SESSION_KEY_PREFIX,
        USER_SESSIONS_KEY_PREFIX
    );
  }

  @Override
  public void removeRefreshTokenSession(String tokenHash) {
    redisTemplate.execute(
        removeRefreshTokenScript,
        List.of(
            TOKEN_KEY_PREFIX + tokenHash
        ),
        SESSION_KEY_PREFIX,
        USER_SESSIONS_KEY_PREFIX
    );
  }

  /**
   * Refresh Token의 만료 시간을 검증한다.
   *
   * @param expiresAt Refresh Token 만료시각.
   */
  private void checkExpireTime(Instant expiresAt) {
    if (!expiresAt.isAfter(Instant.now())) {
      throw new IllegalArgumentException("Refresh Token 만료 시각은 현재보다 미래여야 합니다.");
    }
  }
}
