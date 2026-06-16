FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app
COPY shared ./shared
COPY settings.gradle . 
COPY build.gradle .
COPY services/user-service/build.gradle ./services/user-service/build.gradle
COPY services/user-service/src ./services/user-service/src
RUN gradle :services:user-service:bootJar --no-daemon -x test
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/services/user-service/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["java","-jar","app.jar"]
