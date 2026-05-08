# Troubleshooting

## Index

| 주제 | 핵심 |
| --- | --- |
| BCrypt 검증 실패 | `encode()` 결과 비교가 아니라 `matches()` 사용 |
| JWT 권한 403 | JWT `role` claim을 Spring Security 권한으로 직접 변환 |
| CSRF 403 | Authorization 헤더 기반 stateless API에서는 CSRF 비활성화 |
| Controller 테스트 한계 | `addFilters = false`는 Security 설정을 검증하지 않음 |
| Security 실패 응답 불일치 | Security 401/403은 MVC 예외 처리와 별도로 다룸 |
| 메모 번호 중복 가능성 | `count + 1` 방식은 삭제/동시 요청에서 중복 가능 |

---

## 1. BCrypt 비밀번호 검증에서 `equals()` 비교가 실패하는 문제

### 문제

회원가입 시 비밀번호를 BCrypt로 암호화해 DB에 저장한 뒤, 로그인 시 입력된 비밀번호를 다시 `encode()`해서 DB 값과 `equals()`로 비교하면 인증이 실패할 수 있습니다.

```java
String loginEncodedPassword = passwordEncoder.encode(rawPassword);

if (loginEncodedPassword.equals(savedEncodedPassword)) {
    // 같은 비밀번호여도 실패할 수 있음
}
```

### 증상

같은 원문 비밀번호를 입력했는데도 로그인 검증이 실패합니다.

### 원인

BCrypt는 비밀번호를 해싱할 때 매번 새로운 salt를 사용할 수 있습니다.

따라서 같은 원문 비밀번호라도 `encode()`를 호출할 때마다 서로 다른 해시 문자열이 생성될 수 있습니다.

```text
password123 + saltA -> hashA
password123 + saltB -> hashB
```

즉, 로그인 시 입력한 비밀번호를 다시 `encode()`한 결과와 DB에 저장된 해시 문자열을 단순 비교하면, 같은 비밀번호라도 문자열이 달라 실패할 수 있습니다.

### 해결

로그인 검증에서는 `encode()` 결과를 직접 비교하지 않고 `PasswordEncoder.matches(rawPassword, encodedPassword)`를 사용합니다.

```java
passwordEncoder.matches(rawPassword, savedEncodedPassword);
```

BCrypt 해시 문자열에는 salt와 cost 정보가 포함되어 있습니다.  
`matches()`는 저장된 해시에서 salt와 cost를 꺼내 입력 비밀번호에 동일하게 적용한 뒤 결과를 비교합니다.

### 정리

회원가입에서는 `encode()`로 저장하고, 로그인에서는 `matches()`로 검증해야 합니다.

---

## 2. Resource Server 인증 후 권한이 있는데도 `403 Forbidden`이 발생하는 문제

### 문제

Spring Security OAuth2 Resource Server로 JWT 인증을 처리했는데도, 권한이 필요한 API에서 `403 Forbidden`이 발생할 수 있습니다.

### 증상

```text
JWT 검증 성공
Authentication 객체 생성됨
hasAuthority("ADMIN") 조건에서 403 Forbidden 발생
```

토큰 자체는 유효하지만 권한 검사에서 막힙니다.

### 원인

JWT 인증 성공과 권한 검사 성공은 별개의 문제입니다.

Resource Server의 기본 JWT 권한 변환기는 보통 `scope` 또는 `scp` claim을 권한으로 변환하고, `SCOPE_` prefix를 붙입니다.

하지만 현재 프로젝트의 JWT는 `role` claim에 권한을 담습니다.

```json
{
  "sub": "tester",
  "role": "USER"
}
```

즉, 토큰은 유효해도 Spring Security의 기본 변환기가 `role` claim을 권한으로 읽지 않으면, Authentication 객체의 권한 목록이 비어 있거나 예상과 다르게 매핑될 수 있습니다.

### 해결

