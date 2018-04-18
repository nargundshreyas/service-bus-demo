FROM frolvlad/alpine-oraclejdk8
VOLUME /tmp
ADD  build/libs/SampleMircoservice-0.0.1-SNAPSHOT.jar SampleMircoservice.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/SampleMircoservice.jar"]