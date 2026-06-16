FROM gradle:8.7-jdk21-alpine AS builder
WORKDIR /app
COPY shared ./shared
COPY settings.gradle .
COPY build.gradle .
COPY services/character-service/build.gradle ./services/character-service/build.gradle
COPY services/character-service/src ./services/character-service/src
RUN gradle :services:character-service:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /app/services/character-service/build/libs/*.jar app.jar
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]
