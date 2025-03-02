FROM openjdk:17
ARG JAR_FILE_PATH=target/*.jar
COPY ${JAR_FILE_PATH} payment-service.jar
ENTRYPOINT ["java","-jar","/payment-service.jar"]
EXPOSE 7368