#!/usr/bin/env bash

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

# ------------------------------------------------------------
#  APP_DIR 내 .env 파일 로드 (환경변수 주입)
# ------------------------------------------------------------
ENV_FILE="$APP_DIR/.env"

if [ -f "$ENV_FILE" ]; then
  echo "[INFO] $ENV_FILE 파일에서 환경 변수를 로드합니다..."
  # export 자동화를 위해 set -a 사용
  set -a
  source "$ENV_FILE"
  set +a
else
  echo "[WARN] $ENV_FILE 파일이 존재하지 않습니다. 환경변수 미정 상태로 진행합니다."
fi
# ------------------------------------------------------------

# 배포 디렉터리에서 새로 전달된 이미지 태그 읽기
if [ ! -f "$APP_DIR/image_tag.txt" ]; then
  echo "[ERROR] image_tag.txt 파일이 없습니다. CodeBuild 아티팩트 구성을 확인하세요."
  exit 1
fi

IMAGE_TAG=$(cat "$APP_DIR/image_tag.txt")
IMAGE_REPO="fittoring/fittoring"
IMAGE_FULL="$DOCKERHUB_USERNAME/$IMAGE_REPO:$IMAGE_TAG"

echo "[INFO] 배포할 이미지: $IMAGE_FULL"

# DockerHub에서 새 이미지 pull
echo "[INFO] 새 이미지를 pull 합니다..."
sudo docker pull "$IMAGE_FULL"

# .env 파일을 업데이트하여 docker-compose가 올바른 이미지를 사용하도록 설정
echo "[INFO] .env 파일을 업데이트합니다..."
echo "IMAGE=$DOCKERHUB_USERNAME/$IMAGE_REPO" > .env.deploy
echo "TAG=$IMAGE_TAG" >> .env.deploy

# 실행 중인 컨테이너 교체
echo "[INFO] 새 이미지로 애플리케이션을 시작합니다..."
sudo docker-compose --env-file .env.deploy up -d --wait app

echo "[INFO] 배포 완료!"
