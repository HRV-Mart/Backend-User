FROM openjdk:19
EXPOSE 8080 27017
ARG JAR_FILE=build/libs/User-0.0.1-SNAPSHOT.jar
ARG MONGODB_URI=mongodb://localhost:27017
ENV MONGODB_URI=$MONGODB_URI
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]