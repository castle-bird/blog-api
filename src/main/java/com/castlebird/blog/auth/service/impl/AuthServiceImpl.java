package com.castlebird.blog.auth.service.impl;

import com.castlebird.blog.auth.dto.AuthTokenPair;
import com.castlebird.blog.auth.dto.request.LoginRequest;
import com.castlebird.blog.auth.exception.AuthException;
import com.castlebird.blog.auth.exception.code.AuthErrorCode;
import com.castlebird.blog.auth.service.AuthService;
import com.castlebird.blog.auth.service.RefreshTokenRedisService;
import com.castlebird.blog.global.config.properties.SecurityProperties;
import com.castlebird.blog.global.security.token.AccessTokenProvider;
import com.castlebird.blog.global.security.token.RefreshTokenProvider;
import com.castlebird.blog.user.entity.User;
import com.castlebird.blog.user.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AccessTokenProvider accessTokenProvider;
  private final RefreshTokenProvider refreshTokenProvider;
  private final RefreshTokenRedisService refreshTokenRedisService;
  private final SecurityProperties securityProperties;

  @Override
  public AuthTokenPair login(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.email())
        .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_CREDENTIALS));

    if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
      throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
    }

    return createTokenPair(user.getId());
  }

  @Override
  public AuthTokenPair refresh(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    // Redis 탐색을 위해 Hash로 변환
    String currentTokenHash = refreshTokenProvider.hashRefreshToken(refreshToken);
    // 새로운 Refresh Token 생성 + Redis용 Hash 반환
    String newRefreshToken = refreshTokenProvider.generateRefreshToken();
    String newTokenHash = refreshTokenProvider.hashRefreshToken(newRefreshToken);
    // 유통기한
    Instant newExpiresAt = Instant.now().plus(securityProperties.refreshTokenExpiration());

    Long userId = refreshTokenRedisService.rotateRefreshToken(
        currentTokenHash,
        newTokenHash,
        newExpiresAt
    );

    if (userId == null) {
      throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }

    return AuthTokenPair.of(createAccessToken(userId), newRefreshToken);
  }

  @Override
  public void logout(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }

    refreshTokenRedisService.removeRefreshTokenSession(
        refreshTokenProvider.hashRefreshToken(refreshToken)
    );
  }

  /**
   * Access/Refresh Token을 생성하고, Redis에 Refresh Token Hash를 저장한다.
   *
   * @param userId 토큰을 발급할 사용자 ID
   * @return 새 Access Token과 Refresh Token 쌍
   */
  private AuthTokenPair createTokenPair(Long userId) {
    String accessToken = createAccessToken(userId);
    String refreshToken = refreshTokenProvider.generateRefreshToken();
    String tokenHash = refreshTokenProvider.hashRefreshToken(refreshToken);
    Instant expiresAt = Instant.now().plus(securityProperties.refreshTokenExpiration());

    refreshTokenRedisService.createRefreshTokenSession(userId, tokenHash, expiresAt);

    return AuthTokenPair.of(accessToken, refreshToken);
  }

  /**
   * 사용자 ID를 subject로 하는 Access Token을 생성한다.
   *
   * @param userId Access Token에 담을 사용자 ID
   * @return 서명된 Access Token
   */
  private String createAccessToken(Long userId) {
    try {
      return accessTokenProvider.generateAccessToken(userId);
    } catch (JOSEException e) {
      throw new AuthException(AuthErrorCode.TOKEN_GENERATION_FAILED, e);
    }
  }
}
