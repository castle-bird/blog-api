package com.castlebird.blog.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdatePostRequest(
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    String title,

    @NotBlank(message = "본문은 필수입니다.")
    String content,

    @NotNull(message = "카테고리는 필수입니다.")
    @Positive(message = "카테고리 ID는 양수여야 합니다.")
    Long categoryId,

    // 태그가 비어있을 수 있지만, 1개라도 있으면 검증을 한다.
    List<
        @NotBlank(message = "태그 이름은 비어 있을 수 없습니다.")
        @Size(max = 50, message = "태그 이름은 50자 이하여야 합니다.") String
        > tags
) {

  // Record의 compact constructor.
  // → Record 생성 중 필드가 최종 대입되기 전에 실행된다.
  // → tags가 null이면 빈 목록으로 변환한다.
  public UpdatePostRequest {
    tags = tags == null
        ? List.of()
        : tags;
  }
}
