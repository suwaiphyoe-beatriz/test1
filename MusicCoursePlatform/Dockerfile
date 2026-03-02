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
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/MusicCoursePlatform-1.0-SNAPSHOT.jar app.jar

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

# THE FIX: 
# 1. We treat the jar as the module-path.
# 2. We explicitly add the modules the JVM is looking for.
ENTRYPOINT ["java", "--module-path", "app.jar", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "app.jar"]
