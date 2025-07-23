FROM gradle:8.7-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || true
COPY . .
RUN gradle clean bootJar -x test --no-daemon


FROM openjdk:21-jdk-slim AS runtime
WORKDIR /app

COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
