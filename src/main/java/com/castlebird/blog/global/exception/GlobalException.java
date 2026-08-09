package com.castlebird.blog.global.exception;

import com.castlebird.blog.global.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

  private final ErrorCode errorCode;

  // 에러 메세지만 전달
  public GlobalException(ErrorCode errorCode) {
    super(errorCode.getMessage());

    this.errorCode = errorCode;
  }

  // 원인 예외를 포함하여 생성
  public GlobalException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);

    this.errorCode = errorCode;
  }

}
