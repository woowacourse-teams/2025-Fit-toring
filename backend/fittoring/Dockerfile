FROM eclipse-temurin:21 AS runtime
WORKDIR /app

RUN sed -i 's|archive.ubuntu.com|azure.archive.ubuntu.com|g' /etc/apt/sources.list.d/ubuntu.sources \
 && apt-get update \
 && apt-get install -y --no-install-recommends \
    curl ca-certificates \
    libavif-bin \
 && rm -rf /var/lib/apt/lists/*

COPY build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["sh", "-c", "exec java $JAVA_TOOL_OPTIONS -Duser.timezone=Asia/Seoul -jar app.jar"]