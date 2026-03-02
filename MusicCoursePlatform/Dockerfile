# -------------------------------
# Build stage
# -------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# -------------------------------
# Run stage
# -------------------------------
FROM bellsoft/liberica-openjfx:17
WORKDIR /app
COPY --from=build /app/target/MusicCoursePlatform-1.0-SNAPSHOT.jar app.jar

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]
