# Stage 1: build the jar with the project's own Gradle wrapper (no Gradle or Java needed on the host)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace

# Wrapper and build files first, so the dependency download is cached while only the code changes
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null

COPY src src
# Tests run in CI, not on every image build
RUN ./gradlew --no-daemon bootJar -x test

# Stage 2: only the JRE and the jar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user. /data holds the SQLite file: it must belong to that user, or SQLite cannot create it
# (SQLITE_CANTOPEN). A named volume mounted there starts with these owners.
RUN addgroup -S app && adduser -S app -G app && mkdir /data && chown app:app /data
COPY --from=build /workspace/build/libs/*-SNAPSHOT.jar app.jar
USER app

ENV DB_PATH=/data/salary.db
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
