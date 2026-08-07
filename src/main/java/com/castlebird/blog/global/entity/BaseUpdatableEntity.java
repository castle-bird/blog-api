package com.castlebird.blog.global.entity;

import java.time.Instant;

import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant updatedAt;

  @Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant deletedAt;

  public void delete() {
    this.deletedAt = Instant.now();
  }
}
