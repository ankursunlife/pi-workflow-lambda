FROM openjdk:11
ADD target/pi-metadata-service-0.0.1-SNAPSHOT.jar pi-metadata-service.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","pi-metadata-service.jar"]