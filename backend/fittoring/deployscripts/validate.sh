#!/usr/bin/env bash

set -Eeuo pipefail
trap 'echo "[ERROR] ${BASH_SOURCE[0]}:${LINENO} 명령 실패 (exit $?)"; exit 1' ERR

cd "$(dirname "$0")/../" || exit 1

echo "[INFO] HTTP 헬스체크 검증을 시작합니다."
HEALTHCHECK_URL="http://localhost:80/healthcheck"

for i in {1..12}; do
  echo "[INFO] 헬스체크 검사 시도 ($i/12) - URL: $HEALTHCHECK_URL"
  HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTHCHECK_URL" || echo 000)

  if [ "$HTTP_STATUS" -eq 200 ]; then
    echo "[SUCCESS] 애플리케이션이 Healthy 상태입니다 (HTTP 200)."
    exit 0
  fi
  echo "[INFO] 현재 HTTP 응답 코드: $HTTP_STATUS"
  sleep 5
done

echo "[ERROR] 60초 내에 애플리케이션이 HTTP 200 응답을 반환하지 못했습니다."
sudo docker-compose logs --tail=200 app || true
exit 1
