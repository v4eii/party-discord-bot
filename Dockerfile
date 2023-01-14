FROM ubuntu:18.04
COPY /app/target/party-bot.jar /app
CMD java -jar /app/party-bot.jar
