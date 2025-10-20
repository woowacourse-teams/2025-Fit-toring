#!/usr/bin/env bash
# CodeDeploy의 ApplicationStart 단계에서 실행됩니다.
# 새 버전의 애플리케이션을 시작합니다.

set -Eeuo pipefail
trap 'echo "[ERROR] ${BASH_SOURCE[0]}:${LINENO} 명령 실패 (exit $?)"; exit 1' ERR

APP_DIR="/home/ssm-user/fittoring"
cd "$APP_DIR"

# 여유 공간(%): /var/lib/docker 기준
FREE_PCT=$(df -P /var/lib/docker 2>/dev/null | awk 'NR==2{gsub("%","",$5); print 100-$5}')
FREE_PCT=${FREE_PCT:-0}

if [ "$FREE_PCT" -lt 10 ]; then
  echo "[INFO] 디스크 여유(${FREE_PCT}%) < 10% → 캐시 정리"
  sudo docker system prune -af
fi

# CI/CD 파이프라인에서 생성한 .env.deploy 파일을 docker-compose가 사용할 수 있도록 복사합니다.
# 이 파일에는 IMAGE와 TAG 정보가 들어있습니다.
# CodeDeploy는 배포 파일을 /home/ssm-user/fittoring-deploy 디렉토리에 복사합니다.

# docker-compose.yml에 정의된 이미지 이름과 태그를 .env.deploy 파일에서 읽어옵니다.
# 명시적으로 이미지를 pull 합니다.
echo "[INFO] 새 이미지를 pull 합니다."
sudo docker-compose --env-file .env.deploy pull app

echo "[INFO] 새 이미지로 애플리케이션을 시작합니다 (docker compose up)"
# --pull never 옵션으로 방금 pull 받은 이미지를 사용하도록 강제합니다.
sudo docker-compose --env-file .env.deploy up -d --wait app
