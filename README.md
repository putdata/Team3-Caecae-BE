# Team: 난대학시절현대차를전공했단사실👨‍🎓

현대자동차그룹 소프티어 부트캠프 4기 Team 3️⃣ 

## Project Name: 캐스퍼가캐리해(Caecae)

> **캐스퍼 일렉트릭 출시 이벤트 페이지 :  Caecae**

🔗 http://www.caecae.kro.kr/

### 🔎 이벤트 소개 : 나를 찾아봐 (숨은 캐스퍼 찾기 게임)
- 매일 오후 3시15분 오픈되는 선착순 이벤트입니다.
- 캐스퍼 일렉트릭의 내부에 숨겨진 픽셀/뱃지 디자인을 누구보다 빠르게 찾아보세요!
- 선착순 인원 내에 선정되면, 전화번호를 입력하여 상품을 획득할 수 있습니다.
<img width="1704" alt="image" src="https://github.com/user-attachments/assets/c6d1b6ab-703c-4f1d-816b-156cd2a5b3a2">

### 🚗 이벤트 소개 : 전력으로 315km (레이싱 게임)
- 일주일간 플레이할 수 있는 레이싱 게임입니다.
- 315km에 근접하게 멈추면 당첨 확률이 올라갑니다.
- 친구에게 공유하기를 통해 경쟁심을 유발하세요!
- 기록이 마음에 든다면, 응모하기 버튼을 통해 전화번호를 입력하여 기록을 등록할 수 있습니다. 
- 최고점수만 기록되며, 커스텀 옵션을 선택하면 당첨 확률이 올라갑니다.

<img width="1711" alt="image" src="https://github.com/user-attachments/assets/30d4ff09-cad8-4b87-adc8-10b3d8714917">


## ⭐ 팀원 소개

