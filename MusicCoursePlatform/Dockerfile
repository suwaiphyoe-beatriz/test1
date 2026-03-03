# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage  ← 换成带 JavaFX 的镜像
FROM eclipse-temurin:17-jre
WORKDIR /app

# 安装 JavaFX 依赖
RUN apt-get update && apt-get install -y \\
    openjfx \\
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar

RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser

EXPOSE 8080

# 加上 JavaFX 模块路径
ENTRYPOINT ["java", \\
  "--module-path", "/usr/share/openjfx/lib", \\
  "--add-modules", "javafx.controls,javafx.fxml", \\
  "-jar", "app.jar"]