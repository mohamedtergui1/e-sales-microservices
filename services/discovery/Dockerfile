FROM openjdk:17
ARG JAR_FILE_PATH=target/*.jar
COPY ${JAR_FILE_PATH} discovery-service.jar
ENTRYPOINT ["java","-jar","/discovery-service.jar"]
EXPOSE 8761