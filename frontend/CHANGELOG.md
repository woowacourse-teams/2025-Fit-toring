# [1.17.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.16.0...v1.17.0) (2026-03-03)


### Bug Fixes

* MobileLayout의 전역 padding-bottom 제거 및 BottomTabLayout으로 이동 [#1359](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1359) ([#1360](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1360)) ([d29a39a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d29a39a1c69bd53953eb91715d678af381ed8974))


### Features

* 에러 메시지 수신용 WebSocket 채널 구독 및 송신 실패 처리 로직 구현 [#1347](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1347) ([#1349](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1349)) ([e7a15e2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e7a15e250b27d9fe7f870affddc66020d6829e59))

# [1.16.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.15.0...v1.16.0) (2026-02-28)


### Bug Fixes

* ChatImageUrlResponse, ChatMessageNotFoundException 누락 파일 추가 ([6eb4da9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6eb4da9caf57553c22f1c0ded65cfc70cae0f76b))
* iOS 홈 화면 아이콘 표시 문제 해결을 위한 apple-touch-icon 추가 [#1258](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1258) ([#1259](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1259)) ([cb96103](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cb96103bb7d002307c0306f67ba543d4e6426314))
* SMS 읽기 타임아웃 테스트 딜레이 값 수정 ([d322bf7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d322bf7800c336598e58256d25fc7714ca624efc))
* 데이터베이스 연결 옵션 수정으로 Flyway 호환성 개선 ([cd6e311](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cd6e3116a777b85975be7dbf9bfe9f79cdfa52e4))
* 푸시 알림 메시지 필드명 수정 ([60ea2c7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/60ea2c7027730b2a887e54023e955e6c028ce253))


### Features

* allow public key retrieval ([b101b96](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b101b968313afe5e47e3bd346506b1b9f49f1d44))
* S3 Presigned URL GET 요청 기능 추가 ([04c01cd](https://github.com/woowacourse-teams/2025-Fit-toring/commit/04c01cd480d534dedacdf9af9bbeca21928a79f2))
* S3Configuration에 AWS 자격 증명 제공 로직 추가 ([79d6a37](https://github.com/woowacourse-teams/2025-Fit-toring/commit/79d6a3708c80dcf8fb1e16ea1f3644c4763c8e38))
* 이미지 메시지 Presigned URL 재발급 및 예외 처리 로직 추가 ([41eab4b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/41eab4b89f2aad84c0ed1cdfb93d41cc3fa57390))
* 이미지 메시지 푸시 알림 제목 설정 로직 추가 ([a1df3b4](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a1df3b431abf61b43bff5945f42ec56c87dfd800))
* 채팅 메시지 DTO 필드명 수정 및 검증 로직 변경 ([739a6b0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/739a6b0f05c28e7e55d9c20d6bebb1177c66177f))
* 채팅 메시지 등록 로직 확장 및 이미지 메시지 처리 추가 ([b885f80](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b885f807e9ef4166930ff9388c89097f8a8f4359))
* 채팅 메시지 응답 로직 개선 및 이미지 메시지 섬네일 처리 추가 ([27bd16d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/27bd16d6557cfed4658be4d5905727367c55e389))
* 채팅 메시지 이미지 타입 검증 예외 처리 로직 추가 ([82c4ab7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/82c4ab78f3661d11e188f5639e88a6e3308155f4))
* 채팅 메시지 전송시 messageType 추가 [#1320](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1320) ([#1321](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1321)) ([8db79ed](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8db79edbd926c424a129dae24d2ea1448b851dae))
* 채팅 메시지 접근 권한 예외 처리 로직 추가 ([fb831b9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fb831b92e89207cfb282fe0225bd2413290d8459))
* 채팅 메시지 타입(enum) 추가 및 관련 로직 수정 ([b6c8fc9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b6c8fc91c295d0cd7b16af62ae620f022f5953b6))
* 채팅 메시지 타입별 필드 검증 로직 추가 및 DTO 수정 ([c12ef13](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c12ef13fbcc277b5a24079059ab8d6af8ee88f57))
* 채팅 이미지 메시지 응답 및 Presigned URL 처리 로직 리팩토링 ([3e258ea](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3e258eae23c715508066fbe6c981207fef4f76b4))
* 채팅 이미지 메시지 테스트 추가 및 관련 로직 문서화 ([8fb01be](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8fb01be76eca7b677da40d4f06708a7169135559))

# [1.15.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.14.0...v1.15.0) (2026-02-11)


### Bug Fixes

* cleanupServiceWorkerInDev 함수 제거 및 관련 코드 정리 [#1278](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1278) ([#1279](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1279)) ([2873e26](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2873e26757331c91a0665386e2089cbd0d7db465))
* CreatedMentoring 컴포넌트에서 useMineMentoring 훅을 useMyMentoringId로 변경하여 인증된 멘토링 ID 사용 [#1123](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1123) ([#1124](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1124)) ([680da72](https://github.com/woowacourse-teams/2025-Fit-toring/commit/680da72bdfee0214e1a47190acf43314c0326867))
* docker-compose 포트 설정 수정 ([c2ccdce](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c2ccdcea875e6d94d193f758bdeabd92b9625e71))
* FCM 알림 전송 메서드 매개변수 순서 변경 ([30514aa](https://github.com/woowacourse-teams/2025-Fit-toring/commit/30514aaeceffb6915809b15bc58f2a1af0aac057))
* FCM 토큰 업서트 API 경로 수정 ([1565b23](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1565b2367f4995f625fc9759557a7d5043fe0fe1))
* Firebase 설정을 프로덕션 환경으로 업데이트 [#1218](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1218) ([#1219](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1219)) ([9d0de09](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9d0de09acffbfda7ce092550681a43e6a90e04f8))
* id 유효성 검사 API 엔드포인트 수정 [#1308](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1308) ([e61518f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e61518fd25a07159a5e9a311fa0f07d652890382))
* maskable 아이콘 추가로 홈 화면 아이콘 표시 이슈 해결 [#1235](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1235) ([#1237](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1237)) ([3d2b516](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3d2b5166932577d287f44faa043bc3e7f0043c8a))
* MemberRepository 메서드명 수정 ([51ec369](https://github.com/woowacourse-teams/2025-Fit-toring/commit/51ec36963be756a2a31808cf5ee480ac3ad7c06a))
* 서비스 워커 등록 로직 수정 및 개발 환경에서의 클린업 조건 개선 [#1206](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1206) ([a603610](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a6036108de2e095e7898a4efa4906cba15c74d26))
* 성별 검사 어노테이션 변경 ([c273c48](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c273c483c8c724ae650c274728347ffce2fd878c))
* 예약 상담내용 Request null 허용으로 수정 ([f31c5ed](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f31c5ed76e046d2c52e4d6ea65387bba58c0ee47))
* 전화번호 관련 변수명 변경 및 관련 로직 수정 ([d01d633](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d01d6334ff1fe9faf05b68c9ba417669201c7a60))
* 전화번호 필드 이름을 phone에서 phoneNumber로 변경 ([ff55a2f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ff55a2fa55581a4b601c422c750e115a610ae5cb))
* 채팅방 말풍선 위치 오류 해결 ([#1222](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1222)) ([fc6eecf](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fc6eecf2d6d0569f1ee38de7b2e54917ad3586fa))
* 채팅방 서비스 데이터 무결성 예외 처리 개선 ([452ee54](https://github.com/woowacourse-teams/2025-Fit-toring/commit/452ee5463ee3383e890c13c333386dedca4adb00))
* 카카오 로그인 및 회원가입 로직 리팩토링 ([a18efca](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a18efca634c73e9cbcdad0606d0b201d4bb8309f))
* 카카오 회원가입 로직 수정 ([9ed40eb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9ed40eb125fe3654fe2ff34e23bfb962db4dad09))
* 항상 서비스 워커를 등록하도록 수정 [#1206](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1206) ([a65e6c1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a65e6c105e31bbd31d7757556bbf1538f7fd75be))


### Features

* application-test.yml client base-url 로컬 URL로 변경 ([56bc778](https://github.com/woowacourse-teams/2025-Fit-toring/commit/56bc7789127a9dc74e9ed96de639ce8e9224ea21))
* application-test.yml 설정 업데이트 ([7fbbc70](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7fbbc70e9d50847cac3818cf01d9d27074b2dec9))
* FCM 설정에 프로파일 추가 ([bf89b14](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bf89b141eaac0abc98fb8192a30d28f710b7defa))
* FCM 알림 전송 기능 추가 ([533e3a1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/533e3a179daa1a5730c19f4760acd4833aa92ce0))
* FCM 토큰 활성화 여부 필드 추가 ([7bbe4c4](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7bbe4c49e77f87b21801101dab6bd946af9be3be))
* getChatRooms API 함수 생성 [#1304](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1304) ([e2a0800](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e2a0800866556f4f335ba488d572ad3c7075493e))
* getChatRooms API에 withCredentials 옵션 추가 ([08612c9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/08612c9dd4db23df54bc42252126b95a0078dcdf))
* oauth 회원가입 테스트 추가 ([736a77b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/736a77be6dbb47c79e5e7282462d7d4a85d8e1ab))
* PhoneNumberValidator에 null 허용하도록 변경 ([28686c8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/28686c898b908af09315299b5c5344ffacdfed37))
* response_type 추가 ([10474f7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/10474f79d5f2ff534f9ae00517e4692f34b59222))
* SockJS 로그 추가 ([#1280](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1280)) ([7da17b9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7da17b97673be987528005af56eb2d229b6acff2))
* 개발 환경에서 PWA 푸시 알림을 위한 InjectManifest 플러그인 추가 [#1204](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1204) ([cb0e42b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cb0e42b3724ee89f8bc1e71bc881663be8c6d4d4))
* 관리자 기기 관리 화면 추가 ([6875bb5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6875bb53f948621d5349d122bfb45720cb4b6398))
* 관리자 디바이스 관리 API 추가 ([d295fdb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d295fdb3c7c4adea90f6a321feaced188b2c86aa))
* 관리자 디바이스 관리 기능 추가 ([cf1fdfb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cf1fdfbc712cdbc8b7ad5904ab4e76a067294c08))
* 관리자 디바이스 통합 테스트 추가 ([19933ac](https://github.com/woowacourse-teams/2025-Fit-toring/commit/19933ac53b5c166a37d5a02144e35ecb8859b5ac))
* 관리자 디바이스 통합 테스트 추가 ([a9767a2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a9767a22f30d3acd6e5698ffa14bfbd7fb85399a))
* 관리자용 사용자 목록 조회 연결 ([#1126](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1126)) ([ed292bc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ed292bcbae84aa02b69d4b842d6282dc27e4d34d))
* 디바이스 관리 및 FCM 토큰 업서트 로직 리팩토링 ([66e3e23](https://github.com/woowacourse-teams/2025-Fit-toring/commit/66e3e235058a4f65c961ababd81234f38d5b553b))
* 디바이스 등록 개수 제한 및 관련 예외 처리 로직 추가 ([5a57657](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5a57657435a87926ee56fe0ca767e20a06fa8ac8))
* 디바이스 등록 및 중복 예외 처리 로직 추가 ([9844abe](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9844abe3ed9d79050d50a82ff18fcb4a31004069))
* 디바이스 등록 요청 유효성 검증 추가 및 테스트 코드 수정 ([460ab72](https://github.com/woowacourse-teams/2025-Fit-toring/commit/460ab72bac715107cc4a27f00a6b8f22c4652a31))
* 디바이스 등록 중복 예외 처리 및 데이터베이스 제약조건 추가 ([9869e38](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9869e38a0e8fa79e5627c7387263c9dd4c6d2437))
* 디바이스 복합 유니크 키 추가(member_id, push_token) ([c16d37b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c16d37b0cbe46f63e9804c7ce65adb6028559930))
* 로그인 상태 확인 및 쿠키 기반 멤버 ID 추출 기능 추가 ([dfabef3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/dfabef31f9e3c051955a809428b63f49073799c3))
* 로그인 아이디 찾기 기능 추가 및 관련 API 수정 ([0012d17](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0012d17b7b12f5a833888db7d466e02ad3e606ba))
* 리프레시 토큰을 담는 쿠키 발급시 지속시간 설정 ([#1122](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1122)) ([fcc9c83](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fcc9c83bd17ea617cb9826ccfb7e70950f5d0891))
* 멤버 및 채팅방 서비스 조회 메서드에 트랜잭션 추가 ([090a5df](https://github.com/woowacourse-teams/2025-Fit-toring/commit/090a5dfb9c7e182ac997691fee19e7bb92bdedd6))
* 비밀번호 초기화 API 추가 및 관련 로직 구현 ([ad15501](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ad15501cb4b4f1093be0bfcb9762bbceedbb927c))
* 예약 서비스 조회 메서드에 트랜잭션 추가 ([752e584](https://github.com/woowacourse-teams/2025-Fit-toring/commit/752e5848cfe57c9ca754fcfaf49a4bd96301003b))
* 예약 승인 API 추가 ([#1181](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1181)) ([cee118e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cee118ebefed281073fc6577ccac282d920ee951))
* 요청 클래스 valid 메세지 추가 ([625c23e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/625c23e3cc477183c86e2e2a0b42e221fe092d29))
* 전화번호 인증 로직 및 회원가입 테스트 개선 ([1b9eabc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1b9eabca8d503977913f4dca8c4caea9eb868493))
* 채팅 메시지 등록 시 알림 전송 기능 추가 ([a5df942](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a5df9422ec28d5e1e758d207918de6791bc7a826))
* 채팅방 관련 서비스 및 로직 리팩토링 ([e663b0c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e663b0c8724cd44ac91d45d0238fe8ed627de5ce))
* 채팅방 관련 서비스 및 메서드 리팩토링 ([e5734ca](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e5734ca413ff53794d48f92923dfafb0ff5d24d9))
* 채팅방 목록 API 연결 [#1304](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1304) ([7295b07](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7295b07b256a277891a276bceaab7fac45b6efce))
* 채팅방 목록 조회 useQuery에 401 예외 retry 로직 추가 ([6534b68](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6534b68da08a1e0d2b87729ee6de4def7e6e8c57))
* 채팅방 목록 클릭 시 네비게이션 기능 추가 [#1310](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1310) ([4a75b2f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4a75b2f7d17fe2dda97c61cbdd52ed83a07ff79b))
* 채팅방 목록 타입 생성 [#1304](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1304) ([fb5fde9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fb5fde9dda4ba8be7474e39bedbaaa766070c66f))
* 채팅방 목록 프리뷰 조회 API 및 N+1 문제 해결 ([680c30f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/680c30fc316f0ee88a73c3807d7811b510b52822))
* 채팅방 및 메시지 테스트 유틸 추가 및 ChatRoomPreviewResponse 필드 개선 ([df47480](https://github.com/woowacourse-teams/2025-Fit-toring/commit/df47480da9cea8099f5beb323d4bfd49da57c722))
* 채팅방 서비스 및 매핑 로직 리팩토링 ([6bc51b4](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6bc51b4cede8957add9208fa9f90f8c2f25a7733))
* 카카오 로그인 state 토큰 도입 및 리다이렉트 로직 개선 ([19fda31](https://github.com/woowacourse-teams/2025-Fit-toring/commit/19fda3197d5d2110d88ebccc92f21cfe72b6209c))
* 카카오 로그인 리다이렉트 URL 처리 로직 수정 ([c0486a6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c0486a63644420d34b9f2106a51a13d2c83075bb))
* 카카오 로그인 및 콜백 로직 개선 ([5a53818](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5a53818e2509e49eaa2dc8317c19f17b355128c3))
* 카카오 로그인 및 콜백 로직 개선 ([5b5386b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5b5386bd4d42bf2cf2a22b761e062b2b7b1fe2f3))
* 프로필 이미지 로딩 로직 개선 및 기본 이미지 추가 ([615a274](https://github.com/woowacourse-teams/2025-Fit-toring/commit/615a274a7c19a7623a748f1dcc9e513dfa553ff6))
* 회원 FCM 토큰 업서트 기능 추가 ([69fdf11](https://github.com/woowacourse-teams/2025-Fit-toring/commit/69fdf11bcb686f072ca6e54e9ac910411e8fa1fc))
* 회원 FCM 토큰 테이블 및 엔티티 추가 ([f5e9e55](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f5e9e55adfe5953dd0d0ada084435079333dd357))
* 휴대폰 인증 상태 검증 로직 추가 및 관련 코드 수정 ([411c3fd](https://github.com/woowacourse-teams/2025-Fit-toring/commit/411c3fdeb6bf23e9a14b504e12921c23d26b5185))

# [1.14.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.13.0...v1.14.0) (2025-11-24)


### Features

* S3 버킷 이름 변경 ([#1106](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1106)) ([6866e9b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6866e9bd7192c31bb7955f69c6fbb0ba3d6c256c))

# [1.13.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.12.0...v1.13.0) (2025-10-23)


### Bug Fixes

* 204 redirect 시 memberId 로컬스토리지에 추가 [#1002](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1002) ([110552d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/110552dfa177cfb785bda23b641ba8d0d55da4f9))
* 204일때는 response body 를 받지 않기 때문에 200 일때만 추가 [#1006](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1006) ([#1007](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1007)) ([043409c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/043409c5ee7725119196850154803e811866cf09))
* api 명세 변경에 따른 변수명 변경 [#939](https://github.com/woowacourse-teams/2025-Fit-toring/issues/939) ([e36eb8a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e36eb8ad13de5478b6344851cc88355ca04699c1))
* authCode가 두번 보내지던 에러 수정 [#890](https://github.com/woowacourse-teams/2025-Fit-toring/issues/890) ([9d1347b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9d1347b13942f409b43bd32a49a18c82bb4d4e88))
* aws accessKey 제거 ([f2199c0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f2199c00320f74a8dd8341549e9790a321722928))
* chatUrl 스토리북 제거 [#944](https://github.com/woowacourse-teams/2025-Fit-toring/issues/944) ([21380b8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/21380b89d1dc4e241c6c1b93175072898e09c1a9))
* fetchNextPage가 옵저버 위치 인식시에 여러번 호출되던 문제 해결 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([c29a371](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c29a3713421273b09de6b260417cc884e1249863))
* import 파일 경로 변경 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([3f9bf07](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3f9bf07114f2eaa323819c302189070916dd6098))
* Kakao OAuth 설정값 수정 및 @Transactional 어노테이션 제거 ([c382e49](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c382e49efb10a305a02d1f63edb0c1618af041b3))
* KeyBuilder 검증 추가 ([31dfb92](https://github.com/woowacourse-teams/2025-Fit-toring/commit/31dfb92046c3269fb43ab7fa5d4506c240d0c3ab))
* map의 key값을 고유한 값으로 변경 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([7acb71e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7acb71e23b4c1b140a667534bdd2fbae037b7459))
* MentoringStatistics 엔티티 @SQLDelete 테이블명 수정 ([773948d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/773948dd7207234e57a9decb96ed2d06a61afd42))
* OAuth 회원가입 시 기존 쿠키 삭제 로직 추가 ([976397e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/976397e6a57dd0b8dcdd62bbac254234c6855093))
* OAuth 회원가입 응답 상태 코드 수정 ([ffeafb2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ffeafb2518d2920d9459d601c85b8c94ba74f0cb))
* presigned-url 만료 시간 축소 ([8167309](https://github.com/woowacourse-teams/2025-Fit-toring/commit/81673091eb8640833d4ec9e30488245fc70326e7))
* properties 기반 설정으로 수정 ([a40e7ad](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a40e7ada30b0faa65d6963ffdef248a68f1627d2))
* Ref 를 추가해 중복 실행 방지 [#890](https://github.com/woowacourse-teams/2025-Fit-toring/issues/890) ([f1ea367](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f1ea367d2fd2ddcfa301e2ff00754add267f6e2e))
* tempId 기반 메시지 업데이트 시 status 'success' 적용 및 새 메시지 처리 개선 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([aa4d543](https://github.com/woowacourse-teams/2025-Fit-toring/commit/aa4d5434b00e9512f402b60ade2326d55344e239))
* useEffect랑 useLayoutEffect 바뀐 부분 수정 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([b46253b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b46253bf19ddf1e6c1d53cdcfef3ef12aa63c381))
* useNavigationType을 사용하여 히스토리가 없을 시 홈으로 이동 [#973](https://github.com/woowacourse-teams/2025-Fit-toring/issues/973) ([638a80a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/638a80a71fd4bdc8147cb3bee2f696dfb91a1e82))
* webpack 설정에서 vendor-else 캐시 그룹의 청크 옵션을 'initial'로 변경하여 초기에 필요한 청크들만 만들기 [#797](https://github.com/woowacourse-teams/2025-Fit-toring/issues/797) ([79c066e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/79c066e6afea199b08117c638b45479f18589521))
* webpack.prod 경로 추가 설정 ([5095065](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5095065c5f2a00b41b418ac91076e17bde77575e))
* WebSocket 연결 URL을 로컬에서 API_BASE_URL로 수정 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([88bb525](https://github.com/woowacourse-teams/2025-Fit-toring/commit/88bb525c440881f340af204a1b1d9e60544b5b54))
* 가격 입력 처리 개선 및 NaN 체크 추가 [#999](https://github.com/woowacourse-teams/2025-Fit-toring/issues/999) ([9a3ef91](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9a3ef91afe9d1df151fda9767bbb580629e64762))
* 멘토링 등록 성공 시 홈으로 네비게이션 [#975](https://github.com/woowacourse-teams/2025-Fit-toring/issues/975) ([#976](https://github.com/woowacourse-teams/2025-Fit-toring/issues/976)) ([4b2f128](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4b2f128bcf3ff804a0092579add9cb0ea4bf66a5))
* 멘토링 목록 조회 시 이미지 썸네일으로 불러오도록 수정 ([#980](https://github.com/woowacourse-teams/2025-Fit-toring/issues/980)) ([1f6994d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1f6994d80c1131c8050a9d2f14535960beb555bf))
* 멘토링 수정 로직 api 명세 변경에 따른 수정 [#939](https://github.com/woowacourse-teams/2025-Fit-toring/issues/939) ([15b777f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/15b777fff50565442b7df66fd59a073c65d3ee1d))
* 무한스크롤로 데이터 추가될때 스크롤 아래로 이동하는 문제 해결 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([12ba497](https://github.com/woowacourse-teams/2025-Fit-toring/commit/12ba497aacc7724c5122e51d5f9ed21022d056f3))
* 불필요한 의존성 배열로 인한 버그 수정 ([1fe60e8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1fe60e835c3e0262e5c7cdfac706841901816913))
* 서명을 한 번만 하도록 수정하여 만료시간 일치 ([5b6b34e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5b6b34e9a5488f6997a97b097692ed3e8a65a7c0))
* 설정 프로퍼티 검증 추가 ([425d99d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/425d99d9c0132accc0b3852a748f43f36d77413d))
* 완료상태에도 채팅방에 들어갈 수 있도록 변경 [#986](https://github.com/woowacourse-teams/2025-Fit-toring/issues/986) ([b7aacc5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b7aacc5b17a37543496ea4f9321e16f1f9a7272d))
* 위로 스크롤 시 IntersectionObserver 중복 트리거로 인한 연속 페이징 문제 해결 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([80e4eaf](https://github.com/woowacourse-teams/2025-Fit-toring/commit/80e4eaff41bcc57fe6adf246d6c97b85407a478b))
* 유효성 검사 어노테이션 수정 ([e5d790a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e5d790adca0bb9dc0d30b4deae2eee21eb53fb3c))
* 이전 채팅 기록 불러오는 API에 credentials 옵션 추가 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([c70b46c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c70b46cb8c01afde2643f96997964da52821c057))
* 잘못 제거된 handleMessageSubmit 함수 다시 복구 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([4f57b88](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4f57b88873ad226b0dcac7d5f5c432da45b01f6a))
* 채팅 인풋 빈값일때 빈 메시지가 보내지는 문제 해결 [#983](https://github.com/woowacourse-teams/2025-Fit-toring/issues/983) ([8c9fb39](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8c9fb39b7ea043130458a11ea8c50b91eaeff0c9))
* 채팅방 NPE 문제 해결 ([#1013](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1013)) ([3248f14](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3248f1456ce2585984d9b5a2d7d611777bdd9023))
* 채팅에 관한 API endpoint 오타 수정 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([6de0a1f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6de0a1ff9cf5839fbce74e0fc277d4e1e1f10ae1))
* 카카오 OAuth 로그인 redirectUrl 제거 및 동적 처리 개선 ([6f6edfe](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6f6edfeb3efd751c5bf6417a18b2c840dd7dcf30))
* 카카오 OAuth 콜백 메서드 HTTP 메서드 수정 ([c87bea4](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c87bea465f1a6e6c7c294439e2b67261f915549a))
* 카카오 OAuth 콜백 메서드 파라미터 수정 및 state 검증 주석 추가 ([b156663](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b156663e75a1fd6ed913367cfcd13c27bbcd41d7))
* 카카오 오픈채팅 입력 제거 [#944](https://github.com/woowacourse-teams/2025-Fit-toring/issues/944) ([d24e3c5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d24e3c539aad94ab3e1f52442f4d14dddc93b689))
* 테스트 데이터 및 AuthService 테스트 코드 수정 ([ce8466c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ce8466c2a52fd62f700e2db3fe487a402a7d9ff3))
* 테스트 코드 sql문 수정 ([84d7a6f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/84d7a6fdde6252e87cdf3c1dd40ffd1faca01055))


### Features

* `member_oauth` 테이블 컬럼 수정 ([2c7f65f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2c7f65fa54387e47baff66dc8d751df909a69149))
* 20,000자 이상 입력 제한 기능 추가 [#983](https://github.com/woowacourse-teams/2025-Fit-toring/issues/983) ([37c56e6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/37c56e6bba1a161ea77b8507ae90682d7b97e960))
* actuator 엔드포인트 노출 설정 수정 ([290cc8e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/290cc8e2d9362a5d574dbc0c91a56d87642c65b0))
* AdminMember 조회 테스트 반환 타입 변경 및 검증 로직 수정 ([3437ed5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3437ed56a3c8c1e581f1a5b0ab8796f083b0141c))
* AdminMemberService 분리 및 관련 컨트롤러 적용 ([1517386](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1517386bdfbdd60fd33e21d09a99d042a1f7ca1e))
* AdminMemberService 페이징 조회 테스트 및 로직 개선 ([fd2ba72](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fd2ba72a0382561bc418f634af5fe49f36fbcf2c))
* API_ENDPOINTS에 채팅 api url 추가 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([4b86482](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4b864827c429e56387c8288a4fdd0f2ae51bb388))
* AuthService에 @Transactional 추가 및 OAuth 관련 메서드 리팩토링 ([57bd475](https://github.com/woowacourse-teams/2025-Fit-toring/commit/57bd4753df1d9bd78084692dfb8157624d0e3e6d))
* AWS SQS 비활성화 설정 추가 ([dab068c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/dab068c5fbc57033d1c90067e90377b6b24d28c9))
* AWS SQS 비활성화 설정 추가 ([c0b0abc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c0b0abcac8d12f06ca5a8337a9153c259d5a91c4))
* cacheControl 설정 추가 ([f298684](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f2986845d47080e1dff3a8b38d7f564b38a1d4e9))
* Chat 컴포넌트 생성 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([95cdda8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/95cdda8ce0fc3b54b35f4c8fc453616e0f75f102))
* ChatBubble 컴포넌트 생성 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([ee9282d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ee9282dae6752c0e50ff881d342cda1faabed50e))
* ChatRoom 페이지 생성된 컴포넌트 적용 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([c771b0e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c771b0e8dc1829dd858733c3c7086030971464f3))
* content-type 제한 설정 추가 ([4f83e74](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4f83e74e3c435ccd33528e539f51338c49063b5e))
* CustomMentoringRepository 패키지 원상복구 ([a83f396](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a83f396230e730c23952e786b2f5a72eddb81295))
* getChatRoomInfo API MSW 모킹 구현 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([9634da5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9634da5961171c5640d7a8fcf2239640e197539e))
* getChatRoomInfo 함수 생성 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([c6f940b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c6f940b18f78a69f0701901bf57825ab4bd34b47))
* getChatRooms API MSW 모킹 구현 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([c8b3df6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c8b3df6528b9f656f675a8736185da13e8c11199))
* getChatRooms 함수 생성 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([47b97a8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/47b97a85b62a82759777cb18689e18b0c09edbc0))
* InputSection 컴포넌트 생성 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([a147d46](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a147d46d2ab2c2a428628785f393522b8c47d2c2))
* kakaoLogin API 연결 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([88aecb7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/88aecb722de04c47e639177440284738dea88c02))
* local 환경 변수 및 webpack 추가 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([47e46d5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/47e46d5ccce35717045cec5cc92a5504743d830a))
* member_oauth 테이블 소프트 삭제 컬럼 추가 ([0e04306](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0e0430681d0e0c2e7452b29ae5684af6c1ca97e9))
* memberId없는 경우 로그인 필요 UI 렌더 [#957](https://github.com/woowacourse-teams/2025-Fit-toring/issues/957) ([b8a407b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b8a407ba41f59602917dc5de52f57d0340d7be19))
* Mentoring 통계 저장 기능 추가 ([6a5cbfd](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6a5cbfdd783154c648aea6cbba55c3eee292aae7))
* MentoringActionPanel 컴포넌트 생성 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([e24ec57](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e24ec57c1bfd7d5ef31e850ff8061f9246ebe46a))
* MentoringStatistics 엔티티 및 DDL 생성 ([1779342](https://github.com/woowacourse-teams/2025-Fit-toring/commit/177934272a57d6c25ed4e31b7ecefc1bf05f6f5e))
* MentoringStatistics에 소프트 삭제 관련 필드 추가 ([0eb261f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0eb261fcb8f5df8ab2ee13a820e2569e19015693))
* OAuth 회원 관리 기능 초기 구현 ([d2cdc7b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d2cdc7b878469ad971979b176bcbaf479b41ce42))
* OAuth 회원가입 API oauthSignUpToken 쿠키 처리로 변경 ([1345db6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1345db65bfa48369699d49bb7337124633e5f054))
* OAuth 회원가입용 토큰 생성 메서드 추가 ([209559b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/209559ba18af3897826e46190f48c7ac372fb11c))
* PAGE_URL에 채팅방 페이지 url 추가 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([a654d1b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a654d1b08f81064fd784d6119e2b0242e158c971))
* presignedUrl 발급 로직 구현 ([b9081eb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b9081ebc4ee92d8e847a11656e16741809886c72))
* presignedUrl 발급 컨트롤러 추가 ([35b7260](https://github.com/woowacourse-teams/2025-Fit-toring/commit/35b72602cf545b5b2a0c0056a9448c1f6784a208))
* prometheus metrics 엔드포인트 추가 ([#865](https://github.com/woowacourse-teams/2025-Fit-toring/issues/865)) ([94252c9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/94252c9321ae6ad2d7fca803c07b615485b3a7e4))
* Prometheus 관련 의존성 및 설정 추가 ([#854](https://github.com/woowacourse-teams/2025-Fit-toring/issues/854)) ([bf628a6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bf628a69ee17ff72b1879cd80989059a7255ff46))
* prometheus 및 hikari 설정 추가 ([e8d5d99](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e8d5d996c268e474aec410557016b25c9e0a31ce))
* RestClient 구성 및 서비스 DI 리팩토링 ([0083318](https://github.com/woowacourse-teams/2025-Fit-toring/commit/00833188cdbf6074753cd5128be85e505a3eec5b))
* S3 관련 프로퍼티 설정 ([46f34b5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/46f34b5948786d31a3a3043d1afcf1f0e06dee2a))
* S3Config 추가 ([4dc8e93](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4dc8e935519912c522db7faf3112ca977075922e))
* S3Config에 Presigner 추가 ([37854d5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/37854d520324e71079ace7734210aad9beaea5b7))
* SockJS Options에 withCredentials 타입 추가 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([9d5d67a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9d5d67a0d54b23fd9e152d547cac0fdf120c503e))
* tanstack 프로바이더 추가 [#818](https://github.com/woowacourse-teams/2025-Fit-toring/issues/818) ([03ddf1f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/03ddf1fe25f16727c1def95361e1484cac907407))
* webpack 설정에 캐시 그룹 추가하여 의존성 최적화 [#797](https://github.com/woowacourse-teams/2025-Fit-toring/issues/797) ([45651ff](https://github.com/woowacourse-teams/2025-Fit-toring/commit/45651fffa4e8e80e34d25ae436ada900aafc0619))
* 개설한 멘토링에 완료 버튼 추가 및 상태 업데이트 기능 구현 [#1009](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1009) ([#1010](https://github.com/woowacourse-teams/2025-Fit-toring/issues/1010)) ([76fd85c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/76fd85cb68cf8fbc5c09330481723f0f87d0afc8))
* 관리자 멘토링 삭제 시 멘토링 통계 레코드도 삭제되도록 하는 기능 구현 ([d06aeb9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d06aeb9a07405edd1ac8766b805310dc7524763f))
* 관리자 회원 페이징 조회 기능 추가 ([897ad70](https://github.com/woowacourse-teams/2025-Fit-toring/commit/897ad70dbc57c45247256e6ea41a9ed6f5486944))
* 관리자가 예약 삭제 시 멘토링 통계를 업데이트하는 기능 구현 ([ca412a2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ca412a25618f701ecde71702aa6af61d0417a7e8))
* 로그인 시 memberId 로컬 스토리지에 저장 [#967](https://github.com/woowacourse-teams/2025-Fit-toring/issues/967) ([56341b5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/56341b55c4f164dfe33652f87220f02f3ead5bc5))
* 로그인 요청에 Sentry 추가 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([e068e6c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e068e6c91de66218864ad300a640a18271d6c8f3))
* 로그인 응답 DTO 및 관련 로직 리팩토링 ([4bb5ae0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4bb5ae0be7e122b097cf0c9b5c56b7a094b9738c))
* 리뷰 생성 및 삭제 시 멘토링 통계 정보를 업데이트하는 기능 구현 ([2c3bee3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2c3bee3a949978fbe1871ad4276c57be4bfe4829))
* 리뷰 페이지 aria-label 추가 [#929](https://github.com/woowacourse-teams/2025-Fit-toring/issues/929) ([7c94067](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7c9406760cee081d17626b35d3eaacfc5c3c9e1f))
* 마지막 페이지 도달 시 추가 fetch 방지 로직 추가 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([5b24b39](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5b24b3940d402de9ab3000d25ff9a242c621f34b))
* 메시지 추가 시 자동으로 스크롤 맨 아래로 이동하는 기능 구현 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([e650b02](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e650b02a3a3f7fd776ed093f533f6b68bb1e3c7f))
* 멘토 채팅 진입점 추가 [#986](https://github.com/woowacourse-teams/2025-Fit-toring/issues/986) ([d861988](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d8619888c9a0d7a9d545238aabb4d35ddbed7368))
* 멘토링 예약 응답에 프로필 이미지 조회 로직 추가 ([#873](https://github.com/woowacourse-teams/2025-Fit-toring/issues/873)) ([a15a7f1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a15a7f1f556c00ff66cd30438caf796eb8db4910))
* 멘토링 통계 평균 별점 저장 및 쿼리 최적화 ([e7b9c99](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e7b9c99e2d1d864acaca8516e19f595acc734e15))
* 멘토링 페이지 조회 시 예약 개수 내림차순 기능 구현 ([6366e8b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6366e8b81f13a6e41ea7a17302bbf7a317afaa99))
* 멘티 채팅 진입점 추가 [#986](https://github.com/woowacourse-teams/2025-Fit-toring/issues/986) ([fa077d2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fa077d267724b55f9164c377a4431d50797986fb))
* 멘티에게 전송될 sms 메세지 인자에 채팅방 URL 추가 ([c0c1ba2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c0c1ba2e3432db237ffe4281e7dd33ed5b274c0d))
* 모니터링 위해 actuator 설정 추가 ([e8ffd0c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e8ffd0ccbadaefc4993a8694cb1c93554f9542c6))
* 별점 aria-label 추가 [#929](https://github.com/woowacourse-teams/2025-Fit-toring/issues/929) ([d562658](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d56265890ac7bf858e1dbaf9738c0b1cb7b73bc5))
* 본인 인증 API 연결 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([050187f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/050187fce78354dc74dd32125b668243305d90b9))
* 본인 인증 페이지 ui 구현 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([66ad568](https://github.com/woowacourse-teams/2025-Fit-toring/commit/66ad568221decbc7e6d9a628b27f96214339f40d))
* 본인 인증 페이지 추가 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([cae09f6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cae09f6bc1f6191d70f62cc03e677e01fc5e0417))
* 상수명 REDIRECT_URL 로 변경 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([95899c0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/95899c06be3c7af985c0223328d4317b54b34b75))
* 시간 문자열을 '오전/오후 h시 mm분' 형식으로 변환하는 유틸 함수 추가 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([2614673](https://github.com/woowacourse-teams/2025-Fit-toring/commit/261467368650fd47eeffa3c8c5e007cf817b03cb))
* 신청하기 버튼 바로가기를 제공해 접근성 개선 [#929](https://github.com/woowacourse-teams/2025-Fit-toring/issues/929) ([be8d14f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/be8d14f1e7a351e04dd22dee284d0630e6e090ce))
* 예약 생성 시 멘토링 통계를 업데이트하는 기능 구현 ([8cf92b3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8cf92b36131213ed537914709137106aa3845d40))
* 예약 수 cursorCondition 추가 ([5e33f16](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5e33f165c1912bbe802f4a47c954b04a85ddf6f9))
* 예약 수 SortKey 추가 ([3c0be53](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3c0be5348c1ad9946b8b930646f36f58ad57a027))
* 예약 승인 시 채팅방 생성 로직 추가 ([0b2fcdb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0b2fcdb7322374a2b1ad66a6e776eb0e9220efe7))
* 예약 시간 순 정렬 지원 및 테스트 케이스 개선 ([d62bf0e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d62bf0ec3069f30e790b8527aaaa17d78b11e814))
* 예약 정렬 기준 개선 ([dcee98c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/dcee98c44998409f63c2603a7a2aa8160f8e8c92))
* 의존성 배열 추가 [#890](https://github.com/woowacourse-teams/2025-Fit-toring/issues/890) ([e05f754](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e05f7548c094536f031b8e9611d7770ad8941e07))
* 이미지 확장자 상수 관리 ([999b318](https://github.com/woowacourse-teams/2025-Fit-toring/commit/999b3184757bea279d0dffc5d858fe87fcab7942))
* 이전 채팅 기록 불러오기 기능 구현 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([d9e6c36](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d9e6c36c1affe5ebbef7d01839299a30ff1144dc))
* 인가 코드 전달 시 redirectUrl 도 같이 전달 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([14c0e37](https://github.com/woowacourse-teams/2025-Fit-toring/commit/14c0e37bb733faa50ebeca1dcb36e2f1d14084bb))
* 자격증 보러가기 버튼 활성화하여 접근성 개선 [#929](https://github.com/woowacourse-teams/2025-Fit-toring/issues/929) ([df5d7e6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/df5d7e68edfa901174c04f2422e209aaed920d1c))
* 자격증 캐로셀 aria-live 추가 [#929](https://github.com/woowacourse-teams/2025-Fit-toring/issues/929) ([0abddff](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0abddffd629ba85c09f32c7c229a0f0778b2885d))
* 전송 실패시 감지하는 로직 추가 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([958ecd1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/958ecd1c853ccfb6873f95f1b0ca92ff1edfee74))
* 전송실패 UI 구현 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([cbeb00a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cbeb00aec7dcba4862e459f9e36c810528c81c22))
* 중복된 버튼이 있을 시 alert 후 로그인 페이지로 이동 [#967](https://github.com/woowacourse-teams/2025-Fit-toring/issues/967) ([b7fb967](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b7fb967a54fe28219d15b7c0a5310689cce7a65e))
* 채팅방 URL 생성 유틸 추가 ([ec9e0c1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ec9e0c1568e2269eff13a2a3c45de83206484984))
* 채팅방 생성 service 로직 구현 ([b48da68](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b48da686c09476e220e96c9675ac88254b02ebf3))
* 채팅방(ChatRoom) 엔티티 및 스키마 추가 ([24e394c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/24e394cbc9203403ca056bd256168db5f42abe8e))
* 채팅방페이지 Header 컴포넌트 생성 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([f74b38f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f74b38f306128e82160dc5a9a1704a390116ef41))
* 채팅방페이지 라우트 추가 [#813](https://github.com/woowacourse-teams/2025-Fit-toring/issues/813) ([08570fc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/08570fc72f39a6bddcb781d5b8dc71005ae0296a))
* 카카오 OAuth 로그인 redirectUrl 동적으로 처리 ([16e9268](https://github.com/woowacourse-teams/2025-Fit-toring/commit/16e9268e8d86eeea0f2be00370730b8718620d26))
* 카카오 OAuth 로그인 및 회원가입 기능 개선 ([47f4d13](https://github.com/woowacourse-teams/2025-Fit-toring/commit/47f4d13f2def7b5b55e901453411b191ad15521f))
* 카카오 OAuth 로그인 및 회원가입 기능 추가 ([7e00d6e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7e00d6ef2864672d61c2be0d949edcbce3173cae))
* 카카오 OAuth 설정값 테스트 프로필에 추가 ([889ddf0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/889ddf02850d595e5c5cd429d1b787e323d38b20))
* 카카오 OAuth 콜백 요청 DTO 적용 및 리팩토링 ([adcad6f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/adcad6fe9804a222896c00aaa8ef1ff66c58e428))
* 카카오 OAuth 콜백 처리 개선 및 쿠키 로직 수정 ([da0dd4d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/da0dd4ddaaf93173bf150fba590a46009e848e97))
* 카카오 로그인 OauthClientService 및 환경 변수 설정 추가 ([0999eec](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0999eecaff8e40fe502a001ea4a3994c82075bf9))
* 카카오 로그인 로직에 OauthClientService 추가 ([6991ae3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6991ae3618a1bb37fc3528b720d3fc77795ddad4))
* 카카오 로그인 버튼 클릭 시 로그인 창으로 이동 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([017cf35](https://github.com/woowacourse-teams/2025-Fit-toring/commit/017cf3544afe039f50c39bc304f73171fbce9092))
* 카카오 로그인 콜백 엔드포인트 추가 ([d7cd88d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d7cd88d338078df43da89f874bd996d1867c6884))
* 카카오 콜백 컴포넌트 생성 [#800](https://github.com/woowacourse-teams/2025-Fit-toring/issues/800) ([e2b753c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e2b753c17050dc45825c6db2a8d0e4c3e6ad5018))
* 카테고리(전문분야) 선택 시 필터링된 멘토 목록 불러오기 기능 추가 [#776](https://github.com/woowacourse-teams/2025-Fit-toring/issues/776) ([e24e791](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e24e7917f9beba42dcbd2e6152eb81547e94b519))
* 평균 평점 기준 정렬 기능 추가 ([f58372a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f58372a931e8bf02dfb2f0e4d656c58759b932a2))
* 포커스 트랩 적용하여 접근성 개선 [#929](https://github.com/woowacourse-teams/2025-Fit-toring/issues/929) ([99bf541](https://github.com/woowacourse-teams/2025-Fit-toring/commit/99bf541af25d52f4c6978523a75b20dddcea90d5))
* 필터링된 멘토 목록을 가져오는 로직 추가 및 관련 useEffect 구현 [#776](https://github.com/woowacourse-teams/2025-Fit-toring/issues/776) ([df568c3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/df568c3a62e2665a95cf61d7e3230e54b3d9ce70))
* 하드코딩한 채팅방 정보 api 응답값으로 변경 [#825](https://github.com/woowacourse-teams/2025-Fit-toring/issues/825) ([34863da](https://github.com/woowacourse-teams/2025-Fit-toring/commit/34863da828253a39cb109e86fb5620f28027076b))
* 환경 변수 기반 채팅방 URL 동적 생성 적용 ([b2cf46f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b2cf46fb87fd2ec6571ee6a33185b1bd82ce5843))
* 회원 중복 검증 로직 추가 ([724b991](https://github.com/woowacourse-teams/2025-Fit-toring/commit/724b9910237b7aba7a14b18419346249b137c20e))

# [1.12.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.11.1...v1.12.0) (2025-09-26)


### Features

* 멘토링 목록 조회 시 카테고리 필터링 기능 추가 ([9cb477c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9cb477cd2b64845b54dedabc14e068b3a969d37c))
* 멘토링 목록 조회 시 카테고리 필터링 테스트 추가 ([c453bc2](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c453bc27ef436e014402243c5204a9d902012858))

## [1.11.1](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.11.0...v1.11.1) (2025-09-26)


### Bug Fixes

* mentorList 업데이트 로직 간소화 [#781](https://github.com/woowacourse-teams/2025-Fit-toring/issues/781) ([f848ff9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f848ff90998c7ecffb412e48a0c0c742bc21a614))

# [1.11.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.10.0...v1.11.0) (2025-09-26)


### Bug Fixes

* customStyle 변수 이름 오타 수정 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([60682e9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/60682e9d905be038bf8f79526111745b3e36c7bc))
* MSW 멘토 정보에 평점, 이미지 필드 수정 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([db71792](https://github.com/woowacourse-teams/2025-Fit-toring/commit/db717926f3177b8f973216baa5a4ed2f2aedfdbc))
* ratingAverage 필드 타입을 number에서 string으로 변경 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([2f08289](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2f0828990824217f05e0b6b2830f8673c4ab878f))
* 마이페이지 넘치던 글 디자인 수정 [#755](https://github.com/woowacourse-teams/2025-Fit-toring/issues/755) ([dba6cef](https://github.com/woowacourse-teams/2025-Fit-toring/commit/dba6cef890140082a5c8f325aa9b43f7a25be9d0))
* 멘토 상세보기 페이지 넘치던 글자 수정 [#755](https://github.com/woowacourse-teams/2025-Fit-toring/issues/755) ([ccc6a2e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ccc6a2e34ce94dc00114d6c29497ec8084cc2350))
* 멘토링 이미지 수정 시 삭제되도록 수정 [#768](https://github.com/woowacourse-teams/2025-Fit-toring/issues/768) ([c40e4a5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c40e4a5acab10715193f48310b5f68086e84adbf))
* 회원가입 폼의 인증요청, 인증하기 버튼에 최소 너비 추가 [#764](https://github.com/woowacourse-teams/2025-Fit-toring/issues/764) ([befba40](https://github.com/woowacourse-teams/2025-Fit-toring/commit/befba40360dd363dbcbb3885964176b74c60ba2c))
* 회원가입 폼의 중복확인 버튼에 최소 너비 추가 [#764](https://github.com/woowacourse-teams/2025-Fit-toring/issues/764) ([5a1e085](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5a1e085fcd85a9d966497985279ed39868ff6185))


### Features

* image 테이블 복합 유니크 키 추가 ([ff36ac5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ff36ac5048fb44e385fa05f47caed482d68b221c))
* IntersectionObserver를 사용하여 멘토 리스트의 무한 스크롤 감지 기능 추가 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([6f9c478](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6f9c478b69a57900630a52b191aea23b3958b190))
* MentorCardItem 컴포넌트를 메모이제이션하여 성능 개선 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([6f762f6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6f762f6a316242eee4fcb20e1cc01c6697ef4352))
* MentoringByPage 인터페이스 추가 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([6002eaf](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6002eafc92a1ce704fe1335fef924bcaa8268d75))
* MENTORINGS_PAGE API 엔드포인트 추가 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([826e5c7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/826e5c759ecbbe4fedbc7ea1deb881f8d25edb4b))
* MSW 멘토 정보 추가 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([6368ea0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6368ea070ced8ce2224a37267f56fc6b8a4fd853))
* rel-dev CI 실패 시 테스트 리포트 업로드 job 추가 ([b4d45a8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b4d45a8af2dec2c0d10beb069215068e1db5225c))
* rel-prod CI 실패 시 테스트 리포트 업로드 job 추가 ([afe35ed](https://github.com/woowacourse-teams/2025-Fit-toring/commit/afe35ede64549ffb0a85a63989c02b95895b9ec9))
* 멘토 리스트 페이징을 위한 API 호출 및 IntersectionObserver 구현 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([ebb7e33](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ebb7e33ff5a488c1ec8c6008a7dc7a138e23d465))
* 무한스크롤을 위한 MENTORINGS_PAGE API 기능 추가 [#731](https://github.com/woowacourse-teams/2025-Fit-toring/issues/731) ([d6b5362](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d6b5362ea8c26db17087c624471efeaa9a154ee0))

# [1.10.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.9.1...v1.10.0) (2025-09-25)


### Bug Fixes

* avif 제거 ([536a2d3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/536a2d3177bff9348ab17390d9b289e121e615fb))
* stderr 쓰레드 분리하여 프로세스 스트림 교착 가능성 줄임 ([1721a4f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1721a4f434ab65f58fa05a78f992dadae91a5592))
* 이미지 확장자 변환 방식 수정 ([b0c94a6](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b0c94a6aa946927a0b7f680b37aa7cc830ad69b5))
* 임시 파일 생성 방식으로 수정 ([77ad824](https://github.com/woowacourse-teams/2025-Fit-toring/commit/77ad824ae313c51adba5cdcc268edeacf9479415))


### Features

* swagger request 옵션 추가 ([d77d76a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d77d76a4430cbadc5bc9f1787f8008effe095ccb))

## [1.9.1](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.9.0...v1.9.1) (2025-09-25)


### Bug Fixes

* 캐싱 디렉토리 dist -> empty_artifacts로 변경 ([c9bf3b5](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c9bf3b5ad3f24894866d3d606d3974a0c6013b79))

# [1.9.0](https://github.com/woowacourse-teams/2025-Fit-toring/compare/v1.8.0...v1.9.0) (2025-09-25)


### Bug Fixes

* '회원 정보' 메뉴 항목 이름을 '회원 정보 수정'으로 변경 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([5ee0e12](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5ee0e1214391fc82bfe10f07e4b39fa1b11c1635))
* ApiClientPatchType 인터페이스의 searchParams를 body로 변경, 제네릭 타입 추가 및 patchReservationStatus 함수 수정 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([1737bc3](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1737bc355063fab4299a13131e2de01df540288c))
* App.tsx에 불필요한 라우팅 관련 TODO 주석 제거 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([75761ae](https://github.com/woowacourse-teams/2025-Fit-toring/commit/75761ae983eb8b4d490ec42a3fbdf2f4c42548cb))
* buildspec.yml에서 S3 경로 출력 메시지 수정 [#730](https://github.com/woowacourse-teams/2025-Fit-toring/issues/730) ([9233549](https://github.com/woowacourse-teams/2025-Fit-toring/commit/92335497c651b942ca40c36be913561963cc91fb))
* buildspec.yml에서 S3 동기화 명령어에서 메타데이터 지시어 제거 [#730](https://github.com/woowacourse-teams/2025-Fit-toring/issues/730) ([2269482](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2269482a9c16e360a02b472e36a2bc9a15bf7efb))
* buildspec.yml에서 S3 배포 명령어에 파이프 추가 [#730](https://github.com/woowacourse-teams/2025-Fit-toring/issues/730) ([9854b88](https://github.com/woowacourse-teams/2025-Fit-toring/commit/9854b8897323aaf256a3b841e5230a92934c6aa3))
* CD 과정에서 캐시 추가 자동화 코드 버그 수정 [#727](https://github.com/woowacourse-teams/2025-Fit-toring/issues/727) ([75d63ee](https://github.com/woowacourse-teams/2025-Fit-toring/commit/75d63ee38a7f1e27f8a08120e9c60e3cac898b70))
* CursorCodec 디코딩 필드 타입 변환 로직 수정 ([cdf94a8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cdf94a8fd48c0a5f44b274bd165766ba515fc898))
* dockerfile에 avif 코덱 추가 ([7774b84](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7774b848a85e7b2dccefc259cd79b4075ab02bac))
* kimdev의 사소한 실수 수정 ([1730b1d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1730b1dd285e4b208a501b4b4b4878de47509999))
* mockServiceWorker.js 파일 public 디렉토리로 옮기기 및 개발 서버 실행 경로를 public으로 수정 [#671](https://github.com/woowacourse-teams/2025-Fit-toring/issues/671) ([4eb5849](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4eb5849f4837057f2788cf3663d4ab6eb329f9e2))
* MSW 핸들러 getCreatedMentoringList에서 불필요한 응답 객체 제거 [#702](https://github.com/woowacourse-teams/2025-Fit-toring/issues/702) ([0e88994](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0e889948a459672f189575ee2434118fbf1d9a0e))
* reservation 엔티티의 content 타입을 TEXT로 수정 ([a7a6583](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a7a6583f9b4347f42bbdc3281e9e26f2bb9ee8ce))
* S_ApplicationContentShowMore에 Button 접미사 붙이기 [#702](https://github.com/woowacourse-teams/2025-Fit-toring/issues/702) ([4ead5de](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4ead5de073ba1812fb7c0e1a0d85cefde874636d))
* tab 추가 [#730](https://github.com/woowacourse-teams/2025-Fit-toring/issues/730) ([4b0eb61](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4b0eb611968708dcc72364d34caa299d27714aef))
* UserInfo 인터페이스에서 gender 타입을 string에서 Gender로 변경 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([c04073a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/c04073a8de349e15aabb4393346a369483ba386c))
* UserInfo 인터페이스에서 loginId 타입을 number에서 string으로 변경 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([77e1d84](https://github.com/woowacourse-teams/2025-Fit-toring/commit/77e1d84e2e4d24c4ae5ff27fe608a67a64f858ef))
* 멘토링 상태 승인대기 -> 승인 대기, 승인됨 -> 승인 확정 으로 변경 [#702](https://github.com/woowacourse-teams/2025-Fit-toring/issues/702) ([d29cdce](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d29cdce1afd11163e3bd0b50c0c0eeda81c1ed47))
* 오타 수정 - passwordConfrimValidated를 passwordConfirmValidated로 변경 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([efdf484](https://github.com/woowacourse-teams/2025-Fit-toring/commit/efdf4843e98e7936b8301d330a49d120cb7026aa))
* 이미지타입 정책 빈 주입 ([89ed853](https://github.com/woowacourse-teams/2025-Fit-toring/commit/89ed853fcd5385abc40cb812cab123e28b5a2b6b))


### Features

* avif 이미지를 추가하여 이미지 크기 최소화 [#721](https://github.com/woowacourse-teams/2025-Fit-toring/issues/721) ([7d333d7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/7d333d75bf84f400cfc64501d385ca19f0256bc1))
* build 실행 시 mockServiceWorker.js 파일이 배포에 포함되지 않도록 copyWebpackPlugin의 무시 목록에 추가 [#671](https://github.com/woowacourse-teams/2025-Fit-toring/issues/671) ([8c98585](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8c98585c938abce1ee4b1048af00a0195b293027))
* buildspec.yml에서 S3 배포 시 캐시 제어 지시어 수정 및 더미 아티팩트 생성 추가 [#739](https://github.com/woowacourse-teams/2025-Fit-toring/issues/739) ([fd3b982](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fd3b9821ac588dd76bcddc1a4f3dc3ef222183ca))
* CreatedMentoring에 필터 버튼 및 기능 추가 [#702](https://github.com/woowacourse-teams/2025-Fit-toring/issues/702) ([e392b3b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e392b3b8c6563302d72e5cae0c5cd461f7b10c75))
* CustomMentoringRepository 구현 추가 ([bf1ec66](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bf1ec665092c1986c4a29f4efa1421263ebbb7b2))
* EDIT_PROFILE 페이지 URL 상수 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([972e9ce](https://github.com/woowacourse-teams/2025-Fit-toring/commit/972e9ce4e41a48cb147434a5b9a878e78d3a6ae8))
* EditProfile 페이지 라우팅 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([a8ecf55](https://github.com/woowacourse-teams/2025-Fit-toring/commit/a8ecf55346b371ccbd1278a3b94609c8da4a95dc))
* EditProfile 페이지 컴포넌트 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([db9ea5e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/db9ea5ed877a19ad37fedb2136a53cf09f369aa5))
* EditProfileForm 컴포넌트에 이름, 성별 입력 필드 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([d6cedd4](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d6cedd4d208c911cd15fffde0f2641aed1d070d4))
* EditProfileForm에 비밀번호 입력 필드 및 유효성 검사 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([268769d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/268769dc2f7ea1a88637eca7f089a35b3c939dd4))
* EditProfileForm에 전화번호 및 인증 코드 입력 필드 추가와 검증 로직 구현 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([eed84ff](https://github.com/woowacourse-teams/2025-Fit-toring/commit/eed84ffa266da8eed673ba1d1ccf13d2d04d46fb))
* EditProfileForm에 폼 유효성 검사 및 제출 버튼 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([3c30604](https://github.com/woowacourse-teams/2025-Fit-toring/commit/3c3060458f32eca2ee6412edb455546e161f33b3))
* EditProfileForm에서 프로필 변경 및 검증 로직 개선 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([bfb7499](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bfb7499bb25b04a53186b0ce0c8c2a8a09999a95))
* EditProfileForm에서 프로필 변경 여부 확인 로직 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([d5bb89a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/d5bb89ad3e05476b04cf81ee1e51eaec2fd13de4))
* flyway image 컬럼 추가 스크립트 작성 ([78c65dd](https://github.com/woowacourse-teams/2025-Fit-toring/commit/78c65ddef657c19e9a74547b0937682950bf2372))
* gender 상태 관리를 위한 useGender 훅 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([e6594a1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/e6594a116563e38ba58a61b8c14dff1009ceb29f))
* GlobalExceptionHandler @RequestParam 예외 처리 로직 추가 ([677585e](https://github.com/woowacourse-teams/2025-Fit-toring/commit/677585e08fac46ab465d45bcb2b2b3427a560601))
* image에 variant 컬럼 추가 ([ac810f9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ac810f9564af8313ea9f5cf74c0207bae8a8a4e4))
* lazy 로딩을 통한 코드 스플리팅 적용 [#721](https://github.com/woowacourse-teams/2025-Fit-toring/issues/721) ([ddcaf08](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ddcaf084d0df30d856debb4382aecd772414dff2))
* MentoringApplicationItem 컴포넌트에 클램프된 내용 표시 기능 추가 [#702](https://github.com/woowacourse-teams/2025-Fit-toring/issues/702) ([fb28043](https://github.com/woowacourse-teams/2025-Fit-toring/commit/fb280431505bbad029fddd446708c1cbd39ff966))
* MentoringApplicationItem 컴포넌트에서 내용 더보기 기능 추가 및 스타일 개선 [#702](https://github.com/woowacourse-teams/2025-Fit-toring/issues/702) ([1be1e31](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1be1e31ff63668021d058151e3161771c3f48aab))
* picture 태그 추가하여 avif 지원하지 않을경우 png 파일을 fallback [#721](https://github.com/woowacourse-teams/2025-Fit-toring/issues/721) ([6639803](https://github.com/woowacourse-teams/2025-Fit-toring/commit/66398033603ab54a81e43483973b0ea5dd81ff7e))
* QueryDSL 설정 클래스 추가 ([30ad716](https://github.com/woowacourse-teams/2025-Fit-toring/commit/30ad716ad5b5e41deafdd7379c503eaa4e7d1c17))
* S3 동기화 명령어에 --delete 플래그 추가 [#739](https://github.com/woowacourse-teams/2025-Fit-toring/issues/739) ([6ac2896](https://github.com/woowacourse-teams/2025-Fit-toring/commit/6ac2896a6c66ab2eec1b5da0c6e11b990adac145))
* svg 파일 avif 로 변경 fallback 은 png 로 설정 [#721](https://github.com/woowacourse-teams/2025-Fit-toring/issues/721) ([4f2f1a4](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4f2f1a476a6daddf00f8eba49db0bcf59b73518b))
* useClampedRef 훅 추가하여 요소의 클램프 상태 관리 기능 구현 [#702](https://github.com/woowacourse-teams/2025-Fit-toring/issues/702) ([cd05f3b](https://github.com/woowacourse-teams/2025-Fit-toring/commit/cd05f3b4ed89a0e053a64838e7331376801b6a71))
* useFormattedPhoneNumber 훅에 초기값 설정 기능 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([0bb50a8](https://github.com/woowacourse-teams/2025-Fit-toring/commit/0bb50a85ab254549e854d6207e96382f59117c23))
* useNameInput 훅에 초기값 설정 기능 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([1d1c19a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/1d1c19a5e0c640ded172b5a9172b62add6d9ebbe))
* useSubmitGuardWithConfirm 훅의 초기값 설정 로직 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([ace16ab](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ace16ab02eccd2145e71336a3cf6881fa9eab62a))
* useVerificationStep 훅 추가하여 프로필 편집 기능에 검증 단계 관리 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([b9f8c3a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b9f8c3af2c8b1a1d13abe2bb97deb8822caf153b))
* 관리자 전용 이미지 업로드 컨트롤러 구현 ([bfedc8d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bfedc8d7a49e13ea6601f4e9d8f9df9af1efaa89))
* 로딩 스피너 추가 및 프로필 로드 중 null 반환 대신 스피너 표시 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([b4f2fdb](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b4f2fdb91f47f45275b92c01fdcfdb59e54783ec))
* 멘토링 요약 조회 메서드에 @Transactional(readOnly = true) 추가 ([83d9c56](https://github.com/woowacourse-teams/2025-Fit-toring/commit/83d9c569a01687c9bdfeeddd1ded4a8a35e846c2))
* 멘토링 카드 전체 조회 페이징 기능 추가 ([ffa5d7f](https://github.com/woowacourse-teams/2025-Fit-toring/commit/ffa5d7fba9df73c1b236891045763f37288fd2cd))
* 멘토링 페이징 조회 기능 추가 ([badb834](https://github.com/woowacourse-teams/2025-Fit-toring/commit/badb8347478874eca6dd5c026414dfed7edad564))
* 사용자 정보 조회를 위한 useMyProfile 훅 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([82ee46d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/82ee46dc83b9e76c3fe036b58b265b7d984a6e90))
* 사용자 프로필 데이터 모킹을 위한 USER_PROFILE 및 BASE_UPDATED_USER_PROFILE 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([338ae85](https://github.com/woowacourse-teams/2025-Fit-toring/commit/338ae859360cbf7d06b2da194d233a6890c161e5))
* 사용자 프로필 수정 API 호출을 위한 patchMyProfile 함수 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([23199b9](https://github.com/woowacourse-teams/2025-Fit-toring/commit/23199b96377f358ee68d8f3a914735e7c95b062a))
* 사용자 프로필 수정 MSW 핸들러 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([514af8c](https://github.com/woowacourse-teams/2025-Fit-toring/commit/514af8cffcb98500598156bff471ef219b3f3f92))
* 사용자 프로필 조회 MSW 핸들러 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([de935a0](https://github.com/woowacourse-teams/2025-Fit-toring/commit/de935a05276ff59aab27f59d05c9e884176b2ddc))
* 썸네일 이미지 조회 로직 추가 ([33efa48](https://github.com/woowacourse-teams/2025-Fit-toring/commit/33efa489cdd2e045c628d5e7624fb429c0ae2aaa))
* 예외처리 추가 ([4c6ec59](https://github.com/woowacourse-teams/2025-Fit-toring/commit/4c6ec59e4bb4f5790c13bae49965fda129e4667a))
* 이미지 썸네일 전략 패턴 도입 [#720](https://github.com/woowacourse-teams/2025-Fit-toring/issues/720) ([f078838](https://github.com/woowacourse-teams/2025-Fit-toring/commit/f078838de302e5023586951a400cd8b7da590670))
* 이미지 저장 none 타입 추가 ([57e3503](https://github.com/woowacourse-teams/2025-Fit-toring/commit/57e3503236d9c28928eedcf91f3c4452ac728931))
* 이미지 저장 none 타입 추가 ([bc1e67d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/bc1e67d5429743d8f3ebe1c3f74253bf9bbe81a6))
* 이미지 저장 시 기본/썸네일 variant 저장하도록 수정 ([59c399d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/59c399d337bffe70848b5e410937cd7f81401587))
* 이미지 크기 조절 유틸 구현 ([25aab02](https://github.com/woowacourse-teams/2025-Fit-toring/commit/25aab020bc6bd43a4af11c078723e78ad779f2ed))
* 이미지 확장자 변환 유틸 구현 ([2496c02](https://github.com/woowacourse-teams/2025-Fit-toring/commit/2496c02494b916c7d89c5b972c2d3141e4bc2d41))
* 자격증 및 프로필 이미지 캐싱 추가 [#727](https://github.com/woowacourse-teams/2025-Fit-toring/issues/727) ([8c2f8fc](https://github.com/woowacourse-teams/2025-Fit-toring/commit/8c2f8fc850dc7946f06b08333e407916d57f41d4))
* 전화번호 변경 시 제출 차단 로직 및 오류 메시지 처리 개선 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([164ed56](https://github.com/woowacourse-teams/2025-Fit-toring/commit/164ed56065d47766b9e2f6be4ff84e0752df2cc4))
* 커서 기반 페이징 도입을 위한 핵심 로직 추가 ([88a1c90](https://github.com/woowacourse-teams/2025-Fit-toring/commit/88a1c90564fb5efaa8c7652d65cec740c652306b))
* 페이지 접속시 바로 보이지 않는 이미지 lazy 로딩 처리 [#721](https://github.com/woowacourse-teams/2025-Fit-toring/issues/721) ([b8f9967](https://github.com/woowacourse-teams/2025-Fit-toring/commit/b8f99670861c6a92f369762a807a1f2fae327e6b))
* 프로필 수정 후 홈으로 리디렉션 기능 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([16c823d](https://github.com/woowacourse-teams/2025-Fit-toring/commit/16c823dac7d0ad42b3f37411297fe50550dc446e))
* 회원 드롭 다운 메뉴에 회원 정보 항목 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([40ae0d1](https://github.com/woowacourse-teams/2025-Fit-toring/commit/40ae0d19c575fad4d2db26d076f308a92b6a1b34))
* 회원 예약 조회 최적화 및 DTO 도입 ([5dd0df7](https://github.com/woowacourse-teams/2025-Fit-toring/commit/5dd0df7193bc4fc5ec06a7b47fb241c351d09da4))
* 회원 정보 수정 페이지에 EditProfileForm 컴포넌트 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([aadc94a](https://github.com/woowacourse-teams/2025-Fit-toring/commit/aadc94ac043694f77a05c577a18b46df10aa771e))
* 회원 정보 수정에 쓸 UserProfileResponse 및 UserProfileRequest 타입 추가 [#194](https://github.com/woowacourse-teams/2025-Fit-toring/issues/194) ([952cf73](https://github.com/woowacourse-teams/2025-Fit-toring/commit/952cf7389d19c8366ac5325c24bc9b52ce49c57e))

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
