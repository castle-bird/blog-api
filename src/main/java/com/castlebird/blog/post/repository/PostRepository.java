package com.castlebird.blog.post.repository;

import com.castlebird.blog.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  Slice<Post> findAllByOrderByIdDesc(Pageable pageable);

  Slice<Post> findByIdLessThanOrderByIdDesc(Long postId, Pageable pageable);
}
