# Mentoring Reservation SMS Outbox Load Test

예약 트래픽이 SMS 처리량을 초과해도 예약 API가 응답 성능을 유지하고, 적체된 Outbox가 유실 없이 처리되는지 검증한다.

## 테스트 데이터

- 멘티 1,000명이 각각 한 번만 예약한다.
- 멘토링 200개에 신규 예약을 5건씩 배분한다.
- 모든 `(loginId, mentoringId)` 조합은 기존 활성 예약과 겹치지 않아야 한다.
- 실제 계정이 들어가는 `reservation-scenarios.json`은 Git에 커밋하지 않는다.

`reservation-scenarios.sample.json`은 파일 구조를 보여주기 위한 예시다. 실제 실행 파일은 정확히 1,000행이어야 한다.

```json
[
  {
    "loginId": "mentee_1",
    "password": "test1234",
    "mentoringId": 1
  }
]
```

스크립트 시작 시 다음 조건을 검증한다.

- 시나리오 행 1,000개
- 중복 `loginId` 없음
- 멘토링 ID 200개
- 멘토링별 예약 5건
- 중복 `(loginId, mentoringId)` 없음

기존 활성 예약과의 중복 여부는 시나리오 파일 생성 시 DB에서 검증해야 한다.

## 사용자 흐름

`setup()`에서 1,000개 계정을 20개씩 묶어 로그인하고 access token을 준비한다. 실제 부하 구간에서는 예약 API만 호출한다.

```text
setup: POST /login
test:  POST /mentorings/{mentoringId}/reservation
```

access token은 setup 결과로만 전달하며 파일에 저장하지 않는다.

## 17분 부하 모델

| 구간 | 실행 방식 | 예약 RPS | 예약 수 |
| --- | --- | ---: | ---: |
| 0~5분 | `constant-arrival-rate` | 1 | 300 |
| 5분~7분 20초 | `constant-arrival-rate` | 5 | 700 |
| 7분 20초~17분 | 요청 없이 Outbox 복구 관찰 | 0 | 0 |

복구 관찰 9분 40초는 적체된 Outbox가 전부 `SENT`로 처리되는 데 충분한 시간이다(실측상 약 13분 내 완료).

총 1,000건을 생성하며 `dropped_iterations`가 한 건이라도 발생하면 테스트를 실패 처리한다.

## RUN_ID

`RUN_ID`는 반복 실행 결과를 구분하는 식별자다. 예약 내용에 다음 형식으로 저장된다.

```text
{RUN_ID}-0001
```

PowerShell에서 실행 시각으로 생성할 수 있다.

```powershell
$runId = "sms-outbox-" + (Get-Date -Format "yyyyMMdd-HHmmss")
```

테스트 후 이번 실행 데이터만 조회할 수 있다.

```sql
SELECT COUNT(*)
FROM reservation
WHERE content LIKE 'sms-outbox-20260610-143000-%';
```

## 실행 전제

1. STG 애플리케이션이 테스트 대상 커밋으로 배포되어 있어야 한다.
2. 실제 CoolSMS를 호출하지 않도록 `sms.base-url`이 Mock SMS 서버를 가리켜야 한다.
3. Mock SMS 서버는 2초 후 성공 응답을 반환해야 한다.
4. Outbox Publisher는 5초 간격, 배치 크기 10, 단일 앱 인스턴스로 실행한다.
5. `reservation-scenarios.json`의 조합이 기존 활성 예약과 겹치지 않아야 한다.

## Mock SMS 서버

`performance/mock-sms/`의 서버가 Solapi 배치 발송 엔드포인트(`POST /messages/v4/send-many/detail`)를 흉내 낸다.
`MOCK_SMS_DELAY_MS`(기본 2000ms) 지연 후 전건 성공 응답을 반환하며, `GET /health`로 기동을 확인한다.

```json
{
  "failedMessageList": []
}
```

STG 호스트에서 앱 컨테이너와 같은 docker network에 띄운다.

```bash
cd performance/mock-sms
docker build -t mock-sms .
docker run -d --name mock-sms --restart unless-stopped \
  --network <앱_컨테이너_network> \
  mock-sms

# 네트워크 이름 확인
docker inspect fittoring-app --format '{{range $k, $_ := .NetworkSettings.Networks}}{{$k}}{{end}}'

# 기동 확인 (앱 컨테이너 내부에서)
docker exec fittoring-app curl -s http://mock-sms:9090/health
```

앱이 Mock을 가리키도록 STG `.env`에 한 줄을 추가하고 앱 컨테이너를 재기동한다.
`SMS_BASE_URL`을 지우면 다시 실제 Solapi(`https://api.solapi.com`)로 돌아간다.

```text
SMS_BASE_URL=http://mock-sms:9090
```

테스트 시작 전 적용 여부를 반드시 확인한다. 실제 Solapi로 1,000건이 나가면 안 된다.

```bash
docker exec fittoring-app printenv SMS_BASE_URL   # http://mock-sms:9090 이어야 함
```

## 실행

```powershell
$runId = "sms-outbox-" + (Get-Date -Format "yyyyMMdd-HHmmss")

Set-Location performance/k6

k6 run `
  -e BASE_URL=https://stg-api.example.com `
  -e SCENARIO_FILE=./reservation-scenarios.json `
  -e RUN_ID=$runId `
  -e P95_BUDGET_MS=1500 `
  reservation-create-sms-outbox.js
```

## 성공 기준

- 예약 API 성공률 99% 초과
- 예약 API 실패율 1% 미만
- 예약 API p95 1,500ms 미만
- dropped iteration 0건
- 예약, 멘토링 통계, Outbox 증가량 각각 1,000건
- SMS 처리 지연 p95 6분 이내
- SMS 처리 지연 p99 및 최대 7분 이내
- 최종 `PENDING`, `PROCESSING`, `FAILED` 0건
- 최종 `SENT` 1,000건

## Grafana 관찰 지표

- 예약 API RPS, p95, p99, 5xx 오류율
- Tomcat busy threads
- Hikari active, idle, pending connections
- 상태별 Outbox 수와 oldest pending age
- Publisher 실행 결과, 처리 시간, 배치 크기
- SMS 발송 성공·실패량
- `sms_outbox_delivery_latency_seconds` p95, p99, 최대값

## 특정 건 지연 검증 (구조화 로그)

발송 성공마다 아래 형식의 로그가 남는다. Prometheus label 대신 이 로그로 특정 멘토(특정 예약)의 지연을 검증한다.

```text
SMS outbox delivered: outboxId=.., reservationId=.., eventType=.., createdAt=.., sentAt=.., deliveryLatencyMs=..
```

대기열 후미 검증 대상은 5 RPS 구간 마지막 예약이다. `content`가 `{RUN_ID}-1000`인 예약 ID를 찾아 Grafana Loki에서 검색한다.

```logql
{job="fittoring-app"} |= "SMS outbox delivered" |= "reservationId=<예약ID>"
```

특정 실행의 최종 지연은 DB에서도 확인할 수 있다.

```sql
SELECT
    AVG(TIMESTAMPDIFF(MICROSECOND, so.created_at, so.updated_at)) / 1000000 AS avg_seconds,
    MAX(TIMESTAMPDIFF(MICROSECOND, so.created_at, so.updated_at)) / 1000000 AS max_seconds
FROM sms_outbox so
JOIN reservation r ON r.id = so.reservation_id
WHERE r.content LIKE CONCAT(:runId, '-%')
  AND so.status = 'SENT';
```
