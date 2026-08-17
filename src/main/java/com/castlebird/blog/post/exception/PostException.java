package com.castlebird.blog.post.exception;

import com.castlebird.blog.global.exception.GlobalException;
import com.castlebird.blog.post.exception.code.PostErrorCode;

public class PostException extends GlobalException {

  public PostException(PostErrorCode errorCode) {
    super(errorCode);
  }

  public PostException(PostErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
