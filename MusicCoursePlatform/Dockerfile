FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/MusicCoursePlatform-1.0-SNAPSHOT-shaded.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
