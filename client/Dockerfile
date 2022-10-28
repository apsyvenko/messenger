FROM arm64v8/openjdk:18
ARG JAR_FILE=target/client-*-SNAPSHOT.jar
COPY ${JAR_FILE} client.jar
ENTRYPOINT ["java","-jar","/client.jar","ws://messenger-server:8080/ws/messages"]