`JwtAuthenticationConverter`를 직접 설정해서 JWT의 `role` claim을 Spring Security 권한으로 변환했습니다.

```java
converter.setJwtGrantedAuthoritiesConverter(jwt -> {
    String role = jwt.getClaimAsString("role");

    if (role == null || role.isBlank()) {
        return List.of();
    }

    return List.of(new SimpleGrantedAuthority(role));
});
```

현재 프로젝트는 `USER`, `ADMIN`을 그대로 권한 문자열로 사용합니다.

```java
.hasAuthority("ADMIN")
```

### 정리

JWT 인증이 성공했다는 것은 토큰의 서명과 만료 시간이 유효하다는 뜻입니다.  
권한 검사가 통과하려면 JWT claim, 권한 변환기, `SecurityConfig`의 권한 검사 방식이 일치해야 합니다.

---

## 3. JWT 기반 REST API에서 CSRF 설정 때문에 POST 요청이 `403 Forbidden` 되는 문제

### 문제

Spring Security를 적용한 뒤 로그인, 회원가입, 메모 작성 같은 POST 요청이 Controller까지 도달하지 못하고 `403 Forbidden`으로 막힐 수 있습니다.

### 증상

```text
POST /api/users/login
POST /api/users/register
POST /api/toilet/memo

응답: 403 Forbidden
Controller 또는 Service 로직 실행 안 됨
```

인증 로직이나 비즈니스 로직 문제가 아닌데도 Security 단계에서 요청이 차단됩니다.

### 원인

Spring Security는 기본적으로 CSRF 보호를 활성화합니다.

CSRF 보호가 켜져 있으면 POST, PUT, DELETE 같은 상태 변경 요청에는 CSRF token이 필요합니다.

하지만 현재 프로젝트는 서버 세션과 쿠키 기반 인증이 아니라, 클라이언트가 JWT를 직접 헤더에 담아 보내는 stateless REST API 구조입니다.

```text
Authorization: Bearer {accessToken}
```

이 구조에서는 서버가 CSRF token을 발급하고 검증하는 흐름을 따로 두지 않습니다. 따라서 CSRF 설정을 그대로 두면 정상적인 API 요청도 403으로 막힐 수 있습니다.

### 해결

JWT 기반 stateless REST API 구조에 맞게 CSRF를 비활성화했습니다.

