package com.castlebird.blog.post.repository;
import com.castlebird.blog.post.entity.PostTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import com.castlebird.blog.post.entity.PostTag;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, PostTagId> {

}
