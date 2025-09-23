# [1.8.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.7.0...v1.8.0) (2025-09-23)


### Bug Fixes

* max-size 100MB로 수정 ([faa2466](https://github.com/woowacourse-teams/2025-Fit-toring/commit/faa24668c207b2513d3312839345585b3eed893b))
* 멘토링 가져오기 api 명세 변경에 따른 반환값 타입 추가 [#664](https://github.com/woowacourse-teams/2025-Fit-toring/issues/664) ([8b618d6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8b618d63612c8000d939a95a8c4f721d6ffe87da))
* 에러 처리가 토글되던 오류 수정 [#663](https://github.com/woowacourse-teams/2025-Fit-toring/issues/663) ([219f9b9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/219f9b99a98f39761a3c3bcda7bdba35ee8dbfac))
* 이미지가 비율을 유지할 수 있도록 속성 추가 [#664](https://github.com/woowacourse-teams/2025-Fit-toring/issues/664) ([00c7b71](https://github.com/woowacourse-teams/2025-Fit-toring/commit/00c7b71320e3ff8f5061fe10d1575079f091d719))
* 자격사항 아이디 타입 변경 [#663](https://github.com/woowacourse-teams/2025-Fit-toring/issues/663) ([a6ec7cc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a6ec7cc92cc964acbc03a8ccbfe992dcd8b72b10))


### Features

* textarea 5,000자 이상 입력시 에러 [#663](https://github.com/woowacourse-teams/2025-Fit-toring/issues/663) ([d74c512](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d74c51208972dab60feff03e5f2b5b7a78566999))
* TYPHOGRAPHY 세미 볼드체 추가 [#664](https://github.com/woowacourse-teams/2025-Fit-toring/issues/664) ([691372a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/691372a383bd4169e77913e7add5e9bed2dabeed))
* 리뷰 완료 후 내가 작성한 리뷰를 볼 수 있도록 버튼 변경 [#664](https://github.com/woowacourse-teams/2025-Fit-toring/issues/664) ([c6d1be5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c6d1be56d26bc50d01145d6872bda9bb7a6bb354))
* 멘토링 개설 시 textarea 에러 추가 [#663](https://github.com/woowacourse-teams/2025-Fit-toring/issues/663) ([5612fe1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5612fe10baf21de231a8f646479d8d21f2129d3c))
* 멘토링 수정 시 textarea 에러 추가 [#663](https://github.com/woowacourse-teams/2025-Fit-toring/issues/663) ([48da8d7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/48da8d7fe4e143b6fd31e35cd17698ea45d391ab))
* 멘토링 예약 시 textarea 에러 추가 [#663](https://github.com/woowacourse-teams/2025-Fit-toring/issues/663) ([cf80acd](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cf80acd3d176e68610ba2f593e30ac98a3ee27b9))
* 상태 설명 응집하여 간단하게 변경 [#664](https://github.com/woowacourse-teams/2025-Fit-toring/issues/664) ([c20d81c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c20d81cc6e4eb87dd3ce76f5e838aeb9eca33225))
* 스텝퍼 UI 변경 [#664](https://github.com/woowacourse-teams/2025-Fit-toring/issues/664) ([c3633bb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c3633bb338217e9186a6ea90aa992d04864ed79d))

# [1.7.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.6.0...v1.7.0) (2025-09-21)


### Features

* Docker Compose에 로그 관리 설정 추가 ([92ca3f3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/92ca3f3f507c909ee1d6286ff7d789acbdcc52e8))
* 리뷰 탭 전환 시 레이아웃 밀림 방지용 로딩 스피너 추가 [#669](https://github.com/woowacourse-teams/2025-Fit-toring/issues/669) ([d577d37](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d577d375e81cece9255c60287a7f922a726b8089))
* 리뷰 탭에서 '멘토링 보러가기' 클릭 시 상세보기 탭 전환 및 스크롤 이동 기능 추가 [#669](https://github.com/woowacourse-teams/2025-Fit-toring/issues/669) ([6aea454](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6aea454a5180b1640d64de659d787b1c55bef05f))
* 멘토링 엔티티에 생성 시간 컬럼 추가 ([92fc441](https://github.com/woowacourse-teams/2025-Fit-toring/commit/92fc441ae305846b829fe9da8515c0f1655af90c))
* 모달 열림시 esc키로 닫을 수 있는 기능 추가 [#669](https://github.com/woowacourse-teams/2025-Fit-toring/issues/669) ([3ab6d33](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3ab6d336ba49d2a837419fce32476d1f8c7143a9))
* 자격사항 보러가기 클릭시 페이지내에서 스크롤 이동하도록 구현 [#669](https://github.com/woowacourse-teams/2025-Fit-toring/issues/669) ([7226dc3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7226dc36c32cf63efce1656330d75187a96dcadd))
* 프로필 이미지 클릭시 원본 이미지를 보여주는 모달이 나타나도록 구현 [#669](https://github.com/woowacourse-teams/2025-Fit-toring/issues/669) ([0b28f82](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0b28f824289dbd941e82a0eb294186bf54af910d))

# [1.6.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.5.0...v1.6.0) (2025-09-18)


### Bug Fixes

* CaptureSentryErrorParams의 level 타입을 Sentry에서 제공하는 Sentry.SeverityLevel로 변경 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([31d8a11](https://github.com/woowacourse-teams/2025-Fit-toring/commit/31d8a11175070f8e0d4b389caf9a5324f042d1bd))
* Certificates 인터페이스의 certificateId 타입을 number에서 string으로 변경 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([b99ff58](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b99ff5813c5980d516a7e2fc4b2385c8ebbf4945))


### Features

* addSentryBreadcrumb 함수 추가하여 Sentry에 커스텀 breadcrumb 추가 기능 구현 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([1843161](https://github.com/woowacourse-teams/2025-Fit-toring/commit/18431613bc166556111f0f654c42b4b3ec277fe9))
* captureSentryError 함수에 extras 매개변수 추가 및 Sentry.withScope로 스코프 설정 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([5f7b818](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5f7b8187dafde7fa41b8187683536b5f41f1c65d))
* Sentry 초기화 시 환경 변수 설정 추가 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([eb1d8b6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/eb1d8b6d75b0caa2b766bc39412f0423407f8da5))
* 멘토링 개설 captureSentryError의 level을 'warning'에서 'error'로 변경하고 extras에 mentoringData, profileImageFile, certificateImageFiles 추가 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([372f749](https://github.com/woowacourse-teams/2025-Fit-toring/commit/372f749674035349d0f7799a33ca7f8c64a86623))
* 멘토링 생성 Sentry breadcrumb 추가 - 멘토링 데이터, 프로필 이미지, 자격증 이미지 변경 및 폼 제출 시도 시 로깅 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([66ed410](https://github.com/woowacourse-teams/2025-Fit-toring/commit/66ed4105bab01e70e3677354f7810b2a0580c404))
* 멘토링 업데이트 captureSentryError의 level을 'warning'에서 'error'로 변경하고 extras에 mentoringData, profileImageFile, certificateImageFiles, mentoringId 추가 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([53c40da](https://github.com/woowacourse-teams/2025-Fit-toring/commit/53c40daafe5e42401d27fe14a2da8a5c2ff7f6e3))
* 멘토링 예약 captureSentryError의 level을 'warning'에서 'error'로 변경하고 extras에 counselContent 추가 [#680](https://github.com/woowacourse-teams/2025-Fit-toring/issues/680) ([2370493](https://github.com/woowacourse-teams/2025-Fit-toring/commit/237049395a8ab3fe5f4c25b68d04ddc0444a11e7))

# [1.5.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.4.1...v1.5.0) (2025-09-18)


### Features

* 이미 있는 전화번호로 가입시 실패 alert 추가 [#681](https://github.com/woowacourse-teams/2025-Fit-toring/issues/681) ([592aff0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/592aff0eb8f1608261fadeeb0c357f7be362e2c2))

## [1.4.1](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.4.0...v1.4.1) (2025-09-17)


### Bug Fixes

* Footer 컴포넌트의 이메일 주소 수정 [#661](https://github.com/woowacourse-teams/2025-Fit-toring/issues/661) ([39be9e5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/39be9e5506f2b0c1ae7619699011ed63412fd2ba))
* og-image 경로 재설정 [#667](https://github.com/woowacourse-teams/2025-Fit-toring/issues/667) ([4f2223f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4f2223f9741ec5d7c4c73e7c7d16da1565195c8f))
* prod 에서는 prod.txt dev 에서는 dev.txt 가 복사되도록 변경 [#667](https://github.com/woowacourse-teams/2025-Fit-toring/issues/667) ([b394af7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b394af77a683dd0fcd267eb123d80768840e45bd))
* 멘토링에 달린 리뷰 조회 테스트 수정 ([9790ffe](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9790ffea8c3c904b300310fe0b15dd3a776426ba))

# [1.4.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.3.1...v1.4.0) (2025-09-15)


### Bug Fixes

* chmod 제거 [#622](https://github.com/woowacourse-teams/2025-Fit-toring/issues/622) ([bd9cfe2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bd9cfe2fafbf1a6b61b204f842b339d53306e9cf))
* chromatic CI 브랜치 경로 변경 ([9efc583](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9efc583f9cc23a51013a949c6b890f58e196be2d))
* CI 브랜치 경로 설정 문제로 실행 안되던 버그 수정 [#630](https://github.com/woowacourse-teams/2025-Fit-toring/issues/630) ([e9d43bf](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e9d43bf73f8290a3928de75e4bb61bbd7b5aee68))
* CI 브랜치 경로 설정 수정 [#634](https://github.com/woowacourse-teams/2025-Fit-toring/issues/634) ([bc28493](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bc28493cffc1a693a93e94a08b74e690e7205c18))
* CORS 허용 경로 추가 [#618](https://github.com/woowacourse-teams/2025-Fit-toring/issues/618) ([61e9470](https://github.com/woowacourse-teams/2025-Fit-toring/commit/61e947048de289d6dae140803397c36c803c1819))
* DB 포트넘버 수정[#607](https://github.com/woowacourse-teams/2025-Fit-toring/issues/607) ([54b190e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/54b190e6edbcff49531a4359da221e5b10359e98))
* dev 전용 compose 오버라이딩 [#607](https://github.com/woowacourse-teams/2025-Fit-toring/issues/607) ([88bfa06](https://github.com/woowacourse-teams/2025-Fit-toring/commit/88bfa06e57639b7fa388b3a85546dd4f85d64295))
* docker-compose 포트 매핑 수정 ([576107d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/576107d7d6314a4e8130d650bfc475d6df136264))
* pinpoint 경로 매핑[#607](https://github.com/woowacourse-teams/2025-Fit-toring/issues/607) ([254107b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/254107b900595f4463d197817cf22725a4f5115f))
* robots.txt 개발,배포 파일로 분리 [#654](https://github.com/woowacourse-teams/2025-Fit-toring/issues/654) ([04561dd](https://github.com/woowacourse-teams/2025-Fit-toring/commit/04561ddfd44cee5cabe42efea6ffbee6efecf10e))
* vitest ci를 위한 환경 변수 파일 경로 수정 및 테스트 환경 설정 제거 [#637](https://github.com/woowacourse-teams/2025-Fit-toring/issues/637) ([f785b4a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f785b4a173b292343a576138ba41f022ea66a237))
* Vitest 테스트 환경에서 BASE_URL 환경 변수 로드 문제 해결 ([f56429c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f56429ccb0b4d20fc3cb472ba99fc7373dbf152c))
* 검색엔진이 읽을 수 있도록 배포 시 robots.txt 로 파일 변경 [#654](https://github.com/woowacourse-teams/2025-Fit-toring/issues/654) ([a9e6a43](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a9e6a431a18cbbbd09ce3ce591ad0d1997f8ffd2))
* 도커 볼륨과 우분투 내의 pinpoint 경로 매핑[#607](https://github.com/woowacourse-teams/2025-Fit-toring/issues/607) ([41b5d14](https://github.com/woowacourse-teams/2025-Fit-toring/commit/41b5d145d340b64488ed3238a149d761604b1538))
* 도커 설정파일 원상복구 ([104e942](https://github.com/woowacourse-teams/2025-Fit-toring/commit/104e942078fabace09a3134d1db0e484f0c91a57))
* 로그인 되어 있을 시 멘토링 개설이 아닌 관리하기로 변경 [#643](https://github.com/woowacourse-teams/2025-Fit-toring/issues/643) ([4dde53f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4dde53f62dd16b620698fc10dbb1c1c478c07eb7))
* 메인페이지 슬로건 및 오버뷰 제거 [#643](https://github.com/woowacourse-teams/2025-Fit-toring/issues/643) ([61f19dc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/61f19dc44a11bc53ff0b444bd4a9b33704045df3))
* 멤버 조회 실패 시 예외 응답 메시지 수정 [#580](https://github.com/woowacourse-teams/2025-Fit-toring/issues/580) ([6bd7d49](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6bd7d49e58230eb4b4914fed69e1b8ee338c846e))
* 모든 url 경로 www 추가 [#654](https://github.com/woowacourse-teams/2025-Fit-toring/issues/654) ([7e74f50](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7e74f50fba8c6174cd5ba26d05fb274f26fbff8d))
* 변경된 컴포넌트 props에 맞게 스토리 args 변경 [#595](https://github.com/woowacourse-teams/2025-Fit-toring/issues/595) ([bedb73b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bedb73b6111aa46819145332d96edb44420396b5))
* 빌드 테스트의 환경 변수 생성 로직 수정 및 빌드 단계 분리 [#599](https://github.com/woowacourse-teams/2025-Fit-toring/issues/599) ([ee02098](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ee02098b586922e093d7caff299246a4a17bd2b2))
* 사용되지 않는 @OnDelete 어노테이션 제거 ([4ae0174](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4ae0174714bf02b8ccd0f2f9d92a85c10ef41606))
* 서버에서 오는 타입과 일치하게 타입 변경 [#593](https://github.com/woowacourse-teams/2025-Fit-toring/issues/593) ([a5c9446](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a5c94461d578cc77a8119b8605465a0701c3301a))
* 예약페이지의 하드코딩된 별점 실제 데이터로 변경 [#595](https://github.com/woowacourse-teams/2025-Fit-toring/issues/595) ([600481f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/600481f785bc23738f9d0e9d016b3820cdfa2908))
* 워크플로우 실행 조건을 컴포넌트 내부의 코드 수정 시 발생하도록 변경하여 Storybook 파일 경로를 올바르게 설정 ([d2d4410](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d2d44106ed7433cf0b82dc849de83188dc28dff0))
* 자격증 유형 입력 필드가 제어 컴포넌트로 변경됨에 따라 defaultValue 제거 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([c91d0eb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c91d0ebf6e1f0ab33da0f4f6e6a4addda7a61eae))
* 잘못된 타입 경로 수정 [#593](https://github.com/woowacourse-teams/2025-Fit-toring/issues/593) ([b660e71](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b660e7186026228b4e9fc006f5b36015c0797a02))
* 캐노니컬 태그 수정 [#654](https://github.com/woowacourse-teams/2025-Fit-toring/issues/654) ([c47635d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c47635d2bdc82f6f623c7961cc7597cddf90552a))
* 크로마틱 빌드의 환경 변수 생성 로직 수정 ([3e697b8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3e697b8f5bc4d34f1e9ab311c91c120f9d79ef9b))
* 테스트 CI 환경 변수 생성 로직을 수정하여 브랜치에 따라 API_BASE_URL을 설정 ([10cb3db](https://github.com/woowacourse-teams/2025-Fit-toring/commit/10cb3db5cb8ee9b808a18b9a7cc51a10b2a86496))
* 테스트 파일 경로를 수정하여 모든 프론트엔드 파일을 포함 [#598](https://github.com/woowacourse-teams/2025-Fit-toring/issues/598) ([91be5ab](https://github.com/woowacourse-teams/2025-Fit-toring/commit/91be5ab50295ced64dc38d52c2c2eaa3b614450b))
* 테스트 환경 flyway 설정 변경 ([9765abe](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9765abe452e787d57d162eb98c5bc0ca10fc28fe))
* 푸터 임시 주석 처리 [#643](https://github.com/woowacourse-teams/2025-Fit-toring/issues/643) ([42779ea](https://github.com/woowacourse-teams/2025-Fit-toring/commit/42779ea8526c088381207c1e8ebca3dd8e3f2cda))
* 하드코딩된 멘토링 가격 실제 데이터로 변경 [#595](https://github.com/woowacourse-teams/2025-Fit-toring/issues/595) ([8b7a48c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8b7a48c8c7148e03972d77ca837cd085bcee1441))
* 하위 경로와 매핑[#607](https://github.com/woowacourse-teams/2025-Fit-toring/issues/607) ([41c1899](https://github.com/woowacourse-teams/2025-Fit-toring/commit/41c18999d6b96789a5e005e3099f5ae0fa51d467))


### Features

* api 문서 생성을 위한 gradle task 작업 자동화 ([87f0ec3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/87f0ec373603d1ef387a82ee1a50beb2b376db77))
* api별 구분자를 위한 restdocs 태그 설정 ([f55f9f8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f55f9f86a51bdaae6f26854e7f47101435188885))
* CI/CD workflows에 테스트 DB 설정 및 API 문서 생성 태스크 추가 ([d9bf928](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d9bf928f5446d4d4fc79c6f40dde86d04d9fb472))
* CORS 허용 Origin에 http://localhost:8080 추가 ([7e5f3ed](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7e5f3ed4a5a06f8a13f3653210809cfd594489b1))
* dev 워크플로우 PROFILE 추가 [#607](https://github.com/woowacourse-teams/2025-Fit-toring/issues/607) ([e614763](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e6147635b19d97e2a8bd479a91ca8fda28283d14))
* FitnessQuestionFlow 컴포넌트 생성 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([81cd404](https://github.com/woowacourse-teams/2025-Fit-toring/commit/81cd404fa076aa05ebb12b2b52a0f8f2c791389d))
* HEIC 파일 형식 확인 기능 추가 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([d91545e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d91545ee2e7a8796663355f716400777fac947f2))
* HEIC 파일을 JPEG로 변환하는 기능 추가 및 이미지 입력 처리 개선 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([3b46bf2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3b46bf2bbe06e26ac16fa0f88dd5cd04e7870a28))
* HEIC 파일을 JPEG로 변환하는 함수 추가 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([bd20796](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bd207967c1ed5e6b81193ada9f316b1b854a06d8))
* heic 파일을 다른 이미지 포맷 파일로 변환하기 위한 heic2any 패키지 추가 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([d92a536](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d92a5369a96f2bd3f10532a0c755550003a4447b))
* HEIC 파일이면 JPEG로 변환하는 함수 추가 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([aba3fdf](https://github.com/woowacourse-teams/2025-Fit-toring/commit/aba3fdf92a328e35506030b296547bdcd67f83f7))
* Introduce 컴포넌트 생성 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([1fe71ea](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1fe71ea1c9c0fd5fa469ef8cb1adc6c2e1d5b157))
* ManyToOne 연관관계를 가지는 엔티티 LAZY 로딩 전략 추가 ([701fe03](https://github.com/woowacourse-teams/2025-Fit-toring/commit/701fe03e04f6e28e12f2c005a6b5e17eee4338d8))
* MentoringStepper 컴포넌트 생성 [#593](https://github.com/woowacourse-teams/2025-Fit-toring/issues/593) ([dc6888e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/dc6888e98de3c70b3d5cdb87dcc7990b8f1dc811))
* OpenAPI 서버 정보 다중 설정 추가 ([f877975](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f877975e22d8fffc56d3c10734307fdb9881d19e))
* OpenApiSpec 파일 static 내부 경로로 복사 태스크 추가 ([f8ae33d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f8ae33d003cc35a1cf9d5ffca0f0848a98777b52))
* PAGE_URL: 랜딩페이지 url 추가 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([f56165f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f56165f265702f71f2a491657da4718268effe7a))
* QuestionBubble 컴포넌트 생성 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([c743d04](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c743d047f04790052772c83ea2788512c0525164))
* RestDocs Configurer 타입 수정 ([281184a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/281184a50c0afbc9510b214a9dc1107ebac8a8ca))
* restdocs gradle 설정 적용 ([5e9c896](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5e9c8963f8af4e09e909f23bd7be831670d75140))
* restdocs-api-spec 환경 설정 ([bd51628](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bd5162888efd94f9bee63ca5a83e720a4c208473))
* Slogan컴포넌트 생성 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([d69ecc3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d69ecc35cff4b1e77766d34fc55bc589a1d40525))
* Step 컴포넌트 생성 [#593](https://github.com/woowacourse-teams/2025-Fit-toring/issues/593) ([0e10307](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0e103070cdee7ce8f68e93f9c257c6c4f137baae))
* swagger-ui 구성 파일 추가 ([d888f32](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d888f32a7b7d84a7bd73dae79c706c03a1b30edf))
* swagger-ui 정적 파일 매핑을 위한 핸들러 설정 추가 ([77792b5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/77792b5bef92714dc8b8eccc3a8c857bc79dab53))
* UserLevelGuide 컴포넌트 생성 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([8ac2e6c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8ac2e6c5be808f616403ed70dbca980c593d6690))
* 개행 제거 [#607](https://github.com/woowacourse-teams/2025-Fit-toring/issues/607) ([e6c903b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e6c903b3c94f972504f5f2133f33e17db57fd075))
* 검색 엔진에 사이트 구조를 알려주기 위한 sitemap 작성 ([2b1b770](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2b1b7702edf30b54e9fd63ed2f8f549f1fefa8cb))
* 검색 엔진이 페이지를 크롤링 할 수 있게 robots.txt 추가 ([7b26fd7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7b26fd7dd11379625734e4f7813787ffba39c744))
* 공유시 미리보기 카드 openGraph 설정 추가 [#604](https://github.com/woowacourse-teams/2025-Fit-toring/issues/604) ([fe034e6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fe034e60df8b12d951f6194968dcb9d3aa1edea8))
* 관리자 리뷰 목록 조회 기능 구현 [#582](https://github.com/woowacourse-teams/2025-Fit-toring/issues/582) ([c9bdf5a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c9bdf5af6a8f5d1ceb24731e8aaf6f24dab76ecc))
* 관리자 리뷰 목록 조회 응답 폼 추가 [#582](https://github.com/woowacourse-teams/2025-Fit-toring/issues/582) ([15e1aae](https://github.com/woowacourse-teams/2025-Fit-toring/commit/15e1aae6bb857872bf6ce123cbbcaee814a7281c))
* 관리자 리뷰 목록 조회 컨트롤러 구현 [#582](https://github.com/woowacourse-teams/2025-Fit-toring/issues/582) ([40e82ed](https://github.com/woowacourse-teams/2025-Fit-toring/commit/40e82ede3df52e31b6facf0cf8cf5deff66040db))
* 관리자 리뷰 삭제 기능 구현 [#582](https://github.com/woowacourse-teams/2025-Fit-toring/issues/582) ([3a96d57](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3a96d57d70886472528e7c89e7f31e38e60da817))
* 관리자 리뷰 삭제 컨트롤러 구현 [#582](https://github.com/woowacourse-teams/2025-Fit-toring/issues/582) ([74de69b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/74de69bdb4f29784471224b848e83af672c51725))
* 관리자 예약 api 테스트 restdocs 문서화 적용 ([ef169f4](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ef169f48d61a0d3b73d7f3091c7098e41b02e887))
* 관리자 자격 증명 api 테스트 restdocs 문서화 적용 ([b782cc9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b782cc9b8229b01ee6de03e6ea09539bebda7062))
* 관리자 전용 사용자 목록 조회 기능 구현 [#580](https://github.com/woowacourse-teams/2025-Fit-toring/issues/580) ([112eee6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/112eee6fc2b70f1c4db6086ed4943f0cdc1a4371))
* 기존 openapi3 파일 삭제 설정 ([e6a980e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e6a980e27c1130c8d97387b7e65622bf8a6a55c2))
* 랜딩페이지 Footer컴포넌트 구현 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([27e9faf](https://github.com/woowacourse-teams/2025-Fit-toring/commit/27e9faf5dfeb8b809167a1297f32f168763a4f6a))
* 랜딩페이지 이미지로 구현 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([1f64cf8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1f64cf8d316841cfd9382d121935a0a820bf516a))
* 랜딩페이지 컴포넌트 생성 및 Slogan컴포넌트 적용 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([2c045af](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2c045afa763911d1b6ab57cf426f5f923edc94ac))
* 랜딩페이지에 Footer컴포넌트 적용 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([05fe054](https://github.com/woowacourse-teams/2025-Fit-toring/commit/05fe0541dd3679e97dce82575dea688565505d45))
* 로딩 스피너 컴포넌트 추가 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([79de5b0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/79de5b07dbaa1a56687788c718bbd31108b65579))
* 리뷰 api 테스트 restdocs 문서화 적용 ([28ac389](https://github.com/woowacourse-teams/2025-Fit-toring/commit/28ac3897183c02194a55c7643e777fc0f798f3e1))
* 리뷰 엔티티 soft delete 구현 ([81d3a3a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/81d3a3adeb9d36328a52ad854834ac23e327f5a4))
* 리뷰 엔티티 삭제 상태, 삭제 시간 컬럼 추가와 flyway 스크립트 추가 ([036ccb9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/036ccb9425c46722308288019d89cf72ffc95492))
* 메뉴 외부 클릭 시 메뉴가 닫히도록 기능 추가 [#612](https://github.com/woowacourse-teams/2025-Fit-toring/issues/612) ([4281781](https://github.com/woowacourse-teams/2025-Fit-toring/commit/42817814b5c1c46df66974f86fa20b252aae7985))
* 멘토 카드 아이템 cursor:pointer 추가 [#643](https://github.com/woowacourse-teams/2025-Fit-toring/issues/643) ([33025a2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/33025a2c6d722dd282a3e8acde7fb2ac405b9b44))
* 멘토링 api 테스트 restdocs 문서화 적용 ([8ccb6ea](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8ccb6ea3cf93588f18400b26068be503d0ddeb0d))
* 멘토링 개설 시 카테고리 목록 반영 [#583](https://github.com/woowacourse-teams/2025-Fit-toring/issues/583) ([30f6bfd](https://github.com/woowacourse-teams/2025-Fit-toring/commit/30f6bfdb2cef3a3c4fcb654c9a47a983ac46090d))
* 멘토링 개설 시 카톡 오픈채팅 링크 Input 추가 [#602](https://github.com/woowacourse-teams/2025-Fit-toring/issues/602) ([7666a46](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7666a468a0b5f51dd0274cb803322ff404d922b6))
* 멘토링 상세 리뷰목록 조회 UI 구현 [#583](https://github.com/woowacourse-teams/2025-Fit-toring/issues/583) ([a8450a1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a8450a16037346b7796529da360df4f2098fb313))
* 멘토링 수정 시 카톡 오픈채팅 링크 추가 [#602](https://github.com/woowacourse-teams/2025-Fit-toring/issues/602) ([9bf7ee1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9bf7ee1d10669e9145d03589211ecc1800ed118a))
* 멘토링 엔티티 soft delete 구현 ([b32d47b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b32d47b97bd79c083968276a93bf4cef7ed5e370))
* 멘토링 엔티티 삭제 상태, 삭제 시간 컬럼 추가와 flyway 스크립트 추가 ([265c8bf](https://github.com/woowacourse-teams/2025-Fit-toring/commit/265c8bf0db392e45b5ce9eadda73c91fcf6ee44a))
* 멘토링 예약 api 테스트 restdocs 문서화 적용 ([7e2d5b8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7e2d5b8fd16080c852bf17502571cf65089dae66))
* 멘토링 예약 카드에 승인 과정을 나타내는 Stepper컴포넌트 추가 [#593](https://github.com/woowacourse-teams/2025-Fit-toring/issues/593) ([75359af](https://github.com/woowacourse-teams/2025-Fit-toring/commit/75359afbad79dab0ce6a29463f5e004b2101c1bb))
* 멘토링 채팅 필드 테스트 적용 ([2afdf2b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2afdf2b08549f5f93d14229ddc4d14fe4267700e))
* 멤버 api 테스트 restdocs 문서화 적용 ([e38a967](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e38a967e1386e81241ac4946772015052a8c9b56))
* 멤버 엔티티 soft delete 구현 ([fb9eef9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fb9eef9be4d355073f8f8abf3e62f4948d772d81))
* 멤버 엔티티 삭제 상태, 삭제 시간 컬럼 추가와 flyway 스크립트 추가 ([f0d8c6e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f0d8c6ea914f5a698abf55ecae23846427fe11c4))
* 비동기 로딩 입력 훅 추가 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([578ae66](https://github.com/woowacourse-teams/2025-Fit-toring/commit/578ae66323c5f65005d7051478f63b21e323ed98))
* 사용자 목록 조회 UI 구현 [#583](https://github.com/woowacourse-teams/2025-Fit-toring/issues/583) ([34dd80d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/34dd80daaeb02fd7aa4ea1039f65da679d1e825d))
* 삭제 상태인 멘토링을 조회하는 사용자 정의 쿼리 메서드 추가 ([6b79522](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6b79522a1273848ac8662d8ac60fd1b07966b26a))
* 삭제 상태인 카테고리_멘토링 목록을 조회하는 쿼리 메서드 추가 ([647ddca](https://github.com/woowacourse-teams/2025-Fit-toring/commit/647ddca543eeeba8f7599c1a53546d9239e4e35e))
* 상단바(필터,정렬,멘토링 개설) 디자인 변경 [#643](https://github.com/woowacourse-teams/2025-Fit-toring/issues/643) ([64941ba](https://github.com/woowacourse-teams/2025-Fit-toring/commit/64941ba5722add9a1e68cb071345bb156f81a865))
* 서비스 의존성 주입 [#580](https://github.com/woowacourse-teams/2025-Fit-toring/issues/580) ([d4aa6a8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d4aa6a8d8c766a4d2308defaaa2b8c0b34e428d7))
* 예약 엔티티 soft delete 구현 ([73cd04c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/73cd04c205bd771b433cee349eebd980fca3dd59))
* 예약 엔티티 삭제 상태, 삭제 시간 컬럼 추가와 flyway 스크립트 추가 ([e7508e0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e7508e0f2d62f1d818666b4e06d7f055239d522d))
* 예약 완료 모달 UI 변경 및 Stepper 컴포넌트 적용 [#593](https://github.com/woowacourse-teams/2025-Fit-toring/issues/593) ([8c09615](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8c09615ba1a63350424f3d1d1cf83f8bd6dee954))
* 오픈 채팅 url 유효성 검증 추가 [#602](https://github.com/woowacourse-teams/2025-Fit-toring/issues/602) ([971cfe8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/971cfe836c655ab43ef625e6b2645cca22ed59a8))
* 인증 api 테스트 restdocs 문서화 적용 ([5ec6e76](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5ec6e76df7ce0edf04f92b80c891f65a1465948d))
* 자격증 이미지 업로드 시 로딩 스피너 추가 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([42ba4f1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/42ba4f1ade1fca0cccf3f2a066600e5932c80b7c))
* 자격증명 엔티티 soft delete 구현 ([865b311](https://github.com/woowacourse-teams/2025-Fit-toring/commit/865b3111396073b580567c962109e8cfd56a7a37))
* 자격증명 엔티티 삭제 상태, 삭제 시간 컬럼 추가와 flyway 스크립트 추가 ([ebac30f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ebac30fa9664f22d1d563f8322860e6bc2b4f3c6))
* 중복 컨텐츠 문제를 해결하기 위해 canonical 태그 추가 [#604](https://github.com/woowacourse-teams/2025-Fit-toring/issues/604) ([1e1e0b7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1e1e0b7017fea4868e7ee809563930502759fdbb))
* 채팅 url 필드 추가 ([8a8bbce](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8a8bbcee62b5b47b12c0b91d71a1d21a1472b68a))
* 채팅 url 필드 추가 적용 ([bbbe0c1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bbbe0c143a9e709b5648fcd75e5ad98b681a12dc))
* 첫 방문 Landing 페이지 구현 및 세션 기반 홈 이동 구현 [#653](https://github.com/woowacourse-teams/2025-Fit-toring/issues/653) ([f2e7c4b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f2e7c4bec68ee8ca91a0da08f5f53f7d533bddd2))
* 카테고리 api 테스트 restdocs 문서화 적용 ([3b8387a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3b8387acfa2eb0d3532cfc63c13a4f79f102e502))
* 카테고리_멘토링 엔티티 soft delete 구현 ([a6f8a7c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a6f8a7c5296b3ef5a7f5d9b68a5c048e47c3590e))
* 카테고리_멘토링 엔티티 삭제 상태, 삭제 시간 컬럼 추가와 flyway 스크립트 추가 ([50b29a2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/50b29a209172e74da5634e360b9c314286439b78))
* 툴팁 호버시 안내문구 나타나도록 구현 [#593](https://github.com/woowacourse-teams/2025-Fit-toring/issues/593) ([73923a2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/73923a2d5ba4bb8e04d42ab5e8de1e61dcb1e949))
* 페이지 소유 확인을 위한 meta 태그 추가 [#654](https://github.com/woowacourse-teams/2025-Fit-toring/issues/654) ([a524c46](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a524c46681637ecb6ac327322927b0db083307bf))
* 프로필 사진 업로드 시 로딩 스피너 추가 및 비동기 처리 개선 [#609](https://github.com/woowacourse-teams/2025-Fit-toring/issues/609) ([cc37f99](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cc37f99d1beb5a6e0f796630eda225dcf864dc89))
* 헤더 테두리 제거 및 스크롤 시 테두리 생성 [#643](https://github.com/woowacourse-teams/2025-Fit-toring/issues/643) ([bce6e06](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bce6e06252354d7818c0d6abffcb8a5c6ca8cb52))

## [1.3.1](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.3.0...v1.3.1) (2025-08-21)


### Bug Fixes

* 멘토링 예약의 내용 수정 ([9d15401](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9d1540122df9527b5b3679b9f78d3db50f581b90))

# [1.3.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.2.0...v1.3.0) (2025-08-21)


### Bug Fixes

* 빌드 스크립트에서 환경 변수(APP_ENV) 사용으로 변경 [#566](https://github.com/woowacourse-teams/2025-Fit-toring/issues/566) ([e8c33ea](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e8c33eaf25b24bcf31dd527647909d6e83b9d20c))
* 인증확인 유효성 검사 로직 문제 해결 [#549](https://github.com/woowacourse-teams/2025-Fit-toring/issues/549) ([45a5346](https://github.com/woowacourse-teams/2025-Fit-toring/commit/45a53468b27bf23d55215c3be205d4c356568649))


### Features

* 개발 서버 주소 CORS 허용 추가 ([2e7dd89](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2e7dd89e7ed8de074bb710f8905e4dd8e9e012f7))

# [1.2.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.1.0...v1.2.0) (2025-08-21)


### Bug Fixes

* ci/cd에 platforms 설정 추가 ([ac89ca9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ac89ca9b8c43d077277e62352605b277806bb9af))
* DB클리너 flyway history 지우지 않도록 수정 [#506](https://github.com/woowacourse-teams/2025-Fit-toring/issues/506) ([233feab](https://github.com/woowacourse-teams/2025-Fit-toring/commit/233feab55f1dfa894472e5e0a82b05ad43508cfb))
* 태그 설정 수정 ([3e57bee](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3e57bee6034c4ef31fa5d597389d16a42c9e5d1e))


### Features

* flyway 의존성 [#506](https://github.com/woowacourse-teams/2025-Fit-toring/issues/506) ([46b1423](https://github.com/woowacourse-teams/2025-Fit-toring/commit/46b14232a7024eb81467522e70b5370536cce327))
* flyway 의존성 추가 [#506](https://github.com/woowacourse-teams/2025-Fit-toring/issues/506) ([9a1c0fc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9a1c0fc3bf9e8b897a5320d087521501d9f7951c))
* 개발 환경 flyway 설정 추가 [#506](https://github.com/woowacourse-teams/2025-Fit-toring/issues/506) ([023046e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/023046e4f02bfc7df00dab7fbaa05e80b7cae062))
* 테스트 환경 flyway 설정 추가 [#506](https://github.com/woowacourse-teams/2025-Fit-toring/issues/506) ([5c659ad](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5c659adae3f456de39b9d8e1f07cfc35f7323712))
* 토큰 없이 리프레시 토큰 재발급 요청 시 예외 처리 ([210cd1b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/210cd1ba8fc2173a4615ba4d1a8d4fd808ae6a5a))

# [1.1.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.0.0...v1.1.0) (2025-08-21)


### Bug Fixes

* ApiError에서 상태 코드를 사용자 메시지와 분리하여 UX 개선 [#513](https://github.com/woowacourse-teams/2025-Fit-toring/issues/513) ([a3ae41e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a3ae41ed79d07ef4fc001d91fb127f7d79b6b75e))
* cd runner 라벨 변경 ([e0fb107](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e0fb107b612851ce4235be366949598f67281dea))
* cd 배포 도커 로그인 단계에서 root 로그인으로 변경 ([fcf66cc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fcf66ccb4847e990137c4dd348535c1a049d8371))
* cd 워크플로우 도커 이미지 빌드 방식을 ARM 전용으로만 빌드하도록 수정 ([0b964c2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0b964c28e415a77480d79c49cc30b7434eb823ca))
* deploy-dev if 조건 제거 ([81750b1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/81750b1fe3c017bbc1816ac7fd0cf87f423a993c))
* getMineMentoring 호출 주석처리된 부분 복구 [#549](https://github.com/woowacourse-teams/2025-Fit-toring/issues/549) ([8a8c4ad](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8a8c4ade90a9a4fed22ae89612a46a8072eb8c7f))
* 로그아웃 시 myMentoringId를 초기화하여 버튼 상태가 변경되지 않는 문제 해결 [#540](https://github.com/woowacourse-teams/2025-Fit-toring/issues/540) ([393fece](https://github.com/woowacourse-teams/2025-Fit-toring/commit/393fece2c9ea71af37d6bbd62e1d3ae258f025e4))


### Features

* cd 배포 steps 도커 로그인 단계 추가 ([63b4f78](https://github.com/woowacourse-teams/2025-Fit-toring/commit/63b4f7882dfa8b6ee9e9483d52bd6a4dd626678f))
* cd 워크플로우 .env 파일쓰기 권한 추가 ([7048cc3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7048cc352152000449457a47d874555dac409f3b))
* cd 워크플로우 도커 이미지 IMAGE, TAG 저장 이동 경로 추가 ([cf3206b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cf3206b51309b27a5d82f5a2fceb494f68b6a69c))
* cd 워크플로우 멀티 아키텍처 이미지 빌드를 위한 platforms 옵션 설정 ([9bc0fa1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9bc0fa1f6a03c4f892359fb9f3344bd53c176b5a))
* cd 워크플로우 빌드된 도커 이미지의 IMAGE, TAG 환경변수 주입을 위한 설정 추가 ([8244e38](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8244e38322775e274da3ff68722f06d39f769761))
* Certificate 엔티티의 Mentoring 필드에 @ OnDelete 어노테이션 Cascade 속성 추가 ([2271651](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2271651a9ea2aac9c56b564866063f542ffe2116))
* ci, build-and-push Gradle 캐시 적용 ([2253a54](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2253a54372242243f5c4cc8bc8173d10788b2e3a))
* deleteCertificate 함수 생성 [#504](https://github.com/woowacourse-teams/2025-Fit-toring/issues/504) ([05d9974](https://github.com/woowacourse-teams/2025-Fit-toring/commit/05d997475d347134d4fbeb8f4a8bdeb5ab91de6b))
* deploy-dev needs 조건 추가 ([4bafbf8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4bafbf8a09c73d6e62d7ccfdb0577e3e905b3875))
* DuplicatePhoneException 전역 예외처리 핸들링 추가 ([bee4c1b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bee4c1b470fa53eb83175c75d4af87ec639bbea3))
* StyledLogoLink에 reloadDocument 속성 추가 [#518](https://github.com/woowacourse-teams/2025-Fit-toring/issues/518) ([ca02e49](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ca02e4971c02d1afe1cca41512f1b1c79276db87))
* 개설한 멘토링이 있는 경우 멘토링 개설하기 -> 멘토링 관리하기로 보이도록 로직 변경 [#540](https://github.com/woowacourse-teams/2025-Fit-toring/issues/540) ([c986acb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c986acb2702b6bd5d7872cb000bc28f8222ad49d))
* 네비게이팅 경로 변경 [#510](https://github.com/woowacourse-teams/2025-Fit-toring/issues/510) ([715fa86](https://github.com/woowacourse-teams/2025-Fit-toring/commit/715fa863013d7969bc35f34342230209108c085c))
* 리뷰 작성 후 alert 추가 [#510](https://github.com/woowacourse-teams/2025-Fit-toring/issues/510) ([3154222](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3154222c92356227cbfe09423f44f043cf386441))
* 리뷰 작성 후 페이지 이동 [#510](https://github.com/woowacourse-teams/2025-Fit-toring/issues/510) ([059d515](https://github.com/woowacourse-teams/2025-Fit-toring/commit/059d51519d28f97fe6cf70b8c36f194b7b052ae6))
* 모든 요청에 Sentry 추가 [#511](https://github.com/woowacourse-teams/2025-Fit-toring/issues/511) ([7a8a5e3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7a8a5e36af5ec6c4d34774d35c818b40764f48a1))
* 수정하기 클릭 시 선택된 자격증 삭제 API 호출 기능 추가 [#504](https://github.com/woowacourse-teams/2025-Fit-toring/issues/504) ([76d6dd3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/76d6dd389ea22e1a6c65c39d19d12dee025ddaa2))
* 시맨틱 버저닝 자동화를 위한 설정 추가 [#538](https://github.com/woowacourse-teams/2025-Fit-toring/issues/538) ([811f514](https://github.com/woowacourse-teams/2025-Fit-toring/commit/811f5142babb627e8ae84889d3e474ab723f8a8e))
* 중복확인 상태에 따라 "사용 가능한 아이디입니다." 메시지 표시 [#501](https://github.com/woowacourse-teams/2025-Fit-toring/issues/501) ([6bcb2ff](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6bcb2ff83fc436cfd650fd808ee8cf9b4bad887e))
* 중복확인 체크를 위해 상태 추가 및 API 성공 시 true로 설정 [#501](https://github.com/woowacourse-teams/2025-Fit-toring/issues/501) ([af7e04b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/af7e04b216a2472d3e0e51c71a39fc4e8bc1acd4))
* 테스트 코드 수정 ([e549809](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e549809b85502436372e20978a71b0a78f1d7c8c))
