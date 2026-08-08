package com.castlebird.blog.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.castlebird.blog.post.entity.Tag;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

}
