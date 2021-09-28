FROM maven:3.6.3-adoptopenjdk-15-openj9
COPY src /usr/src/app/src
COPY pomDEPLOY.xml /usr/src/app
RUN mvn -f /usr/src/app/pomDEPLOY.xml install

FROM adoptopenjdk/openjdk16
COPY ./target/DiscordBotPluginArchitecture.jar /tmp
WORKDIR /tmp
CMD ["java", "-jar", "DiscordBotPluginArchitecture.jar"]