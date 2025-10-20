#!/usr/bin/env bash
# CodeDeploy의 ApplicationStop 단계에서 실행됩니다.
# 새 버전 배포를 위해 기존에 실행 중이던 Docker 컨테이너를 중지합니다.

set -Eeuo pipefail
trap 'echo "[ERROR] ${BASH_SOURCE[0]}:${LINENO} 명령 실패 (exit $?)"; exit 1' ERR

APP_DIR="/home/ssm-user/fittoring"
cd "$APP_DIR"

# docker-compose.yml 파일이 존재하는 경우에만 down 명령 실행
if [ -f "docker-compose.yml" ]; then
  echo "[INFO] 기존 애플리케이션을 중지합니다 (docker compose down)"
  sudo docker-compose down --remove-orphans
else
  echo "[INFO] docker-compose.yml 파일이 없으므로 중지할 애플리케이션이 없습니다."
fi
