package com.castlebird.blog.post.dto.response;

import java.util.List;

public record PostListResponse(
    List<PostResponse> posts,
    Long nextCursorId,
    boolean hasNext
) {

}
