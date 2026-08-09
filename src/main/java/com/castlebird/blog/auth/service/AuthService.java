package com.castlebird.blog.auth.service;

import com.castlebird.blog.auth.dto.AuthTokenPair;
import com.castlebird.blog.auth.dto.request.LoginRequest;

public interface AuthService {

  /**
   * 이메일과 비밀번호를 검증하고 새 Refresh Token 세션을 생성한다.
   *
   * @param loginRequest 로그인 요청
   * @return 새 Access Token과 Refresh Token 쌍
   */
  AuthTokenPair login(LoginRequest loginRequest);

  /**
   * 기존 Refresh Token을 Rotate하고 새 토큰 쌍을 생성한다.
   *
   * @param refreshToken 기존 Refresh Token 원문
   * @return 새 Access Token과 Refresh Token 쌍
   */
  AuthTokenPair refresh(String refreshToken);

  /**
   * Refresh Token에 연결된 세션을 폐기한다.
   *
   * @param refreshToken 폐기할 Refresh Token 원문
   */
  void logout(String refreshToken);
}
