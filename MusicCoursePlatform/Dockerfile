# -------------------------------
# Build stage
# -------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom first to leverage Docker cache for dependencies
COPY pom.xml .
COPY src ./src

# Build the application, skip tests to speed up build in Docker
RUN mvn clean package -DskipTests

# -------------------------------
# Run stage
# -------------------------------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

# Expose port (optional, for future web interface)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
