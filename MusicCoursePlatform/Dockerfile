FROM --platform=linux/amd64 maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM --platform=linux/amd64 eclipse-temurin:17-jre
WORKDIR /app

RUN apt-get update && apt-get install -y libx11-6 libxext6 libxrender1 libxtst6 libxi6 libgtk-3-0 wget unzip && rm -rf /var/lib/apt/lists/*

# Download JavaFX SDK (linux x64)
RUN mkdir -p /javafx-sdk && wget -O javafx.zip https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_linux-x64_bin-sdk.zip && unzip javafx.zip -d /javafx-sdk && mv /javafx-sdk/javafx-sdk-21.0.2/lib /javafx-sdk/lib && rm -rf /javafx-sdk/javafx-sdk-21.0.2 javafx.zip

COPY --from=build /app/target/MusicCoursePlatform-1.0-SNAPSHOT.jar app.jar

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

EXPOSE 8080

CMD ["java", "--module-path", "/javafx-sdk/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "app.jar"]