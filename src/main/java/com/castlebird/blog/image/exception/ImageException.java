package com.castlebird.blog.image.exception;

import com.castlebird.blog.global.exception.GlobalException;
import com.castlebird.blog.image.exception.code.ImageErrorCode;

public class ImageException extends GlobalException {

  public ImageException(ImageErrorCode errorCode) {
    super(errorCode);
  }

  public ImageException(ImageErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
  }
}
