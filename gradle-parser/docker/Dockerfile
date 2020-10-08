FROM gradle:6.6.1-jdk11 as builder
USER root
WORKDIR /builder
ADD . /builder
RUN gradle build --stacktrace
RUN ls -lah /builder/build/libs

FROM openjdk:15-jdk-alpine
WORKDIR /app
EXPOSE 4001
COPY --from=builder /builder/build/libs/gradle-parser-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "gradle-parser-0.0.1-SNAPSHOT.jar"]