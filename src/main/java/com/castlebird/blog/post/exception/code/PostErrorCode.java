package com.castlebird.blog.post.exception.code;

import com.castlebird.blog.global.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

  POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
  CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
  CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다."),
  CATEGORY_IN_USE(HttpStatus.CONFLICT, "게시글에서 사용 중인 카테고리는 삭제할 수 없습니다.");

  private final HttpStatus status;
  private final String message;
}
