package com.castlebird.blog.post.service.impl;

import com.castlebird.blog.post.dto.request.CreateCategoryRequest;
import com.castlebird.blog.post.dto.request.UpdateCategoryRequest;
import com.castlebird.blog.post.dto.response.CategoryResponse;
import com.castlebird.blog.post.entity.Category;
import com.castlebird.blog.post.exception.PostException;
import com.castlebird.blog.post.exception.code.PostErrorCode;
import com.castlebird.blog.post.mapper.CategoryMapper;
import com.castlebird.blog.post.repository.CategoryRepository;
import com.castlebird.blog.post.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public CategoryResponse createCategory(CreateCategoryRequest createCategoryRequest) {
    try {
      Category category = categoryRepository.saveAndFlush(
          Category.create(createCategoryRequest.name())
      );

      return categoryMapper.toCategoryResponse(category);
    } catch (DataIntegrityViolationException e) {
      throw new PostException(PostErrorCode.CATEGORY_ALREADY_EXISTS, e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryResponse getCategory(Long categoryId) {
    return categoryMapper.toCategoryResponse(findCategory(categoryId));
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> getCategories() {
    return categoryMapper.toCategoryResponses(
        categoryRepository.findAll(Sort.by("id"))
    );
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public CategoryResponse updateCategory(
      Long categoryId,
      UpdateCategoryRequest updateCategoryRequest
  ) {
    Category category = findCategory(categoryId);
    category.rename(updateCategoryRequest.name());

    try {
      categoryRepository.flush();
    } catch (DataIntegrityViolationException e) {
      throw new PostException(PostErrorCode.CATEGORY_ALREADY_EXISTS, e);
    }

    return categoryMapper.toCategoryResponse(category);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deleteCategory(Long categoryId) {
    Category category = findCategory(categoryId);
    categoryRepository.delete(category);

    try {
      categoryRepository.flush();
    } catch (DataIntegrityViolationException e) {
      throw new PostException(PostErrorCode.CATEGORY_IN_USE, e);
    }
  }

  private Category findCategory(Long categoryId) {
    return categoryRepository.findById(categoryId)
        .orElseThrow(() -> new PostException(PostErrorCode.CATEGORY_NOT_FOUND));
  }
}
