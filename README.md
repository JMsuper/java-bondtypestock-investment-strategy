## 목차
- [📋 프로젝트 개요](#📋-프로젝트-개요)
- [🛠 기술 스택](#🛠-기술-스택)
- [📁 폴더 구조](#📁-폴더-구조)
- [⚙️ 핵심 기능 구현](#⚙️-핵심-기능-구현)
- [🖥️ 주요 화면](#🖥️-주요-화면)
- [🏗 시스템 아키텍처](#🏗-시스템-아키텍처)
- [📊 데이터베이스 설계](#📊-데이터베이스-설계)
- [🔍 트러블 슈팅](#🔍-트러블-슈팅)
- [📝 회고](#📝-회고)

## 📋 프로젝트 개요
<div align="center">
  <img src="./images/logo.PNG" alt="snowball stock logo" width="400px" />
  
  # Snowball Stock
  > 가치투자를 위한 기업 정보 제공 서비스 (개발 기간: 2.5개월)
</div>

### 🎯 핵심 기능
- 기업 재무정보 및 주가 정보 제공
- 관심 종목 등록 및 알림 서비스
- 기업 공시 정보 알림
- ROE 기반 기업 스크리닝

### 💡 프로젝트 특징
- 1인 풀스택 개발 (프론트엔드/백엔드/인프라)
- '눈덩이 주식투자' 도서 기반 가치투자 시스템 구현
- 자동화된 데이터 수집 및 모니터링 시스템

## 🛠 기술 스택

| 분류 | 기술 스택 | 설명 |
|------|-----------|------|
| **Frontend** | Next.js 13.4.3 | React 기반 SSR 프레임워크 |
| | TypeScript 5.0.4 | 정적 타입 지원 |
| | MUI 5.x | Material Design UI 라이브러리 |
| | React 18.2.0 | UI 라이브러리 |
| | Recoil 0.7.7 | 상태 관리 라이브러리 |
| | Vercel | 웹 애플리케이션 배포 플랫폼 |
| **Backend** | Java 17 | LTS 버전 |
| | Spring Boot 3.2.2 | 웹 애플리케이션 프레임워크 |
| | Spring Security 3.2.2 | 인증/인가 처리 |
| | JPA/Hibernate | ORM 프레임워크 |
| | Redis | 기업공시 데이터 인메모리 캐시 |
| | Actuator 3.2.3 | 애플리케이션 모니터링 |
| **Infrastructure** | AWS EC2 t3.small | 애플리케이션 서버 |
| | AWS RDS MySQL 8.0 | MySQL 데이터베이스 |
| | AWS VPC | 서브넷 분리된 네트워크 구성 |
| | AWS Route 53 | DNS 서비스 및 도메인 관리 |
| | Docker 24.0.7 | 컨테이너 기반 배포 |
| | Nginx 1.24.0 | API 캐싱, 리버스 프록시 |
| | Prometheus & Grafana | 시스템 모니터링, Slack 알림 |

## 📁 폴더 구조

### IaC (Infrastructure as Code)
```
IaC/
├─ private/ # 프라이빗 서브넷 관련 설정
│ ├─ data/ # 데이터 저장소
│ ├─ logs/ # 로그 파일
│ └─ node_exporter/ # host os 모니터링 에이전트
└─ public/ # 퍼블릭 서브넷 관련 설정
  ├─ logs/ # API 서버 및 Nginx 로그
  ├─ nginx/ # Nginx 설정 및 캐시
  └─ prometheus_grafana/# 모니터링 시스템
```

### Backend Application
```
src/
├─ main/
│ ├─ java/com/finance/adam/
│ │ ├─ auth/ # 인증/인가 관련
│ │ ├─ config/ # 애플리케이션 설정
│ │ ├─ controller/ # API 엔드포인트
│ │ ├─ dto/ # 데이터 전송 객체
│ │ ├─ exception/ # 예외 처리
│ │ ├─ openapi/ # 외부 API 연동
│ │ │ ├─ dart/ # 전자공시 API
│ │ │ ├─ krx/ # 한국거래소 API
│ │ │ └─ publicdataportal/ # 공공데이터 API
│ │ ├─ repository/ # 데이터 접근 계층
│ │ ├─ scheduler/ # 배치 작업
│ │ ├─ service/ # 비즈니스 로직
│ │ ├─ util/ # 유틸리티
│ │ └─ validation/ # 입력값 검증
│ └─ resources/ # 정적 리소스
└─ test/ # 테스트 코드
```

## ⚙️ 핵심 기능 구현
### 1. 주가 정보 수집 및 제공
- **한국거래소(KRX) 웹사이트 크롤링**
  - 주가 데이터 10분 간격 수집
  - CSV 파일 파싱 및 데이터 가공
  - 실시간 시세 정보 제공
  
- **공공데이터 포털 API 연동**
  - 일 1회 상장/상폐 정보 자동 갱신
  - 기업 기본 정보 동기화
  - 종목 마스터 데이터 관리

### 2. 기업 재무정보 분석
- **재무제표 데이터 수집**
  - 금융감독원(DART) 전자공시 API 활용
  - 분기/연간 재무제표 자동 수집
  - 기업별 재무정보 정기 갱신

- **투자 지표 분석**
  - ROE 기반 기업 스크리닝
  - 맞춤형 종목 필터링

### 3. 알림 서비스
- **맞춤형 알림 설정**
  - 목표주가 도달 알림
  - 정기 주가 알림 (시간 설정)
  - 관심 기업 공시 알림 (공시 유형 선택)

- **알림 이력 관리**
  - 알림 로그 페이지 제공
  - 알림 발생 시간 및 상세 내용 조회

## 🖥️ 주요 화면
### 주식 상장사 검색
<div align="center">
  <img src="./images/주식_상장사_검색.PNG" width="80%" />
  <p align="center">종목명 또는 종목코드로 상장사를 검색하고 실시간 주가 정보를 확인할 수 있습니다.</p>
</div>

<div align="center">
  <img src="./images/주식_상장사_검색2.PNG" width="80%" />
  <p align="center">검색된 기업의 상세 정보와 재무제표, 주요 투자 지표를 분석할 수 있습니다.</p>
</div>

### 종목 스크리닝
<div align="center">
  <img src="./images/종목_스크리닝.PNG" width="80%" />
  <p align="center">ROE 기반으로 기업을 필터링하고 업종별로 기업을 비교 분석할 수 있습니다.</p>
</div>

<div align="center">
  <img src="./images/종목_스크리닝2.PNG" width="80%" />
  <p align="center">스크리닝된 기업들의 상세 재무지표를 확인하고 투자 가치를 분석할 수 있습니다.</p>
</div>

### 알림 서비스
<div align="center">
  <img src="./images/주가_알리미.PNG" width="80%" />
  <p align="center">원하는 종목의 목표가를 설정하고 도달 시 실시간으로 알림을 받을 수 있습니다.</p>
</div>

<div align="center">
  <img src="./images/신규_공시_구독.PNG" width="80%" />
  <p align="center">관심 있는 기업의 공시 정보를 유형별로 선택하여 구독할 수 있습니다.</p>
</div>

<div align="center">
  <img src="./images/알림_메일_이미지.PNG" width="80%" />
  <p align="center">설정한 알림은 이메일로도 전달되며, 상세 정보 링크를 통해 바로 확인할 수 있습니다.</p>
</div>

### 관심 종목 및 알림 이력
<div align="center">
  <img src="./images/관심종목.PNG" width="80%" />
  <p align="center">자주 확인하는 종목을 관심 종목으로 등록하고 실시간으로 요약 정보를 모니터링할 수 있습니다.</p>
</div>

<div align="center">
  <img src="./images/알림_기록_조회.PNG" width="80%" />
  <p align="center">발생한 알림의 이력을 조회하고 알림 설정별 발송 현황을 확인할 수 있습니다.</p>
</div>

## 🏗 시스템 아키텍처
### 시스템 구성도
![AWS Architecture](./images/AWS_Arch.png)

### 아키텍처 설계 의도

#### 🔒 보안 강화
- **네트워크 분리**
  - Public/Private 서브넷 분리로 보안 계층화
  - Private 서브넷의 RDB, Redis, 스케줄링 서버 보호
- **NAT 게이트웨이**
  - Private 서브넷의 안전한 외부 통신 지원
  - 내부 리소스 보호와 동시에 필요한 외부 통신 허용
- **안전한 SSH 접속**
  - AWS Instance Connect Endpoint 활용
  - Private 서브넷 EC2 인스턴스에 대한 보안적인 SSH 접속
  - Bastion 호스트 없이 안전한 원격 접속 구현

#### 🔄 가용성 확보
- **API 서버 이중화**
  - Active-Active 구성의 다중 서버 운영
  - IP Hash 기반 로드밸런싱으로 세션 일관성 보장
  - 동일 사용자의 요청을 동일 서버로 라우팅
- **모니터링 체계**
  - Prometheus & Grafana를 통한 실시간 시스템 모니터링
  - 장애 상황 조기 감지 및 대응

#### ⚡ 개발 효율성
- **프론트엔드 배포**
  - Vercel을 활용한 프론트엔드 배포 자동화
  - 백엔드 개발에 리소스 집중
- **컨테이너 관리**
  - GitHub Container Registry 활용
  - 도커 이미지 관리 비용 절감

## 📊 데이터베이스 설계
### ERD
<div align="center">
  <img src="./images/erd.png" width="80%" />
</div>

## 🔍 트러블 슈팅
작성중

## 📝 회고
작성중