package com.castlebird.blog.image.storage.impl;

import com.castlebird.blog.global.config.properties.UploadProperties;
import com.castlebird.blog.image.exception.ImageException;
import com.castlebird.blog.image.exception.code.ImageErrorCode;
import com.castlebird.blog.image.storage.ImageStorage;
import com.castlebird.blog.image.dto.result.StorageResult;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지를 로컬 디스크에 저장한다. 원본 형식과 무관하게 JPEG로 재인코딩해 용량을 줄인다.
 */
@Component
@RequiredArgsConstructor
public class LocalImageStorageImpl implements ImageStorage {

  private static final Set<String> SUPPORTED_CONTENT_TYPES =
      Set.of("image/jpeg", "image/png", "image/webp");
  private static final String OUTPUT_CONTENT_TYPE = "image/jpeg";
  private static final String OUTPUT_FORMAT = "jpg";
  private static final int MAX_WIDTH = 1600;
  private static final int MAX_HEIGHT = 1600;
  private static final long MAX_PIXELS = 40_000_000L; // 압축 폭탄 방어용 픽셀 수 상한
  private static final double OUTPUT_QUALITY = 0.85;

  private final UploadProperties uploadProperties;

  @Override
  public StorageResult save(MultipartFile file) {
    validateContentType(file.getContentType());

    String relativePath = generateRelativePath();
    Path targetPath = resolvePath(relativePath);

    try {
      Files.createDirectories(targetPath.getParent());

      BufferedImage original = ImageIO.read(file.getInputStream());

      if (original == null) {
        throw new ImageException(ImageErrorCode.INVALID_IMAGE_FILE);
      }
      validatePixelCount(original);

      Builder<BufferedImage> builder = Thumbnails.of(original);

      if (original.getWidth() > MAX_WIDTH || original.getHeight() > MAX_HEIGHT) {
        builder.size(MAX_WIDTH, MAX_HEIGHT);
      } else {
        builder.scale(1.0);
      }

      builder.outputFormat(OUTPUT_FORMAT)
          .outputQuality(OUTPUT_QUALITY)
          .toFile(targetPath.toFile());

      return new StorageResult(relativePath, OUTPUT_CONTENT_TYPE, Files.size(targetPath));
    } catch (IOException e) {
      throw new ImageException(ImageErrorCode.IMAGE_PROCESSING_FAILED, e);
    }
  }

  @Override
  public void delete(String path) {
    try {
      Files.deleteIfExists(resolvePath(path));
    } catch (IOException e) {
      throw new ImageException(ImageErrorCode.IMAGE_PROCESSING_FAILED, e);
    }
  }

  private void validateContentType(String contentType) {
    if (!SUPPORTED_CONTENT_TYPES.contains(contentType)) {
      throw new ImageException(ImageErrorCode.UNSUPPORTED_IMAGE_TYPE);
    }
  }

  private void validatePixelCount(BufferedImage image) {
    long pixelCount = (long) image.getWidth() * image.getHeight();

    if (pixelCount > MAX_PIXELS) {
      throw new ImageException(ImageErrorCode.IMAGE_DIMENSION_TOO_LARGE);
    }
  }

  private String generateRelativePath() {
    LocalDate today = LocalDate.now();

    // 2026/08/{UUID}.jpg
    return "%d/%02d/%s.%s".formatted(
        today.getYear(),
        today.getMonthValue(),
        UUID.randomUUID(),
        OUTPUT_FORMAT
    );
  }

  private Path resolvePath(String relativePath) {
    return Path.of(uploadProperties.basePath()).resolve(relativePath);
  }
}
