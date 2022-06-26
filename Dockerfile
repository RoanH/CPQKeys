# syntax=docker/dockerfile:1
FROM openjdk:8 AS compile
LABEL maintainer="roan@roanh.dev"
WORKDIR /CPQKeys
ADD CPQKeys/gradle/wrapper/ /CPQKeys/gradle/wrapper/
ADD CPQKeys/src/ /CPQKeys/src/
ADD CPQKeys/bindings/ /CPQKeys/bindings/
ADD CPQKeys/build.gradle /CPQKeys/
ADD CPQKeys/gradlew /CPQKeys/
ADD CPQKeys/settings.gradle /CPQKeys/
ADD CPQKeys/compileNatives.sh /CPQKeys/
RUN chmod -R 755 ./
RUN apt-get update && apt-get -y install gcc g++ cmake
RUN ./compileNatives.sh
RUN ./gradlew :shadowJar

FROM openjdk:8
LABEL maintainer="roan@roanh.dev"
WORKDIR /CPQKeys
RUN apt-get update && apt-get -y install python3
COPY --from=compile /CPQKeys/build/libs/CPQKeys-all.jar ./CPQKeys.jar
COPY --from=compile /CPQKeys/native/* ./native/
COPY --from=compile /CPQKeys/bindings/scott/* ./bindings/scott/
ENTRYPOINT ["java", "-jar", "CPQKeys.jar"]
