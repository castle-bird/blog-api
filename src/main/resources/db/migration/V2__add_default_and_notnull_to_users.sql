-- 초기 설정은 생성시에 수정날짜가 없는 것을 생각했는데,
-- JPA의 기본 @LastModifiedDate 어노테이션이 생성에도 값을 넣기때문에
-- 속성을 추가하고 명시적으로 작성

-- 1. 기본값(DEFAULT) 설정
ALTER TABLE users
  ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

-- 2. NOT NULL 제약조건 추가
ALTER TABLE users
  ALTER COLUMN updated_at SET NOT NULL;
