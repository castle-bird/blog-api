package com.castlebird.blog.post.mapper;

import com.castlebird.blog.post.dto.response.CategoryResponse;
import com.castlebird.blog.post.entity.Category;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  CategoryResponse toCategoryResponse(Category category);

  List<CategoryResponse> toCategoryResponses(List<Category> categories);
}
