FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=build /target/demo-0.0.1-SNAPSHOT.jar app.jar # esse demo.... vem do artifactId e version do pom.xml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
