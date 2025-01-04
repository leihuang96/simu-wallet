# 构建阶段
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY ./pom.xml /app/

# 复制本地 Maven 仓库到容器
COPY ./wallet-service /app/wallet-service/

# 构建可执行 JAR 文件
RUN mvn -f /app/wallet-service/pom.xml clean package -DskipTests
RUN ls -l /app/wallet-service/target/


# 运行阶段
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/wallet-service/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]