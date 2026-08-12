package com.castlebird.blog.post.controller.api;

import com.castlebird.blog.global.response.SuccessResponse;
import com.castlebird.blog.post.dto.request.CreateCategoryRequest;
import com.castlebird.blog.post.dto.request.UpdateCategoryRequest;
import com.castlebird.blog.post.dto.response.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "카테고리", description = "카테고리 관련 API")
public interface CategoryControllerApi {

  @Operation(summary = "카테고리 생성", security = @SecurityRequirement(name = "bearerAuth"))
  ResponseEntity<SuccessResponse<CategoryResponse>> createCategory(
      @Valid CreateCategoryRequest createCategoryRequest
  );

  @Operation(summary = "카테고리 단건 조회")
  ResponseEntity<SuccessResponse<CategoryResponse>> getCategory(Long categoryId);

  @Operation(summary = "카테고리 다건 조회")
  ResponseEntity<SuccessResponse<List<CategoryResponse>>> getCategories();

  @Operation(summary = "카테고리 수정", security = @SecurityRequirement(name = "bearerAuth"))
  ResponseEntity<SuccessResponse<CategoryResponse>> updateCategory(
      Long categoryId,
      @Valid UpdateCategoryRequest updateCategoryRequest
  );

  @Operation(summary = "카테고리 삭제", security = @SecurityRequirement(name = "bearerAuth"))
  ResponseEntity<SuccessResponse<Void>> deleteCategory(Long categoryId);
}
