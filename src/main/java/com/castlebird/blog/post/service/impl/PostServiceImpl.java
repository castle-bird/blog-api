package com.castlebird.blog.post.service.impl;

import com.castlebird.blog.post.dto.request.CreatePostRequest;
import com.castlebird.blog.post.dto.request.UpdatePostRequest;
import com.castlebird.blog.post.dto.response.PostListResponse;
import com.castlebird.blog.post.dto.response.PostResponse;
import com.castlebird.blog.post.mapper.PostMapper;
import com.castlebird.blog.post.repository.CategoryRepository;
import com.castlebird.blog.post.repository.PostRepository;
import com.castlebird.blog.post.repository.PostTagRepository;
import com.castlebird.blog.post.repository.TagRepository;
import com.castlebird.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
  private final PostMapper postMapper;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public PostResponse createPost(CreatePostRequest createPostRequest) {
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public PostResponse getPost(Long postId) {
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public PostListResponse getPosts(Long cursorId, int size) {
    return null;
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public PostResponse updatePost(Long postId, UpdatePostRequest updatePostRequest) {
    return null;
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deletePost(Long postId) {

  }
}
