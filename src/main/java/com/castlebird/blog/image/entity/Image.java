package com.castlebird.blog.image.entity;

import com.castlebird.blog.global.entity.BaseEntity;
import com.castlebird.blog.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseEntity {

  @Column(name = "path", nullable = false, unique = true, length = 255)
  private String path;

  @Column(name = "original_filename", nullable = false, length = 255)
  private String originalFilename;

  @Column(name = "content_type", nullable = false, length = 100)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private Long sizeBytes;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "uploader_id", nullable = false)
  private User uploader;

  private Image(
      String path,
      String originalFilename,
      String contentType,
      Long sizeBytes,
      User uploader
  ) {
    this.path = path;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    this.uploader = uploader;
  }

  public static Image create(
      String path,
      String originalFilename,
      String contentType,
      Long sizeBytes,
      User uploader
  ) {
    return new Image(path, originalFilename, contentType, sizeBytes, uploader);
  }
}
