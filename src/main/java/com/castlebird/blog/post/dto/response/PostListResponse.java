package com.castlebird.blog.post.dto.response;

import java.util.List;

public record PostListResponse(
    List<PostResponse> posts,
    Long nextCursorId,
    boolean hasNext
) {

  public static PostListResponse of(
      List<PostResponse> posts,
      Long nextCursorId,
      boolean hasNext
  ) {
    return new PostListResponse(posts, nextCursorId, hasNext);
  }
}
