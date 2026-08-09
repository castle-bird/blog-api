package com.castlebird.blog.global.security.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.castlebird.blog.global.config.properties.JwtProperties;
import com.nimbusds.jose.JOSEException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("토큰 Provider 단위 테스트")
class TokenProviderTest {

  private static final String SECRET = Base64.getEncoder()
      .encodeToString("12345678901234567890123456789012".getBytes(StandardCharsets.UTF_8)
      );

  @Test
  @DisplayName("유효한 Access Token에서 사용자 ID를 추출한다")
  void extractsUserIdFromValidAccessToken() throws JOSEException {
    AccessTokenProvider provider = accessTokenProvider();

    String accessToken = provider.generateAccessToken(42L);

    assertEquals(42L, provider.getUserId(accessToken));
  }

  @Test
  @DisplayName("서명이 변조된 Access Token을 거부한다")
  void rejectsAccessTokenWithTamperedSignature() throws JOSEException {
    AccessTokenProvider provider = accessTokenProvider();
    String accessToken = provider.generateAccessToken(42L);
    String[] parts = accessToken.split("\\.");

    // 시그니처 첫 글자를 변경 + 재조합
    char replacement = parts[2].charAt(0) == 'A' ? 'B' : 'A';
    String tamperedToken = parts[0] + "." + parts[1] + "." + replacement
        + parts[2].substring(1);

    assertThrows(IllegalArgumentException.class, () -> provider.getUserId(tamperedToken));
  }

  @Test
  @DisplayName("Refresh Token과 해시를 정해진 형식으로 생성한다")
  void generatesRefreshTokenAndHashInExpectedFormat() {
    RefreshTokenProvider provider = new RefreshTokenProvider();

    String refreshToken = provider.generateRefreshToken();
    String tokenHash = provider.hashRefreshToken(refreshToken);

    assertEquals(43, refreshToken.length());
    assertEquals(64, tokenHash.length());
    assertTrue(tokenHash.matches("[0-9a-f]{64}"));
  }

  private AccessTokenProvider accessTokenProvider() {
    JwtProperties properties = new JwtProperties(
        Duration.ofMinutes(15),
        SECRET,
        "test-issuer"
    );

    return new AccessTokenProvider(properties);
  }
}
