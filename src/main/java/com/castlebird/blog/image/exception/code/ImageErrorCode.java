package com.castlebird.blog.image.exception.code;

import com.castlebird.blog.global.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {

  IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다."),
  UNSUPPORTED_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다."),
  INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "올바르지 않은 이미지 파일입니다."),
  IMAGE_DIMENSION_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 해상도가 너무 큽니다."),
  IMAGE_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 처리에 실패했습니다.");

  private final HttpStatus status;
  private final String message;
}
