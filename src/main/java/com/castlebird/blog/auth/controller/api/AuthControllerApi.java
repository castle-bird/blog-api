package com.castlebird.blog.auth.controller.api;

import com.castlebird.blog.auth.dto.request.LoginRequest;
import com.castlebird.blog.auth.dto.response.LoginResponse;
import com.castlebird.blog.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증", description = "로그인 및 토큰 관리 API")
public interface AuthControllerApi {

  @Operation(summary = "로그인")
  ResponseEntity<SuccessResponse<LoginResponse>> login(LoginRequest loginRequest);

  @Operation(summary = "Access Token 재발급", description = "Refresh Token Cookie를 Rotate합니다.")
  ResponseEntity<SuccessResponse<LoginResponse>> refresh(String refreshToken);

  @Operation(summary = "로그아웃")
  ResponseEntity<SuccessResponse<Void>> logout(String refreshToken);
}
