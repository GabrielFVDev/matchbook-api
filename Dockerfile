# Stage 1: compile inside Docker, so the image builds straight from the repository
# (Render builds from GitHub, where there is no target/ folder).
FROM eclipse-temurin:17-jdk AS build

WORKDIR /build

# Dependencies first: while pom.xml does not change, Docker reuses this layer and skips the download.
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -q -B dependency:go-offline

COPY src src
RUN ./mvnw -q -B -DskipTests package

# Stage 2: only the JRE and the jar.
FROM eclipse-temurin:17-jre

LABEL authors="gabriel"

WORKDIR /app

COPY --from=build /build/target/matchbook-0.0.1-SNAPSHOT.jar app.jar

# Render's free plan has 512 MB and 0.1 CPU. The first three flags keep the JVM inside the memory;
# TieredStopAtLevel=1 spends less CPU compiling at startup (measured at 0.1 CPU: 6 min without it, under 3 min with it).
# A JAVA_TOOL_OPTIONS variable on the service replaces this line.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -XX:+UseSerialGC -Xss512k -XX:TieredStopAtLevel=1"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
