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

  @Query("""
      SELECT p FROM Post p
      WHERE
        (:cursorId IS NULL OR p.id < :cursorId)
        AND (:tag IS NULL OR EXISTS (
          SELECT 1 FROM PostTag pt
          WHERE pt.post = p
            AND pt.tag.name = :tag
        ))
        AND (:keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%')
          OR p.content LIKE CONCAT('%', :keyword, '%'))
      ORDER BY p.id DESC
      """)
  Slice<Post> search(
      @Param("cursorId") Long cursorId,
      @Param("tag") String tag,
      @Param("keyword") String keyword,
      Pageable pageable
  );

  @Modifying
  @Query(""" 
      UPDATE Post p
      SET p.viewCount = p.viewCount + 1
      WHERE p.id = :postId
      """)
  void increaseViewCount(@Param("postId") Long postId);
}
