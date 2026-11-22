FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml pom.xml
COPY emissor/pom.xml emissor/pom.xml
COPY gravador/pom.xml gravador/pom.xml
COPY emissor/src emissor/src
COPY gravador/src gravador/src
RUN mvn -B -pl emissor -am -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/emissor/target/emissor-0.1.0-SNAPSHOT.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
