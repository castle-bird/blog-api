package com.castlebird.blog.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTagId implements Serializable {

  @Column(name = "post_id")
  private Long postId;

  @Column(name = "tag_id")
  private Long tagId;
}
