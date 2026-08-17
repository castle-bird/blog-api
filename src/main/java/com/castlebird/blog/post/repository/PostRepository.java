package com.castlebird.blog.post.repository;

import com.castlebird.blog.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  Slice<Post> findAllByOrderByIdDesc(Pageable pageable);

  Slice<Post> findByIdLessThanOrderByIdDesc(Long postId, Pageable pageable);

  @Modifying
  @Query(""" 
      UPDATE Post p
      SET p.viewCount = p.viewCount + 1
      WHERE p.id = :postId
      """)
  void increaseViewCount(@Param("postId") Long postId);
}
