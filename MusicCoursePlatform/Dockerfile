# Build stage - Removed the forced amd64 for faster native builds on your Mac
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage - Using Liberica 'Full' which includes JavaFX modules built-in
FROM bellsoft/liberica-openjdk-debian:17-full
WORKDIR /app

# Copy the built JAR file (adjust 'app.jar' if your pom.xml generates a different name)
COPY --from=build /app/target/*.jar app.jar

# Create a non-root user
RUN useradd -ms /bin/bash appuser
USER appuser

# Since 'Full' images often have JavaFX in the default classpath/module path,
# we can simplify the ENTRYPOINT.
ENTRYPOINT ["java", "-jar", "app.jar"]