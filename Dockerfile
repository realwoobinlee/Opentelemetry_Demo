# 1. Building the App with Maven
FROM maven:3-jdk-11
 
ADD . /Opentelemetry_demo
WORKDIR /Opentelemetry_demo
 
# Just echo so we can see, if everything is there :)
RUN ls -l
 
# Run Maven build
RUN mvn clean install
 
# 2. Just using the build artifact and then removing the build-container
FROM openjdk:11-jdk
 
#VOLUME /tmp
 
# Add Spring Boot app.jar to Container
COPY --from=0 "/Opentelemetry_demo/target/Opentelemetry_demo-*-SNAPSHOT.jar" app.jar
COPY --from=0 "/Opentelemetry_demo/opentelemetry-javaagent-all.jar" javaagent.jar

# Fire up our Spring Boot app with Opentelemetry Javaagent
CMD [ "sh", "-c", "java -javaagent:/javaagent.jar -jar /app.jar" ]

EXPOSE 8080