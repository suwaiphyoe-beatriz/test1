# Build stage — force amd64 so JavaFX native JARs resolve correctly
FROM --platform=linux/amd64 maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage — arm64 is fine here
FROM --platform=linux/arm64 eclipse-temurin:17-jre
WORKDIR /app

RUN apt-get update && apt-get install -y openjfx && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/MusicCoursePlatform-1.0-SNAPSHOT.jar app.jar

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "--module-path", "/usr/share/openjfx/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "app.jar"]