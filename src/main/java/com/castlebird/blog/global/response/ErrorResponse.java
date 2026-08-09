package com.castlebird.blog.global.response;

import com.castlebird.blog.global.exception.code.ErrorCode;

public record ErrorResponse(
  String code,
  String message
) {

  public static ErrorResponse of(ErrorCode errorCode) {
    return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
  }
}
