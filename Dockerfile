FROM openjdk:17-oracle
EXPOSE 8080
COPY target/simple-system-task*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]