```java
.csrf(AbstractHttpConfigurer::disable)
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

### 주의

CSRF를 항상 꺼도 된다는 뜻은 아닙니다.

JWT를 사용하더라도 access token을 HttpOnly Cookie에 저장하고, 브라우저가 자동으로 쿠키를 붙이는 구조라면 CSRF 위험이 다시 생깁니다. 이 경우에는 CSRF 방어 전략을 별도로 설계해야 합니다.

### 정리

CSRF 설정은 인증 방식과 토큰 저장 위치에 따라 판단해야 합니다.  
현재 프로젝트처럼 Authorization 헤더 기반 stateless JWT 인증에서는 CSRF를 비활성화하는 것이 자연스럽습니다.

---

## 4. `@WebMvcTest(addFilters = false)`에서 놓친 Security 설정 오류

### 문제

Controller 테스트는 통과했는데, 실제 Security 설정을 적용하면 로그인 API가 `401 Unauthorized`로 막힐 수 있습니다.

### 증상

```text
UserControllerTest 통과
실제 POST /api/users/login 요청은 401 Unauthorized 발생
```

Controller 자체는 정상인데, 실제 실행 환경에서는 Security 필터 체인이 요청을 먼저 막습니다.

### 원인

Controller 슬라이스 테스트에서 Security 필터를 끄면 Controller 요청/응답만 빠르게 검증할 수 있습니다.

```java
@AutoConfigureMockMvc(addFilters = false)
```

하지만 이 경우 다음 항목은 검증하지 못합니다.

```text
어떤 API가 permitAll()인지
어떤 API가 인증을 요구하는지
권한 조건이 맞는지
인증 실패 시 401/403이 의도대로 나오는지
```

즉, Controller 테스트가 통과해도 실제 요청에서는 Security 설정 때문에 막힐 수 있습니다.

### 해결

회원가입과 로그인은 실제 요청 메서드인 POST 기준으로 명시적으로 열어두었습니다.

```java
.requestMatchers(
        HttpMethod.POST,
        "/api/users/register",
        "/api/users/login"
).permitAll()
```

그리고 Security 설정까지 함께 검증하기 위해 `@SpringBootTest`와 `@AutoConfigureMockMvc`를 사용하는 통합 테스트를 별도로 둡니다.

### 정리

Controller 슬라이스 테스트는 빠르지만 Security 설정까지 검증하지 않습니다.  
인증/인가 설정은 별도의 Security 통합 테스트로 검증해야 실제 요청에서 발생하는 401/403 문제를 잡을 수 있습니다.

---

## 5. Spring Security 인증/인가 실패 응답 형식이 MVC 예외와 달라지는 문제

### 문제

Spring MVC 영역에서 발생한 예외는 `@ControllerAdvice`나 공통 예외 응답 DTO로 정리할 수 있습니다.

하지만 Spring Security 필터 체인에서 발생한 인증/인가 실패는 Controller까지 도달하기 전에 처리됩니다.

### 증상

```text
MVC 예외: 프로젝트에서 정의한 JSON 응답
인증 실패: Spring Security 기본 401 응답
인가 실패: Spring Security 기본 403 응답
```

같은 API 서버인데 실패 응답 형식이 상황에 따라 달라질 수 있습니다.

### 원인

Spring Security의 인증 실패와 인가 실패는 MVC 예외 처리 흐름과 다릅니다.

```text
인증 실패: AuthenticationEntryPoint
인가 실패: AccessDeniedHandler
```

이 둘은 Controller 이전의 Security 필터 체인에서 동작합니다.  
따라서 `@ControllerAdvice`만으로는 Security에서 발생한 401/403 응답 형식을 통일할 수 없습니다.

### 해결

`SecurityExceptionHandler`가 `AuthenticationEntryPoint`, `AccessDeniedHandler`를 구현하도록 하고, 인증/인가 실패도 공통 JSON 형식으로 응답하도록 정리했습니다.

```java
public class SecurityExceptionHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {
}
```

Security 설정에서도 해당 핸들러를 등록합니다.

```java
.exceptionHandling(exception -> exception
        .authenticationEntryPoint(securityExceptionHandler)
        .accessDeniedHandler(securityExceptionHandler)
)
```

### 추가 이슈: Resource Server 내부 핸들러

`exceptionHandling`에만 핸들러를 등록하면 응답 형식이 완전히 통일되지 않을 수 있습니다.

JWT 기반 Resource Server는 `oauth2ResourceServer` 설정 내부에 자체 `BearerTokenAuthenticationEntryPoint`와 `BearerTokenAccessDeniedHandler`를 가질 수 있습니다.

토큰 만료, 서명 오류, 잘못된 Bearer 형식처럼 Resource Server 단계에서 잡히는 실패는 이 자체 핸들러가 먼저 처리할 수 있습니다.

그래서 현재 프로젝트는 `oauth2ResourceServer` 안에도 동일한 `SecurityExceptionHandler`를 등록합니다.

```java
.oauth2ResourceServer(oauth2 -> oauth2
        .authenticationEntryPoint(securityExceptionHandler)
        .accessDeniedHandler(securityExceptionHandler)
        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
)
```

### 정리

REST API에서는 성공 응답뿐 아니라 실패 응답의 형식도 클라이언트와의 계약입니다.

Security 필터 체인에서 발생하는 401/403은 MVC 예외 처리와 별도로 다뤄야 합니다.  
JWT Resource Server를 사용하는 경우에는 `oauth2ResourceServer` 내부에도 동일 핸들러를 등록해야 응답 형식을 더 안정적으로 통일할 수 있습니다.

---

## 6. 화장실별 메모 번호를 `count + 1`로 만들 때 중복이 발생할 수 있는 문제

### 문제

현재 메모 기능은 특정 화장실에 달린 메모 개수를 세고, 그 값에 1을 더해서 화장실별 메모 번호를 만듭니다.

```java
long memoId = memoRepository.countByToiletId(toiletId) + 1;
```

이 방식은 단순하지만, 메모 번호를 고유한 식별자처럼 사용할 경우 데이터 정합성 문제가 생길 수 있습니다.

### 증상

같은 화장실에 대해 서로 다른 메모가 같은 `memoId`를 가질 수 있습니다.

```text
/api/toilet/1/memo/3
```

위 URL이 특정 메모 하나를 가리킨다고 기대했는데, 실제로는 같은 `toiletId`, 같은 `memoId`를 가진 메모가 여러 개 생길 수 있습니다.

### 원인 1: 삭제 후 재생성

예를 들어 1번 화장실에 메모가 3개 있다고 가정합니다.

```text
memoId: 1, 2, 3
count = 3
```

이때 2번 메모를 삭제하면 남은 메모는 다음과 같습니다.

```text
memoId: 1, 3
count = 2
```

이 상태에서 새 메모를 작성하면 `count + 1` 결과가 다시 3이 됩니다.

```text
new memoId = 2 + 1 = 3
```

이미 `memoId = 3`인 메모가 남아 있으므로 중복이 발생할 수 있습니다.

### 원인 2: 동시에 작성되는 요청

여기서 “동시에”라는 말은 정말 같은 순간에 실행된다는 뜻이 아닙니다.

두 요청이 거의 같은 시점에 들어와서, 첫 번째 요청이 DB에 저장되기 전에 두 번째 요청도 같은 count 값을 읽는 상황을 말합니다.

```text
현재 count = 3

