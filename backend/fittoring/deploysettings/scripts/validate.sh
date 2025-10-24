#!/usr/bin/env bash
# CodeDeploy의 ValidateService 단계에서 실행됩니다.
# 새로 시작된 애플리케이션이 정상적으로 동작하는지 검증합니다.

set -Eeuo pipefail
trap 'echo "[ERROR] ${BASH_SOURCE[0]}:${LINENO} 명령 실패 (exit $?)"; exit 1' ERR

# 60초 동안 5초 간격으로 애플리케이션이 healthy 상태가 되는지 확인
for i in {1..12}; do
  echo "[INFO] 헬스체크 검사 시도 ($i/12)..."
  # docker compose ps의 상태가 "healthy"인지 확인
  if sudo docker-compose ps app | grep -q "healthy"; then
    echo "[SUCCESS] 애플리케이션이 healthy 상태입니다."
    exit 0
  fi
  sleep 5
done

echo "[ERROR] 60초 내에 애플리케이션이 healthy 상태가 되지 못했습니다."
# 디버깅을 위해 마지막 로그를 출력
sudo docker-compose logs --tail=200 app || true
exit 1
