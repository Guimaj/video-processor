FROM amazoncorretto:21-alpine-jdk as build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM amazoncorretto:21-alpine-jdk
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
WORKDIR /tmp/ffmpeg

RUN apk update
RUN apk upgrade
RUN apk add --no-cache ffmpeg

EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*","com.soat.hackathon.video_processor.VideoProcessorApplication"]
