package com.castlebird.blog.post.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.castlebird.blog.post.entity.PostTag;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

}
