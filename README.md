<br>
<h1 align="center">🧠 LibraMind-API <span style="font-size:1.2em;">🧠</span></h1>

<p align="center">
  <a href="https://spring.io/projects/spring-boot">
  <img src="https://img.shields.io/static/v1?label=Spring%20Boot&message=%3E=3.0&color=green&logo=springboot" alt="Spring Boot">
  </a>
  <a href="https://www.java.com/">
  <img src="https://img.shields.io/static/v1?label=Java&message=17%2B&color=orange&logo=openjdk" alt="Java">
  </a>
  <a href="https://www.mysql.com/">
  <img src="https://img.shields.io/static/v1?label=MySQL&message=8.0&color=blue&logo=mysql" alt="MySQL">
  </a>
  <a href="https://docs.trychroma.com/">
  <img src="https://img.shields.io/static/v1?label=ChromaDB&message=VectorStore&color=red&logo=chroma" alt="ChromaDB">
  </a>
</p>

<p align="center">
  <b>벡터 임베딩(Vector Embedding) 기반 AI 도서 추천 관리 시스템</b><br>
  <span style="color:#56B16F">맥락 기반 도서 검색 · 감정 분석 추천 · 스마트 도서관 관리</span>
</p>

<h3 align="center">
  <a href="https://no-plan.cloud/">
    📚 LibraMind Library 📚
    <br>
    <small>🔗Live Demo Link🔗</small>
  </a>
</h3>

<br>

## ✨ 프로젝트 소개 (About The Project)
> [주의사항] <br>
> **LibraMind-API**를 활용하는 **FE**는 해당 API를 활용하여 직접 구현해야 합니다.

<br>

<b>벡터 임베딩(Vector Embedding)</b>과 <b>LLM(대규모 언어 모델)</b>을 활용하여 자연어 맥락을 이해하고, `Spring AI`를 통해 실제 사서가 추천해주는 듯한 경험을 제공합니다. 또한 도서 정보(Meta)와 실물 도서(Item)를 분리하여 관리하는 체계적인 도서관 관리 시스템을 구축했습니다.

## 🚀 주요 기능 (Key Features)

- 🤖 **AI 기반 맥락 추천**: 사용자의 자연어 질문(예: "우울할 때 읽기 좋은 힐링 소설 추천해줘")을 분석하여 Vector Store에서 유사한 도서를 검색(RAG)하고, LLM이 추천 사유를 생성합니다.
- 📚 **체계적인 도서 관리**: 
  - **BookMeta**: 도서 자체의 정보(제목, 저자, 출판사, 줄거리 등) 관리
  - **BookItem**: 실제 도서관에 비치된 실물 도서(상태, 위치) 관리
- 🔐 **보안 인증 시스템**: JWT 기반의 Access/Refresh 토큰 발급 및 토큰 블랙리스트(로그아웃) 기능 지원.
- 🔄 **대출/반납 프로세스**: 실물 도서의 대출 가능 여부 확인 및 대출 이력 추적.
- 👥 **권한 관리 (RBAC)**: 관리자(도서 등록/수정/삭제)와 일반 사용자(대출/검색)의 권한 분리.

<br>

## 📁 폴더 구조 (Folder structure)
```bash
src/main/java/com/no_plan/library_api/
├── config/              # WebMvc 설정 및 리소스 핸들러
├── controller/          # REST API 엔드포인트 (AI, Auth, Book, Loan, User)
├── dto/                 # 데이터 전송 객체 (Request/Response)
├── entity/              # JPA 엔티티 (BookMeta, BookItem, User, Loan)
├── repository/          # JPA 레포지토리 및 VectorStore 접근
├── security/            # JWT Provider, 필터 및 Security 설정
├── service/             # 비즈니스 로직 (AiService, BookService 등)
└── statusEnum/          # 도서 상태 및 대출 상태 열거형 (Enum)
```

<br>

## 🖥️ 실행 환경 및 요구사항 (Run Environment)

- **JDK:** Java 17 이상
- **Framework:** Spring Boot 3.x
- **Database:** MySQL 8.0+
- **Vector DB:** ChromaDB
- **AI Provider:** OpenAI API Key


## ✅ 지원 서비스 (Supported Services)

### AI 기능
* **Vector Search:** 도서의 메타데이터(제목, 줄거리, 저자)를 임베딩하여 ChromaDB에 저장하고 의미론적 검색을 수행합니다.
* **Generative Response:** 검색된 도서 정보를 바탕으로 ChatClient가 사용자에게 친절한 추천 코멘트를 생성합니다.

### 핵심 서비스
* **이미지 호스팅:** 도서 표지 이미지를 로컬 파일 시스템에 저장하고 서빙합니다 (`/images/**`).
* **사용자 관리:** Custom UserDetails를 통한 회원가입, 로그인, 정보 수정 기능.

---

<br>

## ⚡️ 설치 및 실행 (Quick Start)

### 1. 저장소 클론 (Clone)

```bash
git clone https://github.com/your-repo/LibraMind-API.git
cd LibraMind-API
```

### 2. 환경 설정 (Environment Setup)

