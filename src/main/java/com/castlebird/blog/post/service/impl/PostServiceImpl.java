package com.castlebird.blog.post.service.impl;

import com.castlebird.blog.global.security.principal.CustomUserDetails;
import com.castlebird.blog.post.dto.request.CreatePostRequest;
import com.castlebird.blog.post.dto.request.UpdatePostRequest;
import com.castlebird.blog.post.dto.response.PostListResponse;
import com.castlebird.blog.post.dto.response.PostResponse;
import com.castlebird.blog.post.entity.Category;
import com.castlebird.blog.post.entity.Post;
import com.castlebird.blog.post.entity.PostTag;
import com.castlebird.blog.post.entity.Tag;
import com.castlebird.blog.post.exception.PostException;
import com.castlebird.blog.post.exception.code.PostErrorCode;
import com.castlebird.blog.post.mapper.PostMapper;
import com.castlebird.blog.post.repository.CategoryRepository;
import com.castlebird.blog.post.repository.PostRepository;
import com.castlebird.blog.post.repository.PostTagRepository;
import com.castlebird.blog.post.repository.TagRepository;
import com.castlebird.blog.post.service.PostService;
import com.castlebird.blog.user.entity.User;
import com.castlebird.blog.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

  private static final String VIEW_COUNT_KEY_PREFIX = "view:post:";
  private static final Duration VIEW_COUNT_TTL = Duration.ofHours(24);

  private final PostRepository postRepository;
  private final CategoryRepository categoryRepository;
  private final TagRepository tagRepository;
  private final PostTagRepository postTagRepository;
  private final PostMapper postMapper;
  private final UserRepository userRepository;
  private final EntityManager entityManager;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public PostResponse createPost(CreatePostRequest createPostRequest) {
    CustomUserDetails principal = getPrincipal();

    // 인증 필터에서 존재가 확인된 사용자 ID로 작성자 연관관계용 JPA 참조를 얻는다.
    User author = userRepository.getReferenceById(principal.getUserId());
    Category category = categoryRepository.findById(createPostRequest.categoryId())
        .orElseThrow(() -> new PostException(PostErrorCode.CATEGORY_NOT_FOUND));
    List<Tag> tags = findOrCreateTags(createPostRequest.tags());

    Post post = postRepository.save(Post.create(
        createPostRequest.title(),
        createPostRequest.content(),
        author,
        category
    ));

    // 식별자가 생성된 Post와 Tag로 중간 Entity를 구성한다.
    List<PostTag> postTags = tags.stream()
        .map(post::addTag)
        .toList();

    postTagRepository.saveAll(postTags);

    return postMapper.toPostResponse(post);
  }

  @Override
  @Transactional
  public PostResponse getPost(Long postId, String clientIp) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

    if (!isAuthor(post) && isFirstViewFromIp(postId, clientIp)) {
      postRepository.increaseViewCount(postId);
      entityManager.refresh(post);
    }

    return postMapper.toPostResponse(post);
  }

  @Override
  @Transactional(readOnly = true)
  public PostListResponse getPosts(Long cursorId, int size) {
    Pageable pageable = PageRequest.of(0, size);

    Slice<Post> postSlice = cursorId == null
        ? postRepository.findAllByOrderByIdDesc(pageable)
        : postRepository.findByIdLessThanOrderByIdDesc(cursorId, pageable);

    List<PostResponse> posts = postSlice.getContent().stream()
        .map(postMapper::toPostResponse)
        .toList();

    Long nextCursorId = postSlice.hasNext()
        ? postSlice.getContent().getLast().getId()
        : null;

    return PostListResponse.of(
        posts,
        nextCursorId,
        postSlice.hasNext());
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public PostResponse updatePost(Long postId, UpdatePostRequest updatePostRequest) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    Category category = categoryRepository.findById(updatePostRequest.categoryId())
        .orElseThrow(() -> new PostException(PostErrorCode.CATEGORY_NOT_FOUND));

    // 게시글 내용부터 업데이트
    post.update(
        updatePostRequest.title(),
        updatePostRequest.content(),
        category
    );

    // 요청된 태그들의 Entity와 ID
    List<Tag> tags = findOrCreateTags(updatePostRequest.tags());
    Set<Long> requestedTagIds = tags.stream()
        .map(Tag::getId)
        .collect(Collectors.toSet());

    // 현재 게시글에 연결된 태그 ID
    Set<Long> existingTagIds = post.getPostTags().stream()
        .map(postTag -> postTag.getId().getTagId())
        .collect(Collectors.toSet());

    // 요청 태그에 없는 기존 "게시글 ↔ 태그" 관계를 제거한다.
    // → 요청 태그 기준으로 재정의 해야함
    List<PostTag> removedPostTags = post.getPostTags().stream()
        .filter(postTag -> !requestedTagIds.contains(postTag.getId().getTagId()))
        .toList();
    removePostTagRelations(post, removedPostTags);

    List<PostTag> newPostTags = tags.stream()
        .filter(tag -> !existingTagIds.contains(tag.getId()))
        .map(post::addTag)
        .toList();
    postTagRepository.saveAll(newPostTags);

    return postMapper.toPostResponse(post);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deletePost(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

    removePostTagRelations(post, List.copyOf(post.getPostTags()));
    post.delete();
  }

  /**
   * 게시글에서 지정한 태그 관계를 제거하고, 다른 게시글도 사용하지 않는 Tag를 삭제한다.
   */
  private void removePostTagRelations(Post post, List<PostTag> postTags) {
    if (postTags.isEmpty()) {
      return;
    }

    List<Long> tagIds = postTags.stream()
        .map(postTag -> postTag.getId().getTagId())
        .toList();

    post.removeTags(postTags);
    postTagRepository.deleteAll(postTags);
    tagRepository.deleteUnusedByIdIn(tagIds);
  }

  /**
   * 요청 태그 이름의 중복을 제거하고, 기존 태그는 재사용하며 없는 태그만 생성한다.
   * 반환 순서는 각 태그가 요청에 처음 등장한 순서를 유지한다.
   *
   * @param requestedTags 게시글 생성 요청의 태그 이름 목록
   * @return 게시글에 연결할 Tag Entity 목록
   */
  private List<Tag> findOrCreateTags(List<String> requestedTags) {
    List<String> uniqueTagNames = requestedTags.stream()
        .distinct()
        .toList();

    if (uniqueTagNames.isEmpty()) {
      return List.of();
    }

    // 기존 태그를 이름으로 인덱싱해 신규 태그만 구분한다.
    Map<String, Tag> tagsByName = new HashMap<>();
    tagRepository.findAllByNameIn(uniqueTagNames)
        .forEach(tag -> tagsByName.put(tag.getName(), tag));

    List<Tag> newTags = uniqueTagNames.stream()
        .filter(name -> !tagsByName.containsKey(name))
        .map(Tag::create)
        .toList();

    // 신규 태그도 같은 Map에 합쳐 요청 이름으로 전체 태그를 복원한다.
    tagRepository.saveAll(newTags)
        .forEach(tag -> tagsByName.put(tag.getName(), tag));

    // 중복을 제거한 요청 순서대로 연관관계 생성용 목록을 반환한다.
    return uniqueTagNames.stream()
        .map(tagsByName::get)
        .toList();
  }

  /**
   * 현재 SecurityContext에 저장된 인증 주체를 반환한다.
   *
   * @return JwtAuthenticationFilter가 등록한 CustomUserDetails
   */
  private CustomUserDetails getPrincipal() {
    return (CustomUserDetails) SecurityContextHolder.getContext()
        .getAuthentication()
        .getPrincipal();
  }

  /**
   * 요청자가 게시글 작성자 본인인지 판별한다. 비로그인 요청은 항상 false.
   */
  private boolean isAuthor(Post post) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (!(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
      return false;
    }

    return principal.getUserId().equals(post.getAuthor().getId());
  }

  /**
   * 같은 IP가 24시간 내 이미 조회한 게시글이면 false.
   */
  private boolean isFirstViewFromIp(Long postId, String clientIp) {
    String key = VIEW_COUNT_KEY_PREFIX + postId + ":" + clientIp;
    Boolean isFirstView = redisTemplate.opsForValue()
        .setIfAbsent(key, "1", VIEW_COUNT_TTL);

    return Boolean.TRUE.equals(isFirstView);
  }
}
