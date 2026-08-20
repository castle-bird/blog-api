# 개인 블로그 API

## 목적

- Tistory나 Velog 플랫폼에 의존하지 않는 개인 블로그를 만든다.
- Java/Spring 백엔드 개발 역량을 쌓기 위해 MVP부터 구현하고, 기능을 점진적으로 확장한다.

## 기술 스택

| 구분                  | 기술                                     |
| --------------------- | ---------------------------------------- |
| Language              | Java 21                                  |
| Framework             | Spring Boot 3.5                          |
| Security              | Spring Security, JWT (Nimbus JOSE + JWT) |
| Persistence           | Spring Data JPA, PostgreSQL, Flyway      |
| Refresh Token store   | Redis                                    |
| API documentation     | springdoc-openapi (Swagger UI)           |
| Build                 | Gradle                                   |

## 로컬 실행

필수 환경은 JDK 21과 Docker다.

- Docker Compose와 Spring Boot 둘 다 `.env` 하나를 읽는다: `POSTGRES_USER`, `POSTGRES_PASSWORD`, `REDIS_PASSWORD`, `JWT_SECRET`, `JWT_ISSUER`, `ADMIN_USERNAME`, `ADMIN_PASSWORD`, `ADMIN_EMAIL`, `ADMIN_NICKNAME`.
- `JWT_SECRET`은 Base64 문자열이어야 하며, 디코딩 결과가 HS256에 필요한 32바이트 이상이어야 한다.
- `.env.example`은 변수 목록을 위한 예시다. Spring Boot가 자동으로 읽지 않으며, `.env`는 직접 만들어야 한다.
- `POSTGRES_HOST`, `REDIS_HOST`, `CORS_ALLOWED_ORIGINS`는 prod 프로필에서 사용한다.

```powershell
docker compose up -d
.\gradlew.bat bootRun --args='--spring.profiles.active=local'
```

Swagger UI는 `http://localhost:8080/swagger-ui/index.html`에서 확인한다.

```powershell
.\gradlew.bat test
```

전체 테스트는 Testcontainers PostgreSQL을 사용하므로 Docker가 실행 중이어야 한다.

## 인증·인가

- Spring Security는 `SessionCreationPolicy.STATELESS`로 구성했고, Access Token 인증에 JWT를 사용한다.
- 로그인·토큰 재발급·로그아웃, 게시글·카테고리 조회, Swagger 문서 경로와 `OPTIONS` 요청은 공개한다. 그 외 API는 인증이 필요하다.
- 개인 블로그이기 때문에 회원가입은 작성하지 않았다.
- 개인 블로그의 관리자 계정을 중심으로 운영하므로, 현재 역할은 ADMIN과 USER만 간략히 구분한다.

### Access Token

- Access Token(AT)은 HS256으로 서명한 JWT이며, `sub`에는 사용자 ID를 담는다.
- 발급자(`iss`), 발급 시각(`iat`), 만료 시각(`exp`), 서명과 알고리즘을 검증한다. 기본 만료 시간은 15분이다.
- 클라이언트는 `Authorization: Bearer <AT>` 헤더로 AT를 전송한다.
- **AT에는 역할 정보를 넣지 않는다.** 토큰 검증 후 사용자 ID를 추출해 DB에서 사용자를 조회한다.
  - **이점:** 역할 변경이나 탈퇴 처리 결과가 바로 다음 요청부터 반영된다.
  - **단점:** 대신 인증된 요청마다 사용자 조회가 발생한다.
- 게시글·카테고리 조회는 공개하며, 생성·수정·삭제는 `ADMIN` 역할만 허용한다.

### Refresh Token

- Refresh Token(RT)은 JWT가 아닌 암호학적으로 안전한 32바이트 난수를 Base64URL로 인코딩한 값이다.
  - **이유:** 사용자 정보가 필요 없는 재발급 전용 토큰이므로 서명된 JWT로 만들 필요가 없다고 판단했다.
- 기본 만료 시간은 14일이다. 로그인 또는 재발급 응답에서 RT는 `HttpOnly`, `SameSite=Lax`, `/api/auth` 경로 전용 쿠키로 전달된다.
  - 운영 환경에서는 `Secure` 쿠키를 사용한다.
