# 🍔 Hambug (햄버그) - Backend

> **햄버거 애호가들을 위한 커뮤니티 및 정보 제공 서비스**

Hambug는 전 세계의 다양한 햄버거 정보를 공유하고, 사용자 간의 소통을 돕는 커뮤니티 플랫폼의 백엔드 시스템입니다. 현대적인 기술 스택을 활용하여 확장성 있고 안정적인 API를 제공합니다.

## ✨ 주요 기능

### 1. 인증 및 권한 관리 (Auth)
- **소셜 로그인**: 카카오(Kakao) 및 애플(Apple) OAuth2 인증 지원.
- **JWT 인증**: RSA 비대칭 키 알고리즘(Private/Public Key)을 사용한 보안성 높은 토큰 기반 인증.
- **사용자 프로필**: 닉네임, 프로필 이미지 관리 및 마이페이지 기능.

### 2. 커뮤니티 (Board)
- **게시글 관리**: 이미지 업로드를 포함한 게시글 CRUD 기능 (AWS S3 연동).
- **무한 스크롤**: `lastId` 기반의 No-offset 페이징으로 대용량 데이터 처리 최적화.
- **인기 게시글**: Redis 기반의 실시간 점수 산정 알고리즘을 통한 트렌딩 게시글 노출.
- **카테고리**: 다양한 카테고리별 게시글 필터링 제공.

### 3. 소셜 상호작용
- **댓글 시스템**: 게시글별 댓글 작성 및 관리 기능.
- **좋아요**: 게시글 좋아요 토글 기능 및 실시간 카운트.
- **신고 시스템**: 건전한 커뮤니티 유지를 위한 게시글 및 사용자 신고 기능.

### 4. 햄버거 정보 서비스
- **햄버거 백과사전**: 다양한 햄버거 메뉴 정보 제공 및 관리.
- **오늘의 추천**: 알고리즘 기반의 일일 햄버거 추천 기능.

### 5. 알림 서비스 (Notification)
- **푸시 알림**: FCM(Firebase Cloud Messaging)을 활용한 실시간 알림 전송.
- **이벤트 기반**: 비동기 이벤트를 통한 시스템 간 결합도 완화 및 확장성 확보.

## 🛠 Tech Stack

### Framework & Language
- Java 17
- Spring Boot 3.4.8

### Data Storage
- MySQL (Persistence)
- Redis (Caching, Trending Ranking)
- AWS S3 (Media Storage)

### Security & Auth
- Spring Security
- OAuth2 Client (Kakao, Apple)
- JJWT (JSON Web Token)

### Library & Tools
- **Querydsl**: Type-safe한 동적 쿼리 작성.
- **Firebase Admin SDK**: FCM 푸시 알림 연동.
- **Springdoc OpenAPI**: Swagger UI를 통한 API 문서 자동화.
- **Lombok**: 보일러플레이트 코드 감소.

## 📁 Project Structure
```
src/main/java/com/hambug/Hambug/
├── domain/               # 도메인별 패키지 (Admin, Auth, Board, Burger, Comment, Like, User 등)
│   ├── controller/       # API 엔드포인트
│   ├── service/          # 비즈니스 로직
│   ├── repository/       # 데이터 액세스
│   ├── dto/              # 데이터 전송 객체
│   └── entity/           # JPA 엔티티
├── global/               # 공통 설정 및 유틸리티
│   ├── config/           # 보안, 스웨거, JPA 등 설정
│   ├── exception/        # 전역 예외 처리
│   ├── response/         # 공통 응답 포맷
│   └── util/             # 공통 유틸리티
```

## 🚀 시작하기

### Prerequisites
- JDK 17
- MySQL
- Redis

### Installation & Run
1. 저장소 클론
   ```bash
   git clone https://github.com/your-repo/Hambug-Backend.git
   ```
2. `src/main/resources/application.yaml` 설정 (DB, AWS, OAuth 등 정보 입력)
3. 빌드 및 실행
   ```bash
   ./gradlew bootRun
   ```

### API Documentation
서버 실행 후 아래 주소에서 API 명세서를 확인할 수 있습니다.
- `http://localhost:8081/api/swagger-ui.html`