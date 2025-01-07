# 构建阶段
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY ./pom.xml ./pom.xml
COPY ./common-module ./common-module
COPY ./user-service ./user-service
COPY ./transaction-service ./transaction-service
COPY ./wallet-service ./wallet-service
COPY ./exchange-rate-service ./exchange-rate-service

RUN mvn clean install -pl common-module -am -DskipTests
RUN mvn clean package -pl wallet-service -am -DskipTests



# 运行阶段
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/wallet-service/target/wallet-service.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
# 设置环境变量，用于 Zipkin 配置
ENV SPRING_ZIPKIN_BASE_URL=http://zipkin:9411
ENV SPRING_APPLICATION_NAME=wallet-service