FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn clean dependency:list package


FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/party-bot.jar /usr/local/lib/party-bot.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/party-bot.jar"]
