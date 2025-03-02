FROM openjdk:17
ARG JAR_FILE_PATH=target/*.jar
COPY ${JAR_FILE_PATH} config-server.jar
ENTRYPOINT ["java","-jar","/config-server.jar"]
EXPOSE 8080