| 서창교                      | 조민환                      | 오익준                      | 박진우                      |
| --------------------------- | --------------------------- | --------------------------- | --------------------------- |
| <img src="https://avatars.githubusercontent.com/u/52204038?v=4" width="200" height="200"/> | <img src="https://avatars.githubusercontent.com/u/102406238?v=4" width="200" height="200"/> | <img src="https://avatars.githubusercontent.com/u/39623729?v=4" width="200" height="200"/> | <img src="https://avatars.githubusercontent.com/u/77064618?v=4" width="200" height="200"/> |
| FE                          | FE                          | BE                          | BE                          |
| [@dunkkk](https://github.com/dunkkkk)       | [@minani-0621](https://github.com/minani-0621)       | [@putdata](https://github.com/putdata)                    | [@j2noo](https://github.com/j2noo)                |

<br/>

## 📕 그라운드 룰

### 👨‍💻 개발 및 협업방식

- 모든 에픽, 스토리, 테스크, 작업, 브랜치는 **JIRA**를 통해 관리
- 매주 스프린트 시작 시 백로그 작성
- 현재 개발 피처에서 합의가 필요하면 바로 대화하기
- git flow 전략을 사용하여 브랜치 관리하기
- 코드리뷰 열심히 하기

### 📅 Daily Scrum & Daily Review

- 매일 아침 10시에 스크럼 작성하기
- 매일 퇴근하기 전 회고 작성하기
- 회의 때 적극적으로 참여하기 (의견 많이 내기)

### ❤️‍🔥 팀 문화

- 지각할 경우, 과자 한 박스 사오기
- 웃으면서 화내기/짜증내기
- 쉬고싶을 땐 눈치보지 않고 쉬기
- 아니 / 근데 / 진짜 로 대화 시작하지 않기

### 🍚 점심메뉴

- 아침 10시 스크럼 전, 점심메뉴 먼저 정하기
- 일주일에 한 번은 참맛식당 가기


## 📁 아카이빙
- [스프린트 및 회고](https://drive.google.com/drive/folders/1s0Vez7YbkkyGPteh-5q0r0Y_vre_QfZq?usp=sharing)


<br/>

## **💻Tech - Backend 개발 목표**

### 1. Redis 도입을 통한 대용량 트래픽 및 동시성 처리

- 메모리에서 동작하기 때문에 빠른 처리 속도 기대
- 싱글 스레드 동작 방식으로 인해 동시성 제어 가능

### 2. 부하테스트를 통한 병목 지점 파악 및 성능개선

- 서버의 부하 테스트를 할 수 있는 툴을 사용하여 대용량 트래픽 유발
- 모니터링 툴을 사용하여 자원 사용량 파악 및 성능 개선

### 3. 적극적인 테스트 코드 작성

- 주요 메서드들에 대한 단위테스트 및 통합테스트 작성
- 목표 테스트 커버리지 50%

<br/>

## **📘Backend 기술 스택**

### Application

- Spring boot 3.3.2
- Spring Data Jpa
- Java 17
- MySQL 8.0
- Redis

### Infra

- AWS EC2
- AWS S3
- AWS Parameter store
- Github-actions
- Docker

### Monitoring

- Node-exporter
- Grafana
- Prometheus
- Spring Actuator

### Test Tool

- AssertJ
- Mockito
- nGrinder

<br/>

## 🖥️Server Architecture

<img width="1071" alt="image" src="https://github.com/user-attachments/assets/425b7349-81a2-4b8b-b031-6f343375faac">

<br/>

## 📄ERD

![image](https://github.com/user-attachments/assets/8053e05d-6642-4981-b55f-c0e15dffc051)


<br/>

## **🔖Naming Rules**

- **Packages**
    - 항상 소문자로 생성하기
- **Classes**
    - 명사여야 한다.
    - 복합 단어의 경우 각 단어의 첫글자는 대문자.
    - 완전한 단어를 사용하고, 두 문자어와 약어는 피한다.
- **Interfaces**
    - 인터페이스 이름도 클래스 이름과 같은 대문자 규칙을 적용한다.
- **Methods**
    - 동사여야 한다.
    - 복합 단어의 경우 첫 단어는 소문자로 시작한다.
- **Constants**
    - 클래스 상수로 선언된 변수들과 상수들의 이름은 모두 대문자로 쓰고 각 단어는 언더바 ("_")로 분리한다. -**Variables**
    - 변수 이름의 첫번째 문자는 소문자여야 한다.
    - 언더바 또는 달러 표시 문자로 시작하는 것이 허용 되기는 하지만, 사용하지 말자.
    - 짧지만 의미있게 짓는다.
    - 변수의 사용 의도를 알 수 있도록 의미적으로 짓는다.
    - 한문자로만 이루어진 변수는 암시적으로만 사용하고 버릴 변수를 제외하고는 피한다.
    - 임시 변수의 이름은 integer는 i,j,k,m,n 을 사용하고 character는 c,d,e를 사용한다.
- **ETC**
    - DB 테이블: **lower_snake_case**
    - ENUM, 상수: **Upper_snake_case**
    - 컬렉션(Collection): **복수형**을 사용하거나 **컬렉션을 명시한다**. (Ex. userList, users, userMap)
    - LocalDateTime: 접미사에 **Date**를 붙인다.

<br/>

## **🗂️ Commit Convention**

`git commit -m "feat : 전체 등수 조회 api 추가 (CC-83)"`

- `feat` : 새로운 기능 추가
- `fix` : 버그 수정
- `chore` : 빌드 업무, 패키지 매니저, 라이브러리, dependencies 설정
- `docs` : 문서 수정 - *README.md, .github, ..etc*
- `design` : 사용자 UI 디자인 변경 - *CSS*
- `style` : 기능 수정 없는 코드 스타일 변경
- `refactor` : 코드 리팩터링
- `test` : 테스트 코드, 리펙토링 테스트 코드 추가
- `ci` : ci 설정 파일 수정
- `perf` : 성능 개선
- `rename` : 파일 혹은 폴더명 변경

<br/>

## **🐬 Git Flow**

브랜치 네이밍 : `CC-83`

- `main` : 출시 가능한 프로덕션 코드의 브랜치
- `develop` : 개발 내용 통합 브랜치
- `feature` : 기능을 개발하는 브랜치
- `hotfix` : 출시 버전에서 발생한 버그를 수정하는 브랜치
    
    

---

## 📂 폴더 구조

```
ai.softeer.caecae
├── domain(racingGame..)
│   ├── api(controller)
│   ├── service
│   ├── domain
│   │   ├── dto
│   │   │    ├── request
│   │   │    └── response
│   │   ├── entity
│   └── repository
└── global
     ├── api
     ├── config
     ├── dto
     ├── entity
     ├── enums
     ├── exception
     └── utils
```
