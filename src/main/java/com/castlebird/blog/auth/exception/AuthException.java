package com.castlebird.blog.auth.exception;

import com.castlebird.blog.auth.exception.code.AuthErrorCode;
import com.castlebird.blog.global.exception.GlobalException;

public class AuthException extends GlobalException {

  public AuthException(AuthErrorCode errorCode) {
    super(errorCode);
  }

  public AuthException(AuthErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
