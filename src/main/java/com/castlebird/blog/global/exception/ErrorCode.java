package com.castlebird.blog.global.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
  // name()은 enum의 기본 메서드지만,
  // interface로 enum을 구현하여 컴파일 시점에 name()이 없어서 오류가 난다.
  // 위를 해결하기 위해서 interface에 name()을 추가했다.
  String name();

  HttpStatus getStatus();

  String getMessage();

  // 각 enum에서 name()을 Override하는 중복을 제거
  default String getCode() {
    return name();
  }
}
