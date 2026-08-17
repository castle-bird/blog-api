-- 게시글 작성하려고 보니 카테고리가 없어서 추가함

-- 게시글용 카테고리 테이블 추가
CREATE TABLE categories
(
  id   bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE
);

COMMENT ON TABLE categories IS '게시글 카테고리';
COMMENT ON COLUMN categories.id IS '카테고리 ID';
COMMENT ON COLUMN categories.name IS '카테고리 이름';

-- posts에 카테고리 의존성 추가 + 인덱싱 추가(카테고리별 게시글) + 코멘트 추가
ALTER TABLE posts
  ADD COLUMN category_id bigint NOT NULL,
    ADD CONSTRAINT fk_posts_category
      FOREIGN KEY (category_id) REFERENCES categories (id);

CREATE INDEX idx_posts_category_id ON posts (category_id);

COMMENT ON COLUMN posts.category_id IS '카테고리 ID';
