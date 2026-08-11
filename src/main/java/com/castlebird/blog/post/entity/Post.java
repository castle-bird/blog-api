package com.castlebird.blog.post.entity;

import com.castlebird.blog.global.entity.BaseUpdatableEntity;
import com.castlebird.blog.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@SQLRestriction("deleted_at IS NULL")
@Getter
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseUpdatableEntity {

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "view_count", nullable = false)
  private Long viewCount = 0L;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @OneToMany(mappedBy = "post")
  private List<PostTag> postTags = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  private Post(
      String title,
      String content,
      User author,
      Category category
  ) {
    this.title = title;
    this.content = content;
    this.author = author;
    this.category = category;
  }

  public static Post create(
      String title,
      String content,
      User author,
      Category category
  ) {
    return new Post(title, content, author, category);
  }

  public PostTag addTag(Tag tag) {
    PostTag postTag = PostTag.create(this, tag);
    postTags.add(postTag);

    return postTag;
  }
}
