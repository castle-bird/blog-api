package com.castlebird.blog.post.mapper;

import com.castlebird.blog.post.dto.response.PostResponse;
import com.castlebird.blog.post.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {

  PostResponse toPostResponse(Post post);
}
