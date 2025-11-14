FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /build/target/hotel-booking-monolith-*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
