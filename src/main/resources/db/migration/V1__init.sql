---------------------------------------
-- V1__init.sql
-- 초기 데이터베이스 스키마
---------------------------------------

---------------------------------------
-- <b>사용자</b>
-- 개인 블로그이지만 권한 기반 인가 학습을 위해 역할을 관리한다.
-- 유저는 사용자 1명일 것이라 INDEX 추가는 하지 않는다.
---------------------------------------
CREATE TYPE user_role AS ENUM ('ADMIN', 'USER');

CREATE TABLE users
(
  id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  username   VARCHAR(50)              NOT NULL,
  password   VARCHAR(255)             NOT NULL,
  email      VARCHAR(100)             NOT NULL UNIQUE, -- 이메일을 로그인 ID로 사용한다.
  nickname   VARCHAR(50)              NOT NULL,
  role       user_role                NOT NULL DEFAULT 'USER',
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE,
  deleted_at TIMESTAMP WITH TIME ZONE
);

COMMENT ON TABLE users IS '사용자 정보';
COMMENT ON COLUMN users.id IS '사용자 ID';
COMMENT ON COLUMN users.username IS '사용자 이름';
COMMENT ON COLUMN users.password IS '사용자 비밀번호';
COMMENT ON COLUMN users.email IS '사용자 이메일';
COMMENT ON COLUMN users.nickname IS '사용자 닉네임';
COMMENT ON COLUMN users.role IS '사용자 권한';
COMMENT ON COLUMN users.created_at IS '생성일';
COMMENT ON COLUMN users.updated_at IS '수정일';
COMMENT ON COLUMN users.deleted_at IS '삭제일';

---------------------------------------
-- <b>태그</b>
---------------------------------------
CREATE TABLE tags
(
  id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE
);

COMMENT ON TABLE tags IS '태그';
COMMENT ON COLUMN tags.id IS '태그 ID';
COMMENT ON COLUMN tags.name IS '태그 이름';

---------------------------------------
-- <b>게시글</b>
---------------------------------------
CREATE TABLE posts
(
  id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  title      VARCHAR(100)             NOT NULL,
  content    TEXT                     NOT NULL,
  view_count BIGINT                   NOT NULL DEFAULT 0,
  author_id  BIGINT                   NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE,
  deleted_at TIMESTAMP WITH TIME ZONE,
  CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users (id)
);

COMMENT ON TABLE posts IS '게시글';
COMMENT ON COLUMN posts.id IS '게시글 ID';
COMMENT ON COLUMN posts.title IS '게시글 제목';
COMMENT ON COLUMN posts.content IS '게시글 내용';
COMMENT ON COLUMN posts.view_count IS '조회수';
COMMENT ON COLUMN posts.author_id IS '작성자 ID';
COMMENT ON COLUMN posts.created_at IS '생성일';
COMMENT ON COLUMN posts.updated_at IS '수정일';
COMMENT ON COLUMN posts.deleted_at IS '삭제일';

CREATE INDEX idx_posts_created_at ON posts (created_at);

COMMENT ON INDEX idx_posts_created_at IS '게시글 생성일 인덱스';

---------------------------------------
-- <b>게시글 & 태그</b>
-- 게시글과 태그의 N:M 관계를 해소하기 위한 중간 테이블
---------------------------------------
CREATE TABLE post_tags
(
  post_id BIGINT NOT NULL,
  tag_id  BIGINT NOT NULL,
  PRIMARY KEY (post_id, tag_id),
  CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts (id),
  CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);

COMMENT ON TABLE post_tags IS '게시글 태그 중간 테이블';
COMMENT ON COLUMN post_tags.post_id IS '게시글 ID';
COMMENT ON COLUMN post_tags.tag_id IS '태그 ID';

CREATE INDEX idx_post_tags_tag_id ON post_tags (tag_id);

COMMENT ON INDEX idx_post_tags_tag_id IS '태그별 게시글 조회 인덱스';
