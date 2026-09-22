# 🏢 Fill the Blank

### 서울시 공공데이터를 활용한 유휴공간 용도 추천 서비스

서울시 공공데이터를 활용하여 행정동별 **인구·생활인구·문화시설·복지시설·상권 데이터**를 분석하고, 유휴공간의 위치와 면적을 기반으로 **적합한 활용 용도 TOP 3**를 추천하는 웹 서비스입니다.

---

## 📌 프로젝트 개요

| 항목    | 내용                         |
| ----- | -------------------------- |
| 개발 기간 | 2026.08.21 ~     |
| 개발 인원 | 1명                         |
| 플랫폼   | Web                        |
| 주요 기능 | 유휴공간 검색 · 지도 조회 · 활용 용도 추천 |
| 배포    | Docker · Render            |

---

## 🛠️ Tech Stack

**Backend**
`Java 17` `Spring Boot 4.1.1` `JSP` `JdbcTemplate`

**Data**
`Python` `Hadoop MapReduce` `SQL`

**Database**
`Oracle 21c` `PostgreSQL`

**Frontend**
`HTML/CSS` `JavaScript` `Kakao Map API`

**Deployment**
`Docker` `Render`

---

## ✨ 주요 기능

### 1. 유휴공간 조회

* 서울시 유휴공간 목록 및 지도 조회
* 시설명, 주소, 면적 등 상세정보 제공

### 2. 유휴공간 검색

* 행정동
* 시설명
* 최소 면적

조건을 활용한 공간 검색

### 3. 활용 용도 추천

공간이 위치한 행정동의 데이터를 분석하여 **추천 용도 TOP 3** 제공

* 🧑‍🎨 청년 문화공간
* 🚀 창업지원 공간
* 🤝 복지공간
* 🏘️ 커뮤니티 공간

추천 점수와 추천 이유를 함께 제공합니다.

---

## 📊 데이터 처리
활용 데이터
- 유휴공간 데이터
- 인구 데이터
- 생활인구 데이터
- 문화시설 데이터
- 복지시설 데이터
- 상권 데이터

서로 다른 공공데이터를 **행정동코드 기준으로 통합**하고 Hadoop MapReduce를 활용하여 행정동별 분석 데이터를 생성했습니다.

```text
공공데이터 수집
      ↓
데이터 전처리
      ↓
행정동코드 기준 통합
      ↓
Hadoop MapReduce
      ↓
행정동별 데이터 집계
      ↓
추천 점수 계산
      ↓
활용 용도 TOP 3
```

---

## 🔥 주요 트러블슈팅

**① 공공데이터 지역 기준 불일치**
법정동·행정동 등 서로 다른 지역 기준을 **행정동코드로 통일**하여 데이터 통합

**② 대용량 데이터 처리**
생활인구 등 대용량 데이터를 Hadoop MapReduce로 행정동별 집계

**③ 개발/배포 DB 환경 차이**
Oracle → PostgreSQL 전환 과정에서 `JdbcTemplate` 기반 DB 접근 구조를 유지하고 PostgreSQL에 맞게 SQL 및 테이블 구조 수정

---

## 🏗️ Architecture

```text
공공데이터
    ↓
Hadoop MapReduce
    ↓
PostgreSQL
    ↓
Spring Boot
    ↓
REST API
    ↓
JSP + JavaScript
    ↓
사용자
```

---

## 🔗 Links

* **서비스** : https://filltheblank.onrender.com/idle-space-list
* **GitHub** : https://github.com/jungnoeun/fillTheBlank
* **시연 영상** : https://youtu.be/7NgwCzTVu7w

---

## 💡 프로젝트 회고

공공데이터의 **수집 → 전처리 → 대용량 데이터 처리 → DB 구축 → 추천 로직 구현 → 웹 서비스 개발 → 배포**까지 전체 개발 과정을 경험함.

서로 다른 공공데이터를 행정동 기준으로 통합하고, Hadoop MapReduce를 활용해 분석 데이터를 생성하는 과정을 통해 **데이터 처리와 백엔드 개발을 함께 경험**함.
