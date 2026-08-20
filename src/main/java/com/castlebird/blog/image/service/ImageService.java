package com.castlebird.blog.image.service;

import com.castlebird.blog.image.dto.response.ImageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  ImageResponse uploadImage(MultipartFile file);

  void deleteImage(Long imageId);
}
