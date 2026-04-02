FROM eclipse-temurin:25-jdk AS builder

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /build

COPY pom.xml .
COPY fitnation-common/pom.xml         fitnation-common/pom.xml
COPY fitnation-user/pom.xml           fitnation-user/pom.xml
COPY fitnation-membership/pom.xml     fitnation-membership/pom.xml
COPY fitnation-booking/pom.xml        fitnation-booking/pom.xml
COPY fitnation-trainer/pom.xml        fitnation-trainer/pom.xml
COPY fitnation-nutrition/pom.xml      fitnation-nutrition/pom.xml
COPY fitnation-rest-api/pom.xml       fitnation-rest-api/pom.xml
COPY fitnation-frontend/pom.xml       fitnation-frontend/pom.xml

COPY fitnation-common/src             fitnation-common/src
COPY fitnation-user/src               fitnation-user/src
COPY fitnation-membership/src         fitnation-membership/src
COPY fitnation-booking/src            fitnation-booking/src
COPY fitnation-trainer/src            fitnation-trainer/src
COPY fitnation-nutrition/src          fitnation-nutrition/src
COPY fitnation-rest-api/src           fitnation-rest-api/src

RUN mvn clean package -pl fitnation-rest-api -am \
    -DskipTests \
    -B --no-transfer-progress


FROM eclipse-temurin:25-jre AS runtime

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

WORKDIR /app

COPY --from=builder /build/fitnation-rest-api/target/fitnation-rest-api-*.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]