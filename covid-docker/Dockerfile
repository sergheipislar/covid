FROM openjdk:11
ARG JAR_FILE=target/release/covid-service-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} covid-service.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV TZ=Europe/Bucharest
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080
ENTRYPOINT ["java","-jar","/covid-service.jar"]