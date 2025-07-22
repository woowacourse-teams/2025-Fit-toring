FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y findutils

WORKDIR /app

COPY gradlew ./
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies || true
COPY . .
RUN ./gradlew clean bootJar -x test
RUN mv build/libs/*SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
