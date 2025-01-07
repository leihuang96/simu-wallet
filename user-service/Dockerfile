# 构建阶段
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
# 复制项目的根 POM 文件和所有模块
COPY ./pom.xml ./pom.xml
COPY ./common-module ./common-module
COPY ./user-service ./user-service
COPY ./transaction-service ./transaction-service
COPY ./wallet-service ./wallet-service
COPY ./exchange-rate-service ./exchange-rate-service

RUN mvn clean package -pl user-service -am -DskipTests

# 运行阶段
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/user-service/target/user-service.jar app.jar
EXPOSE 8081
ENV SPRING_ZIPKIN_BASE_URL=http://zipkin:9411
ENV SPRING_APPLICATION_NAME=user-service
ENTRYPOINT ["java", "-jar", "app.jar"]
