package com.castlebird.blog.post.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

  @EmbeddedId
  private PostTagId id;

  @MapsId("postId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  @MapsId("tagId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tag_id", nullable = false)
  private Tag tag;

  private PostTag(Post post, Tag tag) {
    this.id = new PostTagId(post.getId(), tag.getId());
    this.post = post;
    this.tag = tag;
  }

  static PostTag create(Post post, Tag tag) {
    return new PostTag(post, tag);
  }
}
