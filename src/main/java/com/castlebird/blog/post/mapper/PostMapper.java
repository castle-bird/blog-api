package com.castlebird.blog.post.mapper;

import com.castlebird.blog.post.dto.response.PostResponse;
import com.castlebird.blog.post.entity.Post;
import com.castlebird.blog.post.entity.PostTag;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

  @Mapping(target = "authorId", source = "author.id")
  @Mapping(target = "authorNickname", source = "author.nickname")
  @Mapping(target = "categoryId", source = "category.id")
  @Mapping(target = "categoryName", source = "category.name")
  @Mapping(target = "tags", source = "postTags")
  PostResponse toPostResponse(Post post);

  default List<String> toTagNames(List<PostTag> postTags) {
    return postTags.stream()
        .map(postTag -> postTag.getTag().getName())
        .toList();
  }
}
