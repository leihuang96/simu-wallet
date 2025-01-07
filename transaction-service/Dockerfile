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
RUN mvn clean package -pl transaction-service -am -DskipTests


# 运行阶段
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/transaction-service/target/transaction-service.jar app.jar

EXPOSE 8084
ENV SPRING_ZIPKIN_BASE_URL=http://zipkin:9411
ENV SPRING_APPLICATION_NAME=transaction-service
ENTRYPOINT ["java", "-jar", "app.jar"]
