FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app
COPY shared ./shared
COPY settings.gradle .
COPY build.gradle .
COPY services/auth-service/build.gradle ./services/auth-service/build.gradle
COPY services/auth-service/src ./services/auth-service/src
RUN gradle :services:auth-service:bootJar --no-daemon -x test
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/services/auth-service/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["java","-jar","app.jar"]
