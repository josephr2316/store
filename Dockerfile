# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (no application code execution)
RUN ./mvnw dependency:go-offline -B

# Copy source and build (skip tests for smaller image; tests run in CI)
COPY src src
RUN ./mvnw package -B -DskipTests -q

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user
RUN adduser -D -s /bin/sh appuser
USER appuser

# Copy built JAR
COPY --from=builder /app/target/store-*.jar app.jar

# Render injects PORT; default 8080
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
