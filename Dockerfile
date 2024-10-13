FROM openjdk:21-jdk

WORKDIR /app

COPY target/desafio-credpago-0.0.1-SNAPSHOT.jar /app/desafio-credpago.jar

EXPOSE 8080

CMD ["java", "-jar", "desafio-credpago.jar"]