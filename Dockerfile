FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /opt/app

COPY .mvn/ .mvn
COPY settings.xml /root/.m2/settings.xml
RUN cat /root/.m2/settings.xml

COPY mvnw pom.xml ./

RUN chmod +x ./mvnw

COPY ./src ./src

RUN ./mvnw clean install -DskipTests


FROM eclipse-temurin:21 AS jre-build
WORKDIR /opt/app
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar

RUN jar xf /opt/app/*.jar
RUN ${JAVA_HOME}/bin/jdeps --ignore-missing-deps -q  \
    --recursive  \
    --multi-release 21  \
    --print-module-deps  \
    --class-path 'BOOT-INF/lib/*'  \
    /opt/app/*.jar > deps.info

RUN ${JAVA_HOME}/bin/jlink \
         --add-modules $(cat deps.info) \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime




FROM debian:stretch-slim

ENV JAVA_OPTS=""
ENV JAVA_ARGS=""
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"

COPY --from=jre-build /javaruntime $JAVA_HOME

RUN mkdir /opt/app
COPY --from=jre-build /opt/app/*.jar /opt/app/*.jar
EXPOSE 8080
ENTRYPOINT java ${JAVA_OPTS} -jar /opt/app/*jar ${JAVA_ARGS}