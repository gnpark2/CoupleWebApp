FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app
COPY shared ./shared
COPY settings.gradle .
COPY build.gradle .
COPY services/diary-service/build.gradle ./services/diary-service/build.gradle
COPY services/diary-service/src ./services/diary-service/src
RUN gradle :services:diary-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/services/diary-service/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
