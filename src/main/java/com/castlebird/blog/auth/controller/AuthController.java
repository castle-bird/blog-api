package com.castlebird.blog.auth.controller;

import com.castlebird.blog.auth.controller.api.AuthControllerApi;
import com.castlebird.blog.auth.dto.AuthTokenPair;
import com.castlebird.blog.auth.dto.request.LoginRequest;
import com.castlebird.blog.auth.dto.response.LoginResponse;
import com.castlebird.blog.auth.service.AuthService;
import com.castlebird.blog.global.config.properties.SecurityProperties;
import com.castlebird.blog.global.response.SuccessResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

  private static final String REFRESH_TOKEN_PATH = "/api/auth";

  private final AuthService authService;
  private final SecurityProperties securityProperties;

  @Override
  @PostMapping("/login")
  public ResponseEntity<SuccessResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest loginRequest
  ) {
    return tokenResponse(authService.login(loginRequest));
  }

  @Override
  @PostMapping("/refresh")
  public ResponseEntity<SuccessResponse<LoginResponse>> refresh(
      @CookieValue(name = "${app.security.cookie-name}", required = false) String refreshToken
  ) {
    return tokenResponse(authService.refresh(refreshToken));
  }

  @Override
  @PostMapping("/logout")
  public ResponseEntity<SuccessResponse<Void>> logout(
      @CookieValue(name = "${app.security.cookie-name}", required = false) String refreshToken
  ) {
    authService.logout(refreshToken);

    return ResponseEntity.ok()
        .header(
            HttpHeaders.SET_COOKIE,
            refreshTokenCookie("")
                .maxAge(Duration.ZERO)
                .build()
                .toString())
        .body(SuccessResponse.of());
  }

  /**
   * Access Token 응답과 Refresh Token Cookie를 함께 생성한다.
   *
   * @param tokens Access Token과 Refresh Token 쌍
   * @return Access Token 응답 및 Refresh Token Cookie가 설정된 HTTP 응답
   */
  private ResponseEntity<SuccessResponse<LoginResponse>> tokenResponse(AuthTokenPair tokens) {
    return ResponseEntity.ok()
        .header(
            HttpHeaders.SET_COOKIE,
            refreshTokenCookie(tokens.refreshToken())
                .maxAge(securityProperties.refreshTokenExpiration())
                .build()
                .toString())
        .body(SuccessResponse.of(
            LoginResponse.of(tokens.accessToken())
        ));
  }

  /**
   * Refresh Token을 담는 HttpOnly Cookie 빌더를 생성한다.
   *
   * @param refreshToken Cookie에 저장할 Refresh Token 원문
   * @return Refresh Token Cookie 빌더
   */
  private ResponseCookie.ResponseCookieBuilder refreshTokenCookie(String refreshToken) {
    return ResponseCookie.from(
            securityProperties.cookieName(),
            refreshToken)
        .httpOnly(true)
        .secure(securityProperties.cookieSecure())
        .sameSite(securityProperties.cookieSecure() ? "None" : "Lax")
        .path(REFRESH_TOKEN_PATH);
  }
}
