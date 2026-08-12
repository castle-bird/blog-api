package com.castlebird.blog.post.service;

import com.castlebird.blog.post.dto.request.CreatePostRequest;
import com.castlebird.blog.post.dto.request.UpdatePostRequest;
import com.castlebird.blog.post.dto.response.PostListResponse;
import com.castlebird.blog.post.dto.response.PostResponse;

public interface PostService {

  PostResponse createPost(CreatePostRequest createPostRequest);

  PostResponse getPost(Long postId);

  PostListResponse getPosts(Long cursorId, int size);

  PostResponse updatePost(Long postId, UpdatePostRequest updatePostRequest);

  void deletePost(Long postId);
}
