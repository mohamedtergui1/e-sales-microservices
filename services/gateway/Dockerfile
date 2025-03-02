FROM openjdk:17
ARG JAR_FILE_PATH=target/*.jar
COPY ${JAR_FILE_PATH} gateway-service.jar
ENTRYPOINT ["java","-jar","/gateway-service.jar"]
EXPOSE 8222