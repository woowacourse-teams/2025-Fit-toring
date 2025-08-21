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
