package com.castlebird.blog.post.controller;

import com.castlebird.blog.global.response.SuccessResponse;
import com.castlebird.blog.post.controller.api.PostControllerApi;
import com.castlebird.blog.post.dto.request.CreatePostRequest;
import com.castlebird.blog.post.dto.request.UpdatePostRequest;
import com.castlebird.blog.post.dto.response.PostListResponse;
import com.castlebird.blog.post.dto.response.PostResponse;
import com.castlebird.blog.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController implements PostControllerApi {

  private final PostService postService;

  @Override
  @PostMapping
  public ResponseEntity<SuccessResponse<PostResponse>> createPost(
      @RequestBody CreatePostRequest createPostRequest
  ) {
    PostResponse response =
        postService.createPost(createPostRequest);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(SuccessResponse.of(response));
  }

  @Override
  @GetMapping("/{postId}")
  public ResponseEntity<SuccessResponse<PostResponse>> getPost(
      @PathVariable Long postId,
      HttpServletRequest request
  ) {
    PostResponse response =
        postService.getPost(postId, request.getRemoteAddr());

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(response));
  }

  @Override
  @GetMapping
  public ResponseEntity<SuccessResponse<PostListResponse>> getPosts(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size
  ) {
    PostListResponse response =
        postService.getPosts(cursorId, size);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(response));
  }

  @Override
  @PutMapping("/{postId}")
  public ResponseEntity<SuccessResponse<PostResponse>> updatePost(
      @PathVariable Long postId,
      @RequestBody UpdatePostRequest updatePostRequest
  ) {
    PostResponse response =
        postService.updatePost(postId, updatePostRequest);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(response));
  }

  @Override
  @DeleteMapping("/{postId}")
  public ResponseEntity<SuccessResponse<Void>> deletePost(
      @PathVariable Long postId
  ) {
    postService.deletePost(postId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of());
  }
}
