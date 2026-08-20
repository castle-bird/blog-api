package com.castlebird.blog.image.service.impl;

import com.castlebird.blog.image.dto.response.ImageResponse;
import com.castlebird.blog.image.repository.ImageRepository;
import com.castlebird.blog.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final ImageRepository imageRepository;


  @Override
  public ImageResponse uploadImage(MultipartFile file) {
    return null;
  }

  @Override
  public void deleteImage(Long imageId) {

  }
}
