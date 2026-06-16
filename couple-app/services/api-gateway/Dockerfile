FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app
COPY shared ./shared
COPY settings.gradle .
COPY build.gradle .
COPY services/api-gateway/build.gradle ./services/api-gateway/build.gradle
COPY services/api-gateway/src ./services/api-gateway/src
RUN gradle :services:api-gateway:bootJar --no-daemon -x test
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/services/api-gateway/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