**`application.yml` 또는 `application.properties` 파일에 아래 변수들을 설정하세요:**

```properties
# 🗄️ 데이터베이스 설정
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=<DB_USERNAME>
spring.datasource.password=<DB_PASSWORD>

# 🧠 AI 설정 (OpenAI 예시)
spring.ai.openai.api-key=<YOUR_OPENAI_API_KEY>
spring.ai.vectorstore.chroma.client.host=http://localhost:8000
spring.ai.vectorstore.chroma.initialize-schema=true

# 🔐 JWT 비밀키 설정
jwt.secret=<YOUR_VERY_LONG_SECRET_KEY>

# 📂 파일 업로드 경로 및 CORS
file.upload-dir=./uploads
app.cors.allowed-origins=http://localhost:3000
```

> [주의사항] <br>
> **ChromaDB**가 실행 중이어야 벡터 검색이 가능합니다. 

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 4. 관리자 계정 설정 (선택 사항)
`signup` API는 기본적으로 일반 사용자(`ROLE_USER`)를 생성합니다. 도서 등록 기능을 사용하려면 DB에서 직접 권한을 수정해야 합니다.
```sql
UPDATE users SET is_admin = 1 WHERE id = 'your_admin_id';
```

<br>

## 💡 **API 사용 예시 (Usage Example)**

<details>
<summary><b>📋 [1. 회원가입 & 로그인] (Authentication)</b></summary>

**1-1. 회원가입 (Sign Up)**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
     -H "Content-Type: application/json" \
     -d '{
           "id": "myuser",
           "password": "mypassword",
           "name": "홍길동",
           "phoneNum": "010-1234-5678",
           "email": "test@example.com"
         }'
```

**1-2. 로그인 (Login)**
```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
           "id": "myuser",
           "password": "mypassword"
         }'
```
> **Response:** `accessToken`과 `refreshToken`이 반환됩니다. 이후 요청 헤더에 `Authorization: Bearer <accessToken>`을 포함해야 합니다.
</details>

<br>

<details>
<summary><b>📘 [2. 도서 등록 (관리자)] (Book Management)</b></summary>

> [주의사항] <br>
> 도서 등록은 **관리자(ROLE_ADMIN)** 권한이 필요합니다. 등록 시 **Vector DB**에 임베딩 데이터가 자동 생성됩니다.

```bash
# 이미지 파일(cover.jpg)이 현재 경로에 있어야 합니다.
curl -X POST http://localhost:8080/api/books \
     -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>" \
     -F "image=@cover.jpg;type=image/jpeg" \
     -F 'request={
           "title": "리얼 유럽 2024",
           "author": "김여행",
           "publisher": "여행출판사",
           "category": "여행",
           "description": "초보자도 쉽게 따라하는 유럽 여행 가이드. 맛집부터 명소까지 완벽 정리!"
         };type=application/json'
```

**도서 아이템(실물) 추가**
```bash
# 위에서 생성된 metaId를 사용하여 실물 도서를 추가합니다.
curl -X POST http://localhost:8080/api/books/{metaId}/items \
     -H "Authorization: Bearer <ADMIN_ACCESS_TOKEN>"
```
</details>

<br>

<details open>
<summary><b>🤖 [3. AI 도서 추천] (AI Recommendation)</b></summary>

> **Vector DB**에 저장된 도서 정보를 바탕으로 AI가 답변합니다.

```bash
curl -X POST http://localhost:8080/api/ai/recommend \
     -H "Content-Type: application/json" \
     -d '{
           "query": "유럽 여행 가고 싶은데 가이드북 추천해줘."
         }'
```

**응답 예시:**
```json
{
    "aiComment": "유럽 여행을 계획 중이시군요! 여행의 설렘을 더해줄 가이드북을 추천해 드립니다.",
    "recommendedBooks": [
        {
            "metaId": 10,
            "title": "리얼 유럽 2024",
            "author": "김여행",
            "description": "초보자도 쉽게 따라하는 유럽 여행 가이드..."
        }
    ]
}
```
</details>

<br>

<details>
<summary><b>📚 [4. 대출 및 반납] (Loan & Return)</b></summary>

**4-1. 도서 대출 (Loan)**
```bash
curl -X POST http://localhost:8080/api/loans \
     -H "Authorization: Bearer <USER_ACCESS_TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{
           "bookItemId": "uuid-book-item-id"
         }'
```

**4-2. 내 대출 기록 조회 (History)**
```bash
curl -X GET http://localhost:8080/api/loans/me \
     -H "Authorization: Bearer <USER_ACCESS_TOKEN>"
```

**4-3. 도서 반납 (Return)**
```bash
curl -X POST http://localhost:8080/api/loans/{loanId}/return \
     -H "Authorization: Bearer <USER_ACCESS_TOKEN>"
```
</details>

---

## ✍️ 작성자 (Author)

이 프로젝트는 **[LibraMind]** 서비스의 백엔드 API 서버입니다.

* **Team:** NoPlan
* **Stack:** Spring Boot, Spring AI, JPA, MySQL
* **Contact:** <xiest@naver.com>
