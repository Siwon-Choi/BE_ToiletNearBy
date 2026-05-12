<div align="center">

<br>

# ToiletNearBy Backend

위치 기반 공중 화장실 검색과 후기 공유 기능을 제공하는 Spring Boot 백엔드 프로젝트입니다.

위치(사용자 위치 혹은 검색한 위치)를 기준으로 주변 화장실을 조회하고, 최단 거리 혹은 평점 순으로 화장실을 탐색할 수 있으며,

길찾기 기능과 커뮤니티 기능을 함께 제공하는 REST API 서버입니다.

<br>

<img src="./docs/assets/toiletNearBy.gif" alt="ToiletNearBy demo" width="720">

<br>

</div>

<br>

## Index
- [Overview](#overview)
- [API Docs](#api-docs)
- [Tech Stack](#tech-stack)
- [Key Features](#key-features)
- [Core Implementation](#core-implementation)
  
<br>

## Overview

공중 화장실 안내 서비스는 단순 위치 정보만으로는 실제 이용 판단에 필요한 정보가 부족합니다.

ToiletNearBy Backend는 사용자 위치를 기준으로 주변 화장실을 조회하고, 사용자 후기, 평점, 비밀번호 공유, Kakao 장소 검색을 함께 제공하도록 설계했습니다.

구현 과정에서는 다음 기준을 중심으로 백엔드 구조를 구성했습니다.

- JWT 인증은 Spring Security OAuth2 Resource Server 기반으로 처리
- DTO가 도메인에 직접 침투하지 않도록 DTO/VO/Domain 역할 분리
- Service가 Spring Data JPA에 직접 의존하지 않도록 Repository interface와 JPA adapter 분리
- 성능 측정 기능은 외부 API로 노출하지 않고 console runner로 분리
- 민감할 수 있는 비밀번호 수정 값은 URL path가 아니라 request body로 전달
- 초기 데이터는 애플리케이션 API가 아니라 DB import 절차로 관리

<br>

## API Docs

[![Static Swagger UI](https://img.shields.io/badge/Static%20Swagger%20UI-GitHub%20Pages-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](https://siwon-choi.github.io/BE_ToiletNearBy/)

API 문서는 `main` 브랜치에 push될 때 GitHub Actions에서 OpenAPI 스펙을 생성하고, GitHub Pages에 정적 Swagger UI로 게시합니다.

GitHub Pages 문서는 API 명세 확인용 정적 문서입니다. 실제 API 호출 테스트는 애플리케이션을 로컬에서 실행한 뒤 아래 주소에서 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

<br>

## Tech Stack

### Backend

[![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)

### Database / Cache

[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Supabase](https://img.shields.io/badge/Supabase-3FCF8E?style=for-the-badge&logo=supabase&logoColor=white)](https://supabase.com/)
[![H2](https://img.shields.io/badge/H2-09476B?style=for-the-badge&logo=h2database&logoColor=white)](https://www.h2database.com/)
[![Caffeine](https://img.shields.io/badge/Caffeine%20Cache-5A3E2B?style=for-the-badge)](https://github.com/ben-manes/caffeine)

### Test

[![JUnit 5](https://img.shields.io/badge/JUnit%205-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)
[![Mockito](https://img.shields.io/badge/Mockito-78A641?style=for-the-badge)](https://site.mockito.org/)
[![MockMvc](https://img.shields.io/badge/MockMvc-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework.html)
[![Spring Boot Test](https://img.shields.io/badge/Spring%20Boot%20Test-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://docs.spring.io/spring-boot/reference/testing/)

<br>

## Key Features

### 1. 사용자 인증

- 회원가입
- 로그인 및 JWT 발급
- `Authorization: Bearer {token}` 기반 인증
- 내 정보 조회
- 관리자 권한 테스트 API

### 2. 화장실 조회

- 전체 화장실 목록 조회
- 화장실 단건 상세 조회
- 사용자 좌표와 반경 기반 주변 화장실 조회
- 좌표 기반 거리 계산

### 3. 후기 / 평점

- 화장실별 후기 목록 조회
- 로그인 사용자의 후기 등록, 수정, 삭제
- 내가 작성한 후기 조회
- 후기 평점 수정
- 후기 등록 시 화장실 누적 평점 반영

### 4. 비밀번호 공유

- 로그인 사용자 기준 화장실 비밀번호 등록
- 저장한 비밀번호 조회
- 비밀번호 수정
- 비밀번호 삭제

### 5. 장소 검색

- Kakao Local API 기반 키워드 장소 검색
- 검색어, 좌표, 반경, 페이지 조건 지원
- 외부 API 의존성을 `PlaceSearchClient` interface로 분리
- 현재 구현체는 Kakao Local API client

### 6. 위치 검색 최적화

- 전체 조회 baseline 제공
- Bounding Box 후보 조회
- 좌표 복합 인덱스 활용
- 격자 기반 후보군 캐싱
- 최종 응답은 실제 사용자 좌표 기준 거리로 재필터링

### 7. Benchmark

- `benchmark` profile에서만 실행
- API endpoint가 아니라 `CommandLineRunner`로 콘솔 출력
- 전체 조회, index 조회, index + grid cache 조회 방식을 동일 조건으로 비교

<br>

## Core Implementation

### JWT Authentication

로그인 성공 시 서버가 JWT를 발급하고, 이후 요청은 `Authorization` 헤더의 Bearer Token으로 인증합니다.

토큰 검증과 Bearer Token 인증 처리는 Spring Security OAuth2 Resource Server에 위임했습니다.

```text
POST /api/users/login
-> UserService
-> BCrypt password matches
-> JwtTokenProvider
-> accessToken response
-> Authorization: Bearer {accessToken}
-> Resource Server JWT validation
-> Authentication 생성
-> Controller에서 인증 사용자 사용
```

JWT 인증/인가 실패 응답 형식은 `SecurityExceptionHandler`로 통일합니다.

일반 `exceptionHandling`뿐 아니라 `oauth2ResourceServer` 내부에도 동일 핸들러를 등록해, 토큰 만료/서명 오류처럼 Resource Server 단계에서 잡히는 실패도 동일한 JSON 형식으로 응답합니다.

자세한 배경은 [TroubleShooting.md](./TroubleShooting.md)에 정리되어 있습니다.

관련 파일:

```text
src/main/java/com/toiletnearby/global/security/SecurityConfig.java
src/main/java/com/toiletnearby/global/security/SecurityExceptionHandler.java
src/main/java/com/toiletnearby/global/security/jwt/JwtTokenProvider.java
src/main/java/com/toiletnearby/user/service/UserService.java
```

<br>

### Nearby Toilet Search

주변 화장실 검색은 사용자 좌표와 반경을 기준으로 동작합니다.

단순 구현에서는 모든 화장실을 조회한 뒤 애플리케이션에서 거리 계산을 수행할 수 있지만, 데이터가 늘어나면 매 요청마다 전체 테이블을 읽는 문제가 생깁니다.

이를 줄이기 위해 DB에서 Bounding Box 후보를 먼저 조회하고, 후보 데이터에 대해서만 실제 거리를 계산합니다.

**전체 조회 흐름**

```text
전체 화장실 조회
-> 모든 화장실 거리 계산
-> 반경 필터
-> 응답 DTO 변환
```

**Bounding Box 조회 흐름**

```text
사용자 좌표와 반경으로 Bounding Box 계산
-> DB에서 후보 조회
-> 실제 거리 계산
-> 반경 필터
-> 응답 DTO 변환
```

관련 파일:

```text
src/main/java/com/toiletnearby/toilet/service/ToiletService.java
src/main/java/com/toiletnearby/toilet/service/ToiletLocationCandidateService.java
src/main/java/com/toiletnearby/toilet/repository/jpa/ToiletJpaRepository.java
```

<br>

### Grid Cache

GPS 좌표는 작은 흔들림이 자주 발생합니다.

정확한 좌표를 그대로 캐시 키로 쓰면 거의 같은 위치 요청도 매번 다른 key가 됩니다. 그래서 좌표를 일정한 grid 값으로 보정한 뒤 후보 조회 결과를 캐싱합니다.

```text
사용자 좌표
-> grid 좌표로 보정
-> grid + range 기준 후보군 캐싱
-> 실제 사용자 좌표 기준 거리 계산
-> 최종 반경 필터
```

이 방식은 H3, Geohash, S2처럼 공간을 cell/grid 단위로 나누어 위치 데이터를 다루는 geospatial indexing 계열 아이디어를 단순화한 구현입니다.

최종 결과 자체를 캐싱하지 않고 후보군만 캐싱한 뒤, 실제 사용자 좌표 기준으로 다시 거리 계산을 수행해 정확도를 유지합니다.
