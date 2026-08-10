package com.castlebird.blog.post.dto.response;

import java.time.Instant;
import java.util.List;

public record PostResponse(
    Long id,
    String title,
    String content,
    Long viewCount,
    Long authorId,
    String authorNickname,
    Long categoryId,
    String categoryName,
    List<String> tags,
    Instant createdAt,
    Instant updatedAt
) {

}
