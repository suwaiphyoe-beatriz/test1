# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install JavaFX manually
RUN apt-get update && apt-get install -y wget unzip

# Download JavaFX SDK
RUN wget https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_linux-x64_bin-sdk.zip \
    && unzip openjfx-21.0.2_linux-x64_bin-sdk.zip \
    && mv javafx-sdk-21.0.2 /opt/javafx

# Copy jar
COPY --from=build /app/target/MusicCoursePlatform-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "--module-path=/opt/javafx/lib", "--add-modules=javafx.controls,javafx.fxml", "-jar", "app.jar"]