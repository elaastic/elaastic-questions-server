# Build stage
# ----------------
FROM eclipse-temurin:17-jdk-alpine as build

ENV REFRESHED_AT 2023-08-25

WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY src src
COPY lib lib

RUN ./gradlew build -x test
RUN mkdir -p build/libs/dependency && (cd build/libs/dependency; jar -xf ../*.jar)


# Production stage
#-----------------
FROM eclipse-temurin:17-jdk-alpine

# Create a non-root user
RUN addgroup -S elaastic && adduser -S elaastic -G elaastic

# Initialize datastore
RUN mkdir -p /datastore && chown -R  elaastic:elaastic /datastore

USER elaastic

# Copy application ressources
ARG DEPENDENCY=/workspace/app/build/libs/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app


EXPOSE 8080

ENTRYPOINT [ \
    "java", \
    "-cp", \
    "/app:/app/lib/*", \
    "-Xms512M",  "-Xmx2048M", \
    "-server", "-XX:+UseParallelGC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Djava.io.tmpdir=/tmp/", \
    "org.elaastic.questions.ElaasticQuestionsServerKt", \
    "--spring.config.additional-location=file:/configuration/" \
]
