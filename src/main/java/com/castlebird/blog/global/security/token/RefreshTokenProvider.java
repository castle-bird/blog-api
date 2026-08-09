package com.castlebird.blog.global.security.token;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProvider {

  private final SecureRandom secureRandom;

  public RefreshTokenProvider() {
    this.secureRandom = new SecureRandom();
  }

  /**
   * 암호학적으로 안전한 난수 기반 Refresh Token을 생성한다.
   *
   * @return Refresh Token = 32바이트 난수를 패딩 없이 Base64Url로 인코딩한 43자 ASCII 문자열
   */
  public String generateRefreshToken() {
    byte[] bytes = new byte[32];
    secureRandom.nextBytes(bytes);

    // 32바이트는 Base64URL 인코딩 시 무조건 44자(끝의 '=' padding 포함).
    // padding을 제거하면 43자.
    return Base64
        .getUrlEncoder()
        .withoutPadding()
        .encodeToString(bytes);
  }

  /**
   * Refresh Token의 SHA-256 해시를 생성하고 16진수 문자열로 반환한다.
   * Redis에 Refresh Token 원문을 저장하지 않기 위함이다.
   * Redis 노출 시 Refresh Token 탈취 위험을 줄인다.
   *
   * @param refreshToken Refresh Token 원문
   * @return SHA-256 해시 문자열
   */
  public String hashRefreshToken(String refreshToken) {
    try {
      // SHA-256 해시는 항상 32바이트다.
      byte[] hash = MessageDigest.getInstance("SHA-256")
          .digest(refreshToken.getBytes(StandardCharsets.UTF_8));

      // 16진수 한 글자는 4비트다. → 16 = 2의 4제곱
      // 따라서 32바이트 해시는 64글자 16진수 문자열이 된다.
      return HexFormat.of().formatHex(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256을 지원하지 않습니다.", e);
    }
  }
}
