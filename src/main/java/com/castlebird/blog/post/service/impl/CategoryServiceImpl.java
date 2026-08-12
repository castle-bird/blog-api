package com.castlebird.blog.post.service.impl;

import com.castlebird.blog.post.dto.request.CreateCategoryRequest;
import com.castlebird.blog.post.dto.request.UpdateCategoryRequest;
import com.castlebird.blog.post.dto.response.CategoryResponse;
import com.castlebird.blog.post.service.CategoryService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Override
  public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {
    throw new UnsupportedOperationException("카테고리 생성 로직은 아직 구현되지 않았습니다.");
  }

  @Override
  public CategoryResponse getCategory(Long categoryId) {
    throw new UnsupportedOperationException("카테고리 단건 조회 로직은 아직 구현되지 않았습니다.");
  }

  @Override
  public List<CategoryResponse> getCategories() {
    throw new UnsupportedOperationException("카테고리 다건 조회 로직은 아직 구현되지 않았습니다.");
  }

  @Override
  public CategoryResponse updateCategory(
      Long categoryId,
      UpdateCategoryRequest updateCategoryRequest
  ) {
    throw new UnsupportedOperationException("카테고리 수정 로직은 아직 구현되지 않았습니다.");
  }

  @Override
  public void deleteCategory(Long categoryId) {
    throw new UnsupportedOperationException("카테고리 삭제 로직은 아직 구현되지 않았습니다.");
  }
}
