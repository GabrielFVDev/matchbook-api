FROM eclipse-temurin:17-jre

LABEL authors="gabriel"

WORKDIR /app

COPY target/matchbook-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]