FROM openjdk:17-jdk-alpine3.13
WORKDIR /app
EXPOSE 4001
COPY build/libs/gradle-parser-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "gradle-parser-0.0.1-SNAPSHOT.jar"]