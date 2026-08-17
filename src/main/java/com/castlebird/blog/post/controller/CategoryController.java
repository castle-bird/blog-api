package com.castlebird.blog.post.controller;

import com.castlebird.blog.global.response.SuccessResponse;
import com.castlebird.blog.post.controller.api.CategoryControllerApi;
import com.castlebird.blog.post.dto.request.CreateCategoryRequest;
import com.castlebird.blog.post.dto.request.UpdateCategoryRequest;
import com.castlebird.blog.post.dto.response.CategoryResponse;
import com.castlebird.blog.post.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerApi {

  private final CategoryService categoryService;

  @Override
  @PostMapping
  public ResponseEntity<SuccessResponse<CategoryResponse>> createCategory(
      @RequestBody CreateCategoryRequest createCategoryRequest
  ) {
    CategoryResponse response = categoryService.createCategory(createCategoryRequest);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(SuccessResponse.of(response));
  }

  @Override
  @GetMapping("/{categoryId}")
  public ResponseEntity<SuccessResponse<CategoryResponse>> getCategory(
      @PathVariable Long categoryId
  ) {
    CategoryResponse response = categoryService.getCategory(categoryId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(response));
  }

  @Override
  @GetMapping
  public ResponseEntity<SuccessResponse<List<CategoryResponse>>> getCategories() {
    List<CategoryResponse> response = categoryService.getCategories();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(response));
  }

  @Override
  @PutMapping("/{categoryId}")
  public ResponseEntity<SuccessResponse<CategoryResponse>> updateCategory(
      @PathVariable Long categoryId,
      @RequestBody UpdateCategoryRequest updateCategoryRequest
  ) {
    CategoryResponse response = categoryService.updateCategory(
        categoryId,
        updateCategoryRequest
    );

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of(response));
  }

  @Override
  @DeleteMapping("/{categoryId}")
  public ResponseEntity<SuccessResponse<Void>> deleteCategory(
      @PathVariable Long categoryId
  ) {
    categoryService.deleteCategory(categoryId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(SuccessResponse.of());
  }
}
