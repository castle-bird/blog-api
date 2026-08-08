package com.castlebird.blog.global.response;

public record SuccessResponse<T>(
  T data,
  String message
) {
  public static <T> SuccessResponse<T> of(T data) {
    return new SuccessResponse<>(data, "Success");
  }

  public static SuccessResponse<Void> of() {
    return new SuccessResponse<>(null, "Success");
  }
}
