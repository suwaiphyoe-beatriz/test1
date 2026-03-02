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

# THE FIX: Point exactly to the SNAPSHOT jar to avoid the "original" one
COPY --from=build /app/target/MusicCoursePlatform-1.0-SNAPSHOT.jar app.jar

# Create a non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

ENTRYPOINT ["java", "-Dglass.platform=Monocle", "-Dmonocle.platform=Headless", "-Dprism.order=sw", "-jar", "app.jar"]
