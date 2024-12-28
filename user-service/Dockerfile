# 构建阶段
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY ./pom.xml /app/
COPY ./user-service /app/user-service/

RUN mvn -f /app/user-service/pom.xml clean package -DskipTests
RUN ls -l /app/user-service/target/


# 运行阶段
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
