FROM public.ecr.aws/amazoncorretto/amazoncorretto:17-al2-native-jdk

MAINTAINER Ramakrishna Bellana

WORKDIR /app

COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
