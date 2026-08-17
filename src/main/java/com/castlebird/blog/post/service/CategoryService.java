package com.castlebird.blog.post.service;

import com.castlebird.blog.post.dto.request.CreateCategoryRequest;
import com.castlebird.blog.post.dto.request.UpdateCategoryRequest;
import com.castlebird.blog.post.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

  CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest);

  CategoryResponse getCategory(Long categoryId);

  List<CategoryResponse> getCategories();

  CategoryResponse updateCategory(
      Long categoryId,
      UpdateCategoryRequest updateCategoryRequest
  );

  void deleteCategory(Long categoryId);
}
