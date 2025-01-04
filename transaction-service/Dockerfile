# 构建阶段
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 将顶层 pom.xml 和子模块同时复制到容器中
COPY ./pom.xml /app/
COPY ./transaction-service /app/transaction-service/

RUN mvn -f /app/transaction-service/pom.xml clean package -DskipTests
RUN ls -l /app/transaction-service/target/


# 运行阶段
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/transaction-service/target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
