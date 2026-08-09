package com.castlebird.blog.auth.exception.code;

import com.castlebird.blog.global.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
  INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
  TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "토큰 생성에 실패했습니다.");

  private final HttpStatus status;
  private final String message;
}
