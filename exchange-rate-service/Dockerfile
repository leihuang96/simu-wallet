# 构建阶段
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 将顶层 pom.xml 和子模块同时复制到容器中
COPY ./pom.xml /app/
COPY ./exchange-rate-service /app/exchange-rate-service/
RUN mvn -f /app/exchange-rate-service/pom.xml clean package -DskipTests
RUN ls -l /app/exchange-rate-service/target/

# 运行阶段
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/exchange-rate-service/target/*.jar app.jar

EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]