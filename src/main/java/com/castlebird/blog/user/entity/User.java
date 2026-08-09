package com.castlebird.blog.user.entity;

import com.castlebird.blog.global.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

@Entity
@SQLRestriction("deleted_at IS NULL")
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseUpdatableEntity {

  @Column(name = "username", nullable = false, length = 50)
  private String username;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  @Column(name = "email", nullable = false, length = 100, unique = true)
  private String email;

  @Column(name = "nickname", nullable = false, length = 50)
  private String nickname;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(name = "role", nullable = false, columnDefinition = "user_role")
  private UserRole role = UserRole.USER;
}
