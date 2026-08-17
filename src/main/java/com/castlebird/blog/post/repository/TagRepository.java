package com.castlebird.blog.post.repository;

import com.castlebird.blog.post.entity.Tag;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

  List<Tag> findAllByNameIn(Collection<String> names);

  @Modifying(flushAutomatically = true)
  @Query("""
      DELETE FROM Tag t
      WHERE t.id IN :tagIds
        AND NOT EXISTS (
          SELECT 1
          FROM PostTag pt
          WHERE pt.tag = t
        )
      """)
  int deleteUnusedByIdIn(@Param("tagIds") Collection<Long> tagIds);
}
