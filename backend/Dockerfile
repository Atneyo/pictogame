# ---------- Stage 1: build the jar ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# The cache mount keeps downloaded dependencies between builds (much faster rebuilds)
RUN --mount=type=cache,target=/root/.m2 mvn -DskipTests package

# ---------- Stage 2: lightweight runtime image ----------
FROM eclipse-temurin:25-jre
WORKDIR /app

# Run as a non-root user: a compromised app cannot take over the container
RUN useradd --system --no-create-home appuser
COPY --from=build /app/target/*.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]