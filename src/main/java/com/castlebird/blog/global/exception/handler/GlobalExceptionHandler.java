package com.castlebird.blog.global.exception.handler;

import com.castlebird.blog.global.exception.GlobalException;
import com.castlebird.blog.global.exception.code.CommonErrorCode;
import com.castlebird.blog.global.exception.code.ErrorCode;
import com.castlebird.blog.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // ErrorCode를 가진 프로젝트 공통 예외
  @ExceptionHandler(GlobalException.class)
  public ResponseEntity<ErrorResponse> handleApplicationError(GlobalException e) {
    ErrorCode errorCode = e.getErrorCode();

    if (errorCode.getStatus().is5xxServerError()) {
      log.error("서비스 처리 중 서버 오류가 발생했습니다.", e);
    }

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode));
  }

  // @Valid 요청 DTO의 필드 검증 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleBodyValidation(
      MethodArgumentNotValidException e
  ) {
    ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST;
    String message = e.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(FieldError::getDefaultMessage)
        .orElse(errorCode.getMessage());

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(new ErrorResponse(errorCode.getCode(), message));
  }

  // Controller 메서드의 개별 파라미터 검증 실패
  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponse> handleParameterValidation(
      HandlerMethodValidationException e
  ) {
    ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST;

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode));
  }

  // JSON 형식이 잘못됐거나 요청 값을 메서드 파라미터 타입으로 변환하지 못한 경우
  @ExceptionHandler({
      HttpMessageNotReadableException.class,
      MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception e) {
    ErrorCode errorCode = CommonErrorCode.INVALID_REQUEST;

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode));
  }

  // 별도로 처리하지 못한 서버 내부 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception e) {
    log.error("처리되지 않은 예외가 발생했습니다.", e);
    ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.of(errorCode));
  }
}
