FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml /app/
RUN mvn dependency:go-offline
COPY src /app/src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/monio-0.0.1-SNAPSHOT.jar monio.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "monio.jar"]