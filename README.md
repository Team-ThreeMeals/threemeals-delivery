<p align="center">
 <img width="100px" src="https://res.cloudinary.com/anuraghazra/image/upload/v1594908242/logo_ccswme.svg" align="center" alt="GitHub Readme Stats" />
    <h2 align="center">삼시세끼민족</h2>
</p>
<br/>

---

## 목차

- [📜 소개](#소개)
- [👨‍👧‍👦 팀원](#팀원)
- [⚙️ 개발 환경](#개발-환경)
- [🎲 기능 목록](#기능-목록)
- [🧩 핵심 기능](#핵심-기능)
- [📕 ERD DIAGRAM](#erd-diagram)
- [📄 API 명세서](#API-명세서)
- [✍ Trouble Shooting](#trouble-shooting)

---

## 📜 소개

**삼시세끼민족 프로젝트**는 배달의 민족을 참고하여 기본적인 CRUD와 Redis, 소셜 로그인 기능을 학습하고 구현한 프로젝트입니다. 주요 기능은 아래와 같습니다:

- **User**: 유저 회원가입, 로그인
- **Menu**: CRUD (Create, Read, Update, Delete)
- **Store**: CRUD
- **Cart**: CRUD
- **Order**: CRUD
- **Review** : CRD
- **Store Like**: 좋아요 활성화/비활성화

**에러 처리:**

- 잘못된 데이터 입력(예: ID 누락) 또는 연산 오류에 대해 사용자에게 명확한 에러 메시지를 제공합니다.

**로그인 구현:**

- 토큰을 사용하여 ID와 비밀번호 기반 로그인을 구현했습니다.
- Naver 소셜 로그인을 구현했습니다.

---

## 👨‍👧‍👦 팀원
<table>
  <tr>
    <td align="center">
      <b><a href="https://github.com/duol9">이하영</a></b><br>
      <a href="https://github.com/duol9">
        <img src="https://avatars.githubusercontent.com/u/90500100?v=4" width="100px" />
      </a><br>
      <b>팀장</b>
    </td>
    <td align="center">
      <b><a href="https://github.com/chk223">김창현</a></b><br>
      <a href="https://github.com/chk223">
        <img src="https://avatars.githubusercontent.com/u/104356399?v=4" width="100px" />
      </a><br>
      <b>팀원</b>
    </td>
    <td align="center">
      <b><a href="https://github.com/freedrawing">강성욱</a></b><br>
      <a href="https://github.com/freedrawing">
        <img src="https://avatars.githubusercontent.com/u/43941383?v=4" width="100px" />
      </a><br>
      <b>팀원</b>
    </td>
    <td align="center">
      <b><a href="https://github.com/kyung412820">이경훈</a></b><br>
      <a href="https://github.com/kyung412820">
        <img src="https://avatars.githubusercontent.com/u/71320521?v=4" width="100px" />
      </a><br>
      <b>팀원</b>
    </td>
    <td align="center">
      <b><a href="https://github.com/dllll2">이진영</a></b><br>
      <a href="https://github.com/dllll2">
        <img src="https://avatars.githubusercontent.com/u/105922173?v=4" width="100px" />
      </a><br>
      <b>팀원</b>
    </td>
  </tr>
</table>

---

## ⚙️ 개발 환경

- **Framework**: Spring Framework, Spring Boot
- **Database**: MySQL, Redis
- **ORM**: Spring Data JPA
- **View**: JSP
- **Language**: Java

---

## 🎲 기능 목록

- DB, 클라이언트, 서버 간 통신 구현
- JPA와 Redis를 이용하여 데이터베이스와 통신
- Servlet을 통한 서버 데이터 처리 및 응답

---

## 🧩 핵심 기능

### 1. 사용자(User)

- **회원가입 및 로그인**:
  - 사용자는 ID와 비밀번호를 이용하여 회원가입 및 로그인을 수행.
- **정보 수정 및 삭제**:
  - 자신의 계정 정보를 수정하거나 삭제 가능.
- **주문 내역 조회**:
  - 특정 사용자의 주문 내역을 확인.

### 2. 가게(Store)

- **가게 등록 및 관리**:
  - 가게 주인만 접근 가능한 가게 등록, 수정, 삭제 기능 제공.
- **가게 검색 및 조회**:
  - 가게 이름으로 검색하여 목록을 페이징 처리.
  - 특정 가게의 상세 정보 및 메뉴 목록 확인.

### 3. 가게 좋아요(Store Like)

- **좋아요 추가**
  - 가게 좋아요(찜) 선택 제공

### 4. 메뉴(Menu)

- **메뉴 관리**:
  - 메뉴 추가, 수정, 삭제 기능.
  - 메뉴 옵션 추가 및 관리.
- **메뉴 상세 조회**:
  - 특정 메뉴와 해당 메뉴 옵션 정보를 조회.

### 5. 장바구니(Cart)

- **장바구니 기능**:
  - 메뉴 추가, 수정, 삭제 및 초기화 가능.
- **주문 생성**:
  - 장바구니 데이터를 기반으로 주문 생성.

### 6. 리뷰(Review)

- **리뷰 작성 및 관리**:
  - 사용자는 리뷰 작성 및 삭제 가능.
  - 가게 주인은 리뷰 댓글 추가 가능.
- **리뷰 조회**:
  - 특정 가게의 모든 리뷰를 페이징 처리하여 조회.

### 7. 주문(Order)

- **주문 관리**:
  - 사용자의 모든 주문 목록 조회.
  - 특정 주문의 상세 정보 확인.
  - 주문 상태 업데이트 및 취소 가능.

---
## 📕 ERD DIAGRAM
<img src="https://github.com/user-attachments/assets/524d4501-4259-4a4e-8b29-8b41bd2720b4" width="500" height="1000">

## 📄API 명세서
[API 명세서 상세보기 ](https://www.notion.so/API-1751c81f481d80c7804adf8d172a0278?pvs=21)

## ✍ Trouble Shooting

### 주요 문제 및 해결 방법:

- 프로젝트 개발 과정에서 발생한 주요 이슈와 해결 방법은 아래 블로그에서 확인할 수 있습니다:
  - [Trouble Shooting 상세 보기](https://kyunghun0515.tistory.com/109)

---

