FROM azul/zulu-openjdk-alpine:21
ENV DATASOURCE_URL DATASOURCE_URL
ENV DATASOURCE_USERNAME DATASOURCE_USERNAME
ENV DATASOURCE_PASSWORD DATASOURCE_PASSWORD
ENV eureka.client.serviceUrl.defaultZone eureka.client.serviceUrl.defaultZone
EXPOSE 8083
COPY target/*.jar hackaton-cartao-app-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/hackaton-cartao-app-0.0.1-SNAPSHOT.jar"]
