package com.castlebird.blog.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.castlebird.blog.post.entity.Post;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

}
