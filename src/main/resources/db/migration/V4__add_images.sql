-- 이미지 업로드 기능 추가: 파일은 디스크에 저장하고, DB엔 메타데이터+경로만 저장

CREATE TABLE images
(
  id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  path              VARCHAR(255)             NOT NULL UNIQUE,
  original_filename VARCHAR(255)             NOT NULL,
  content_type      VARCHAR(100)             NOT NULL,
  size_bytes        BIGINT                   NOT NULL,
  uploader_id       BIGINT                   NOT NULL,
  created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
  CONSTRAINT fk_images_uploader
    FOREIGN KEY (uploader_id) REFERENCES users (id)
);

CREATE INDEX idx_images_uploader_id ON images (uploader_id);

COMMENT ON TABLE images IS '업로드된 이미지 메타데이터';
COMMENT ON COLUMN images.id IS '이미지 ID';
COMMENT ON COLUMN images.path IS '파일 저장 경로';
COMMENT ON COLUMN images.original_filename IS '업로드 시 원본 파일명';
COMMENT ON COLUMN images.content_type IS 'MIME 타입';
COMMENT ON COLUMN images.size_bytes IS '파일 크기(byte)';
COMMENT ON COLUMN images.uploader_id IS '업로더 사용자 ID';
COMMENT ON COLUMN images.created_at IS '업로드 일시';
