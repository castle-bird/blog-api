# Redis scripts

## 목적

`src/main/resources/redis/**/*.lua`는 여러 Redis 명령을 원자적으로 실행하기 위한 Lua Script다.

## Redis의 Refresh Token Key

| 키 이름                      | 타입 | 목적                          | 값                                                |
|------------------------------|------|-------------------------------|---------------------------------------------------|
| `rt:token:{토큰 해시}`       | SET  | 토큰 해시로 세션 조회         | `sessionId`                                       |
| `rt:session:{세션 ID}`       | HASH | Refresh Token 세션 정보 관리  | `userId`, `activeTokenHash`                       |
| `rt:user:{user ID}:sessions` | ZSET | 사용자 세션 수·만료 순서 관리 | member: `sessionId`, score: RT 만료 epoch seconds |
