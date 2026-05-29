FROM eclipse-temurin:21 AS runtime
WORKDIR /app

RUN sed -i 's|archive.ubuntu.com|azure.archive.ubuntu.com|g' /etc/apt/sources.list.d/ubuntu.sources \
 && apt-get update \
 && apt-get install -y --no-install-recommends \
    curl ca-certificates \
    libavif-bin \
 && rm -rf /var/lib/apt/lists/*

# OpenTelemetry Java agent — 이미지에 굽어 호스트 마운트 의존 제거 (버전 고정)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.28.1/opentelemetry-javaagent.jar /app/otel-agent.jar

COPY build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["sh", "-c", "exec java $JAVA_TOOL_OPTIONS -Duser.timezone=Asia/Seoul -jar app.jar"]