package com.castlebird.blog.image.storage;

import com.castlebird.blog.image.dto.result.StorageResult;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {

  StorageResult save(MultipartFile file);

  void delete(String path);
}
