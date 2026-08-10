package com.castlebird.blog.post.service.impl;

import com.castlebird.blog.post.repository.CategoryRepository;
import com.castlebird.blog.post.repository.PostRepository;
import com.castlebird.blog.post.repository.PostTagRepository;
import com.castlebird.blog.post.repository.TagRepository;
import com.castlebird.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
}