요청 A: countByToiletId(1) -> 3
요청 B: countByToiletId(1) -> 3

요청 A: memoId = 4로 저장
요청 B: memoId = 4로 저장
```

두 요청 모두 저장 전에 같은 count 값을 읽었기 때문에, 서로 다른 메모가 같은 `memoId = 4`를 갖게 됩니다.

### 왜 데이터 정합성 문제인가?

현재 API는 `toiletId + memoId` 조합으로 특정 메모를 조회하거나 수정하는 흐름을 가집니다.

그런데 같은 화장실 안에서 `memoId`가 중복되면, 이 조합이 특정 메모 하나를 안정적으로 가리키지 못합니다.

즉, 애플리케이션이 기대하는 데이터 규칙이 DB에서 보장되지 않는 상태가 됩니다.

### 현재 프로젝트의 상태

현재 프로젝트에서는 기존 API 흐름을 유지하기 위해 `count + 1` 방식을 그대로 남겨두었습니다.

따라서 이 문제는 해결 완료 항목이 아니라, 현재 구조의 알려진 한계이자 추후 개선 과제입니다.

### 추후 개선 방향

화장실별 노출용 번호가 꼭 필요하다면 다음 중 하나를 선택할 수 있습니다.

```text
1. DB unique constraint를 걸고 충돌 시 재시도
2. 화장실별 sequence를 별도 테이블로 관리
3. 내부 식별자는 JPA 기본키 id를 사용하고, 화면용 번호는 조회 시 계산
```

특히 `max(memoId) + 1`만 사용하면 삭제 후 중복은 줄일 수 있지만, 동시 요청 중복은 완전히 막지 못합니다.

최종적으로는 DB 제약 조건이나 별도 번호 생성 전략이 필요합니다.

### 정리

`count + 1`은 단순 구현에는 쉽지만, 삭제와 동시 요청 상황에서 고유 번호를 보장하지 못합니다.

현재 프로젝트에서는 기존 API 흐름 유지를 위해 남겨두었고, 운영 수준으로 확장한다면 별도의 번호 생성 전략으로 개선해야 합니다.
