FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app
COPY shared ./shared
COPY settings.gradle .
COPY build.gradle .
COPY services/notification-service/build.gradle ./services/notification-service/build.gradle
COPY services/notification-service/src ./services/notification-service/src
RUN gradle :services:notification-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/services/notification-service/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
