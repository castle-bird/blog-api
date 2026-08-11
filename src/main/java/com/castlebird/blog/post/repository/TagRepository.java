package com.castlebird.blog.post.repository;

import com.castlebird.blog.post.entity.Tag;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

  List<Tag> findAllByNameIn(Collection<String> names);
}
