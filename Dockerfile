FROM maven:3.6.3 AS builder

WORKDIR /usr/src/app
COPY . /usr/src/app

RUN mvn clean package -B

FROM openjdk:17-jdk

COPY --from=builder /usr/src/app/target/*.jar /mapcollector.jar

ENTRYPOINT ["java","-jar","/mapcollector.jar"]
