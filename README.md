


<img width="1497" alt="스크린샷 2025-07-28 오전 9 55 56" src="https://github.com/user-attachments/assets/1390622a-ac69-4401-8e75-0b98d21a1052" />




<br/>
<br/>

# 0. 서비스 주소  
```bash
$ npm install
$ npm run dev
```
[서비스 링크](https://yetda.kro.kr/)


<br/>

# 1. 서비스 개요

## 🛠️ 서비스명: **크라우드 플랫폼**

**크라우드 플랫폼**은 개발자와 창작자를 위한 후원 및 투자 기반의 **IT 프로젝트 크라우드 펀딩 플랫폼**입니다.  
단순한 프로젝트 소개를 넘어, **프로젝트 등록 → 결제 → 정산 → 리뷰 및 커뮤니케이션**까지 전 과정을 하나의 시스템에서 통합 관리할 수 있도록 설계되었습니다.  

**후원형**, **구매형** 프로젝트를 모두 지원하며, 실시간 알림, 정산 자동화, 신고 처리, 팔로우 기반 소셜 기능 등을 통해 창작자와 사용자 간의 신뢰 기반 펀딩 환경을 제공합니다.

<br>


## 🔍 기획 배경

기존의 개발자 중심 크라우드 펀딩 플랫폼은 다음과 같은 문제점을 갖고 있었습니다:

- 후원/구매 프로젝트 관리 시스템의 부재  
- 사용자와 창작자 간 피드백 및 소통의 어려움  
- 정산/통계/관리 기능이 부족하여 운영 효율성 저하  
- 플랫폼 내 커뮤니티성 기능(팔로우, Q&A 등) 미비  

이를 해결하기 위해, **후원형 + 구매형 프로젝트 이중 구조**, **SSO 기반 로그인**, **통합 알림 및 관리자 기능**을 갖춘 플랫폼을 직접 기획하고 구현하게 되었습니다.


<br>


## 💡 주요 포인트

- **후원형/구매형 프로젝트 등록 기능**  
  → 프로젝트 유형 선택, 옵션 설정, 이미지/내용 등록 가능

- **토스 결제 연동**  
  → 후원형/구매형 프로젝트에 대해 토스페이 결제 기능 구현

- **실시간 알림(SSE)**  
  → 후원 완료, 구매 완료, 승인/신고 처리 등에 대한 실시간 알림 제공

- **리뷰/신고/Q&A/공지사항 관리 기능**  
  → 사용자 커뮤니케이션 중심 기능 강화

- **팔로우/팔로잉 기반 사용자 연결 구조**  
  → 창작자 구독, 신규 프로젝트 알림 기능 제공

- **마이페이지 기능**  
  → 회원 정보 관리, 구매/후원 내역, 정산 이력, 알림 관리

- **소셜 로그인 (카카오, 깃허브)**  
  → 간편 로그인 및 프로필 자동 등록 기능

- **관리자 대시보드**  
  → 회원 목록, 프로젝트 정산, 신고 내역, 공지사항 관리, 통계 제공

- **조회 기능 고도화**  
  → 좋아요 수, 판매 수, 달성률 기반 정렬 및 필터 기능

- **정산 시스템 구현**  
  → 월 1회 정산 자동화, 관리자 수동 정산 처리, 수수료 계산 포함

- **배포 및 운영 환경 담당**  
  → 무중단 배포, 환경별 설정 파일 분리, AWS 기반 운영 가능


<br/>
<br/>

# 2. 팀원 소개



| <img src="https://github.com/codefish-sea.png" width="100" > |<img width="100" alt="스크린샷 2025-06-02 오후 5 55 11" src="https://github.com/user-attachments/assets/0be9b10c-8dce-43be-ac78-1a48e6d9cbb8" /> | <img src="https://github.com/user-attachments/assets/de92e8b8-5ed3-4b51-8598-c4c0e089de08" width="100" > | <img src="https://github.com/heeeun-ko.png" alt="고희은" width="100"> |  <img src="https://github.com/user-attachments/assets/0a5f26dd-ed7d-4904-9365-f2a91f21bdec" width="100">|
|-------------------------------|-------------------------------|-------------------------------|-------------------------------|-------------------------------|
| **김정욱** | **박혁** | **장지현** | **고희은** | **최보경** |
| [GitHub](https://github.com/KIMB0B) | [GitHub](https://github.com/treejh) | [GitHub](https://github.com/treejh) | [GitHub](https://github.com/heeeun-ko) | [GitHub](https://github.com/chlqhrud) |

<br/>
<br/>



# 3. Key Features (주요 기능)

## 👥 회원가입 및 인증

소셜 로그인 기반의 인증과 JWT 인증 방식을 적용하여 사용자 편의성과 보안성을 모두 확보했습니다.

- **소셜 로그인 지원**  
  - 카카오, 깃허브 OAuth2 로그인
- **JWT 기반 인증 및 권한 관리**  
  - Access Token + Refresh Token 구조  
  - Refresh Token은 Redis에 저장하여 RTR 방식(Refresh Token Rotation) 적용
- **역할(Role) 기반 권한 설정**  
  - USER, ADMIN 등 역할 분리
- **마이페이지 기능**  
  - 기본 정보 수정, 이메일, 소개글, 포트폴리오 등록/수정 가능


<br/>


## 📦 프로젝트 등록 및 구매 흐름

크리에이터는 후원형 또는 구매형 프로젝트를 등록하고, 사용자는 손쉽게 후원하거나 제품을 구매할 수 있습니다.

- **후원형 프로젝트 등록**  
  - 제목, 설명, 가격, 목표금액, 기간 등 설정
- **구매형 프로젝트 등록**  
  - 다운로드, 이메일 제공 방식 선택  
  - 여러 개의 옵션 구성 가능
- **토스페이 연동 결제 기능**  
  - 후원/구매 모두 토스 결제 연동
- **정산 시스템 구현**  
  - 월 1회 자동 정산 + 관리자 수동 정산 가능  
  - 수수료, 실지급액 계산 자동화


<br/>


## ✨ 실시간 알림 기능

SSE(Server-Sent Events)를 활용한 실시간 알림 시스템을 구현하여 사용자와 관리자의 상호작용을 강화합니다.

- **후원/구매 완료 알림**  
- **리뷰 작성, 정산 요청/승인 알림**  
- **신고 처리 및 관리자 알림**  
- **팔로우한 창작자 프로젝트 등록 시 알림 발송**


<br/>


## 💬 커뮤니티 기능

사용자와 창작자 간 소통을 위한 다양한 커뮤니케이션 기능을 제공합니다.

- **리뷰 기능**  
  - 프로젝트 구매자 대상 리뷰 작성 가능  
- **Q&A 게시판**  
  - 프로젝트별 질문과 답변 기능
- **신고 기능**  
  - 프로젝트, 댓글, 리뷰에 대한 신고 접수 및 관리자 처리
- **공지사항 게시판**  
  - 운영자 공지 등록 및 사용자 열람 가능


<br/>


## 📈 관리자 대시보드

운영 관리를 위한 관리자 전용 페이지를 제공합니다.

- **회원 관리**  
  - 유저 목록, 상태(활성/정지) 관리
- **프로젝트 관리**  
  - 후원형/구매형 프로젝트 현황 조회
- **정산 관리**  
  - 모든 프로젝트 정산 요청 및 상태 처리
- **신고 처리 및 공지 등록**  
- **통계/정렬 기능**  
  - 판매 수, 좋아요 수, 목표 달성률 기반 정렬/조회 기능


<br/>


## 🔍 검색 및 추천 기능

사용자의 탐색 편의성을 높이기 위한 검색 및 정렬 기능을 제공합니다.

- **카테고리별 프로젝트 검색**
- **정렬 옵션 제공**  
  - 인기순(좋아요), 최신순, 판매순, 목표 달성률 기준
- **자동 추천 기능 (ElasticSearch 연동 예정)**



<br/>



## 🤝 팔로우 및 소셜 기능

플랫폼 내에서 창작자와 사용자가 연결될 수 있도록 소셜 기능을 제공합니다.

- **팔로우/언팔로우 기능**  
  - 창작자 활동을 지속적으로 확인 가능
- **팔로잉 기반 알림 전송**  
  - 팔로우한 창작자의 신규 프로젝트 등록 시 알림 발송


<br/>
<br/>

# 4. Tasks & Responsibilities (작업 및 역할 분담)
## 4.1 백엔드
|  |  |
|-----------------|-----------------|
| 김정욱   | 	<ul><li>비품 통계</li><ul><li>카테고리 별 분석</li><li>월별 출고량 / 입고량</li><li>품목별 사용 빈도</li></ul><li>관리페이지</li><ul><li>관리페이지 CRUD</li></ul><li>비품 추천</li><li>비품 관리</li><ul><li>반납 요청</li><li>입고 내역</li><li>비품 구매</ul></li></ul>   |
| 박혁    | <ul><li>회원</li><ul><li>최고 관리자, 매니저, 일반 회원 CRUD</li><li>회원/매니저 승인 · 반려</li><li>이메일/휴대폰 인증</li></ul><li>로그인 / 로그아웃</li><li>채팅</li><ul><li>1:1</li><li>고객센터</li><li>단체</li></ul><li>S3 이미지 CRUD</li></ul>   |
|  장지현  |	<ul><li>검색</li><ul><li>비품명 검색</li><li>회원 검색</li><li>대시보드별 검색 제한</li></ul><li>카테고리</li><ul><li>카테고리 CRUD</li><li>카테고리별 비품 수 집계</li><li>회원구분별 기능 차등</li></ul></ul>  |
| 고희은    | <ul><li>소셜 로그인</li><ul><li>KAKAO 로그인</li><li>GITHUB 로그인</li></ul><li>사용자 정보 (마이페이지)</li><ul><li>프로필(이름, 이미지, 소개글, 포트폴리오 주소) 수정</li><li>이메일 인증 및 변경</li><li>계좌 정보</li></ul><li>팔로우</li><ul><li>팔로우/팔로잉</li></ul><li>관리자 - 사용자 정보</li><ul><li>기본 조회</li><li>활동 조회</li><li>사용자 활성/정지</li>
| 최보경    | <ul><li>부서</li><ul><li>부서 CRUD</li></ul><li>알림</li><ul><li>알림 CRUD</li><li>비품 관련</li><li>재고 관련</li><li>채팅</li><li>승인/거절</li><li>스케줄러</li></ul></ul>    |



<br/>
<br/>

# 5. Technology Stack (기술 스택)

## 🛠️ Tech Stack

| 분류 | 기술 스택 |
|------|-----------|
| **Backend** | ![Spring Boot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white) |
| **DevOps / Infra** | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![Amazon EC2](https://img.shields.io/badge/Amazon%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white) ![Amazon S3](https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white) ![NGINX](https://img.shields.io/badge/NGINX-009639?style=for-the-badge&logo=nginx&logoColor=white) ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white) |
| **Collaboration** | ![Git](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white) ![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white) |
| **Documentation/Test** | ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white) |


<br/>
<br/>
    
# 6. 프로젝트 개요



## 6-1 api 명세서(스웨거)
    
    
    
## 6-2 ERD
<img width="5340" height="2802" alt="옛다(YETDA)" src="https://github.com/user-attachments/assets/9db09c75-86e5-4ffc-b2e3-ef1a598823f0" />

<br/>
<br/>

# 7. Project Structure (프로젝트 구조) 


## 📂 7-1 백엔드 프로젝트 구조 
```
📦main
 ┣ 📂java
 ┃ ┗ 📂com
 ┃ ┃ ┗ 📂funding
 ┃ ┃ ┃ ┗ 📂backend
 ┃ ┃ ┃ ┃ ┣ 📂domain
 ┃ ┃ ┃ ┃ ┃ ┣ 📂admin
 ┃ ┃ ┃ ┃ ┃ ┣ 📂alarm
 ┃ ┃ ┃ ┃ ┃ ┣ 📂donation
 ┃ ┃ ┃ ┃ ┃ ┣ 📂follow
 ┃ ┃ ┃ ┃ ┃ ┣ 📂like
 ┃ ┃ ┃ ┃ ┃ ┣ 📂mainCategory
 ┃ ┃ ┃ ┃ ┃ ┣ 📂notice
 ┃ ┃ ┃ ┃ ┃ ┣ 📂order
 ┃ ┃ ┃ ┃ ┃ ┣ 📂orderOption
 ┃ ┃ ┃ ┃ ┃ ┣ 📂pricingPlan
 ┃ ┃ ┃ ┃ ┃ ┣ 📂project
 ┃ ┃ ┃ ┃ ┃ ┣ 📂projectImage
 ┃ ┃ ┃ ┃ ┃ ┣ 📂projectSubCategory
 ┃ ┃ ┃ ┃ ┃ ┣ 📂purchase
 ┃ ┃ ┃ ┃ ┃ ┣ 📂purchaseCategory
 ┃ ┃ ┃ ┃ ┃ ┣ 📂purchaseOption
 ┃ ┃ ┃ ┃ ┃ ┣ 📂qna
 ┃ ┃ ┃ ┃ ┃ ┣ 📂report
 ┃ ┃ ┃ ┃ ┃ ┣ 📂review
 ┃ ┃ ┃ ┃ ┃ ┣ 📂role
 ┃ ┃ ┃ ┃ ┃ ┣ 📂settlement
 ┃ ┃ ┃ ┃ ┃ ┣ 📂subjectCategory
 ┃ ┃ ┃ ┃ ┃ ┗ 📂user
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂email
 ┃ ┃ ┃ ┃ ┣ 📂enums
 ┃ ┃ ┃ ┃ ┣ 📂global
 ┃ ┃ ┃ ┃ ┃ ┣ 📂auditable
 ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┣ 📂toss
 ┃ ┃ ┃ ┃ ┃ ┣ 📂utils
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂s3
 ┃ ┃ ┃ ┃ ┃ ┗ 📂validator
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂annotaion
 ┃ ┃ ┃ ┃ ┣ 📂security
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┣ 📂filter
 ┃ ┃ ┃ ┃ ┃ ┣ 📂jwt
 ┃ ┃ ┃ ┃ ┃ ┗ 📂oauth
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂handler
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂model
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂provider
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂resolver
 ┃ ┃ ┃ ┃ ┗ 📜BackendApplication.java
 ┗ 📂resources
 ┃ ┣ 📂static
 ┃ ┣ 📂templates
 ┃ ┣ 📜application-docker.yml
 ┃ ┣ 📜application-local.yml
 ┃ ┣ 📜application-secret.yml
 ┃ ┣ 📜application-test.yml
 ┃ ┗ 📜application.yml
```


<br/>
<br/>

# 8. Development Workflow (개발 워크플로우)

## 8-1 브랜치 전략 (Branch Strategy)
브랜치 전략은 Git Flow를 기반으로 하며, 다음과 같은 브랜치를 사용합니다.
- **Main Branch**
     - `main`  
       - 배포 가능한 상태의 코드를 유지합니다.
       - 모든 배포는 이 브랜치에서 이루어집니다.


- **백엔드 기능 개발**
    - `back/[type]/이슈번호-브랜치이름 `
      - **백엔드 API, 서비스 로직, DB 처리 등 백엔드 중심 작업 시 사용합니다.**
      - 팀원 각자의 기능 개발용 브랜치입니다.
      - 모든 기능 개발은 이 브랜치에서 진행되며, 완료 시 병합 요청합니다.

## 8-2 커밋 컨벤션
```
[ back or front / type ]  [ 이슈 번호 ] : 커밋 내용
```

- 예시
    - [front / feat]  139 : 로그인 페이지 UI 구현
 
## 8-3 PR 컨벤션
```
[ back or front / type ]  [ 이슈 번호 ]  :  PR 내용
```
- 예시 
    - [ back / feat ]  3  : 회원가입 API 구현

## 8-4 Issue 컨벤션

```
[ back or front / type ]  :  이슈 내용
```
- 작업 목적 + 대상 + 내용을 포함해 작성합니다.
- 예시
    - [ front / refact ] : 메인페이지 인기 게시글, 블로그 페이지 팔로우, 팔로우 페이지 블로그 연동
    - [ front / feat ] : 댓글버튼 숨기기
    - [ feat ] : 이메일 인증 구현 

## 8-5 태그 타입 종류 및 사용 예시

| 태그     | 의미 및 사용 시점 |
|----------|-------------------|
| `feat`   | **새로운 기능 추가** <br> 사용자에게 보이는 기능/화면 등 새로운 기능 개발 시 사용 |
| `fix`    | **버그 수정** <br> 의도와 다르게 동작하는 코드 수정, 예외/오류 처리 등 |
| `refact` | **리팩토링** <br> 코드 구조 개선, 성능 향상 등 기능 변화 없이 내부 개선 시 |
| `docs`   | **문서 변경** <br> README, 주석, API 문서, PR/Issue 템플릿 등 코드 외 문서 수정 시 |
| `style`  | **코드 스타일 변경** <br> 세미콜론, 들여쓰기, 줄 바꿈 등 로직 변경 없이 포맷 수정 시 |
| `test`   | **테스트 코드 추가/변경** <br> JUnit, Mock 객체 등 테스트 관련 작업 시 |
| `chore`  | **환경설정/기타 작업** <br> 빌드/패키지 관련 작업, 테스트 외 설정 수정 등 |
| `ci`     | **CI/CD 설정 변경** <br> GitHub Actions, Jenkins 등 자동화 관련 설정 수정 시 |
| `build`  | **빌드 시스템/의존성 변경** <br> Gradle, npm, Docker 설정 등 수정 시 |
| `perf`   | **성능 개선** <br> 속도 향상, 메모리 최적화 등 성능 관련 작업 시 |


<br/>