- Redis에는 **RT 원문 대신 SHA-256 해시만 저장**한다. Redis 데이터가 노출되더라도 저장된 값만으로 RT 원문을 직접 사용할 수 없도록 한다.
- 재발급 시 Lua 스크립트로 RT를 원자적으로 교체(Rotation)한다. 기존 RT는 즉시 무효화되므로, 이미 사용한 RT로 다시 재발급할 수 없다.
- 로그아웃 시 해당 RT 세션을 Redis에서 제거하고 쿠키를 만료시킨다.

### 동시 로그인 제한

- 사용자당 RT 세션은 최대 3개까지 유지한다.
- 새 로그인으로 제한을 넘으면 Redis에서 만료 시각이 가장 이른 RT 세션을 제거한다. 이는 기기 종류를 구분하는 기능이 아니라 세션 수를 제한하는 기능이다.
- RT 세션이 제거되어도 이미 발급된 AT는 자체 만료 시각까지 유효하다. 따라서 해당 세션은 AT 만료 후 재발급할 수 없으며 다시 로그인해야 한다.

### 인증 흐름

```text
로그인
  → 이메일·BCrypt 비밀번호 검증
  → AT/RT 발급 + RT 해시를 Redis에 저장
  → AT는 응답 본문, RT는 HttpOnly 쿠키로 반환

보호 API 요청
  → Bearer AT 검증
  → sub의 사용자 ID로 DB에서 사용자·역할 조회
  → SecurityContext에 인증 정보 설정

AT 재발급
  → RT 쿠키의 해시로 Redis 세션 검증
  → 기존 RT 무효화 및 새 AT/RT 발급
```

### Redis의 Refresh Token 키

```text
rt:token:{rtHash}         (STRING) -> sessionId
rt:session:{sessionId}    (HASH)   -> userId, activeTokenHash
rt:user:{userId}:sessions (ZSET)   -> sessionId, RT 만료 시각
```

## 트러블슈팅: 방문자 없는데 조회수만 증가

배포 직후 실제 방문자가 거의 없던 시점에, 새로고침으로는 늘지 않는데 몇 분 간격으로 조회수가 계속 증가하는 현상을 발견했다.

### 진단

**1. 백엔드 nginx 로그**
`GET /api/posts/{id}` 요청이 매번 다른 AWS IP 대역에서, User-Agent `axios/1.19.0`(브라우저 아님)으로 몇 분 간격 반복 유입되는 것을 확인했다.

**2. 프론트(Vercel) Runtime Logs**
요청 URL에 `_rsc=...` 쿼리스트링이 붙어 있었다. 이는 실제 페이지 이동이 아니라 **Next.js의 자동 Link 프리페치**가 백그라운드에서 RSC 데이터를 미리 가져오는 요청이었다. User-Agent는 실제 Chrome — 홈페이지를 열어둔 브라우저 탭이 있으면, 그 안의 게시글 링크를 Next.js가 주기적으로 미리 당겨오고 있었다.

> 로그에 함께 보인 `HeadlessChrome`/`undici` UA는 Vercel이 배포 직후 자동 수행하는 헬스체크·스크린샷 생성으로, 1회성이라 이번 문제와는 무관하다고 판단해 배제했다.

### 원인

```text
브라우저 탭 (같은 IP, 유지)
  └─ Next.js Link 프리페치
       └─ Vercel 서버(SSR)가 대신 백엔드 호출  ← 매번 다른 서버리스 IP
            └─ 백엔드 "같은 IP 24시간 1회" 중복 방지 로직 무력화
```

조회수 중복 방지는 클라이언트 IP 기준으로 동작하는데, 실제로는 사용자 브라우저가 백엔드를 직접 호출한 적이 한 번도 없었다. 매 요청이 Vercel 서버를 거치며 서로 다른 IP로 나가, IP 기반 중복 방지가 매번 새 방문으로 오인했다.

### 조치

콘텐츠 렌더링(SSR 유지)과 조회수 기록(클라이언트 마운트 시 1회)을 분리하는 방향으로 프론트 수정을 진행했다. 서버 컴포넌트가 대신 호출하는 구조 자체가 원인이므로, 신뢰 가능한 신호(실제 클라이언트 마운트)에서만 조회수를 기록하도록 바꿔야 근본적으로 해결된다.

**교훈**: IP 기반 중복 방지는 요청이 "누구를 대신해서" 왔는지까지는 구분하지 못한다. SSR/프리페치처럼 서버가 클라이언트 대신 요청을 만드는 구조에서는 IP가 클라이언트 신원을 보장하지 않는다.
