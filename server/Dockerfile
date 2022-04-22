FROM openjdk:8-jdk
ARG VERSION
VOLUME /tmp
RUN apt-get update && apt-get install -y libjffi-jni
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
COPY ./build/libs/server-${VERSION}.jar app.jar
