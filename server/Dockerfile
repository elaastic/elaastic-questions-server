# Build stage
# ----------------
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /workspace/app

COPY gradle gradle
COPY settings.gradle.kts gradlew ./
COPY server/build.gradle.kts server/build.gradle.kts
COPY server/src server/src
COPY server/lib server/lib

RUN ./gradlew --no-daemon :server:bootJar
RUN mkdir -p server/build/libs/dependency && (cd server/build/libs/dependency; jar -xf ../*.jar)


# Production stage
#-----------------
FROM eclipse-temurin:17-jdk-alpine

# Create a non-root user
RUN addgroup -S elaastic && adduser -S elaastic -G elaastic \
  # Initialize datastore \
  && mkdir -p /datastore && chown -R  elaastic:elaastic /datastore

USER elaastic

# Copy application ressources
ARG DEPENDENCY=/workspace/app/server/build/libs/dependency
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
    "org.elaastic.ElaasticServerKt", \
    "--spring.config.additional-location=file:/configuration/" \
]
