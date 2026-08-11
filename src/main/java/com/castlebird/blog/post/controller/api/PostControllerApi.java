package com.castlebird.blog.post.controller.api;

import com.castlebird.blog.global.response.SuccessResponse;
import com.castlebird.blog.post.dto.request.CreatePostRequest;
import com.castlebird.blog.post.dto.request.UpdatePostRequest;
import com.castlebird.blog.post.dto.response.PostListResponse;
import com.castlebird.blog.post.dto.response.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;

@Tag(name = "게시글", description = "게시글 관련 API")
public interface PostControllerApi {

  @Operation(summary = "게시글 생성", security = @SecurityRequirement(name = "bearerAuth"))
  ResponseEntity<SuccessResponse<PostResponse>> createPost(CreatePostRequest createPostRequest);

  @Operation(summary = "게시글 단건 조회")
  ResponseEntity<SuccessResponse<PostResponse>> getPost(Long postId);

  @Operation(summary = "게시글 다건 조회")
  ResponseEntity<SuccessResponse<PostListResponse>> getPosts(Long cursorId,int size);

  @Operation(summary = "게시글 수정", security = @SecurityRequirement(name = "bearerAuth"))
  ResponseEntity<SuccessResponse<PostResponse>> updatePost(
      Long postId,
      UpdatePostRequest updatePostRequest
  );

  @Operation(summary = "게시글 삭제", security = @SecurityRequirement(name = "bearerAuth"))
  ResponseEntity<SuccessResponse<Void>> deletePost(Long postId);
}
