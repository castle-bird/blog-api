package com.castlebird.blog.global.security.token;

import com.castlebird.blog.global.config.properties.JwtProperties;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProvider {

  private final JwtProperties jwtProperties;
  private final JWSSigner signer;
  private final JWSVerifier verifier;
  private final JWSAlgorithm algorithm;

  public AccessTokenProvider(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
    this.algorithm = JWSAlgorithm.HS256;

    try {
      byte[] secret = Base64.getDecoder().decode(jwtProperties.secret());
      this.signer = new MACSigner(secret);
      this.verifier = new MACVerifier(secret);

    } catch (JOSEException | IllegalArgumentException e) {
      throw new IllegalStateException("JWT 설정이 올바르지 않습니다", e);
    }

  }

  /**
   * Access Token을 생성한다. 현재 프로젝트에서는 Access Token에 권한을 넣지 않는다.
   *
   * @param userId 사용자 ID
   * @return Access Token
   * @throws JOSEException 토큰 서명 과정에서 실패할 경우 발생한다.
   */
  public String generateAccessToken(Long userId) throws JOSEException {
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plus(jwtProperties.accessTokenExpiration());

    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .subject(userId.toString())
        .issuer(jwtProperties.issuer())
        .issueTime(Date.from(issuedAt))
        .expirationTime(Date.from(expiresAt))
        .build();

    SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
    signedJWT.sign(signer);

    return signedJWT.serialize();
  }

  /**
   * Access Token을 검증하고 사용자 ID를 추출한다.
   *
   * @param accessToken Access Token
   * @return 사용자 ID
   */
  public Long getUserId(String accessToken) {
    try {
      return Long.parseLong(parseAndVerify(accessToken).getSubject());
    } catch (JOSEException | NumberFormatException e) {
      throw new IllegalArgumentException("유효하지 않은 Access Token입니다.", e);
    }
  }

  /**
   * Access Token을 파싱하고 서명, 알고리즘, 필수 Claim을 검증한다.
   *
   * @param accessToken 검증할 토큰
   * @return 검증된 토큰 Claim
   * @throws JOSEException 토큰 파싱 또는 검증에 실패한 경우
   */
  private JWTClaimsSet parseAndVerify(String accessToken) throws JOSEException {
    try {
      // header.payload.signature → JWT 파싱
      SignedJWT signedJwt = SignedJWT.parse(accessToken);

      // 1. header 검증(알고리즘)
      JWSAlgorithm jwsAlgorithm = signedJwt.getHeader().getAlgorithm();

      if (!algorithm.equals(jwsAlgorithm)) {
        throw new JOSEException("유효하지 않은 알고리즘의 JWT 입니다.");
      }

      // 2. signature 검증
      boolean verified = signedJwt.verify(verifier);

      if (!verified) {
        throw new JOSEException("유효하지 않은 서명의 JWT 입니다.");
      }

      // 3. payload 검증
      JWTClaimsSet claims = signedJwt.getJWTClaimsSet();
      Instant now = Instant.now();

      if (!jwtProperties.issuer().equals(claims.getIssuer())
          || claims.getSubject() == null
          || claims.getSubject().isBlank()
          || claims.getExpirationTime() == null
          || !claims.getExpirationTime().toInstant().isAfter(now)
          || claims.getIssueTime() == null
          || claims.getIssueTime().toInstant().isAfter(now)
      ) {
        throw new JOSEException("유효하지 않은 JWT claim입니다.");
      }

      return claims;
    } catch (ParseException e) {
      // ParseException: JWT 파싱 실패시 발생
      throw new JOSEException("JWT 형식이 올바르지 않습니다.", e);
    }
  }
}
