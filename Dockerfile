# Production stage
FROM openjdk:17

ENV REFRESHED_AT 2023-08-25
ENV ELAASTIC_VERSION 5.1.5

# Create a non-root user
RUN useradd -m elaastic

# Initialize datastore
RUN mkdir -p /app/datastore && chown -R  elaastic:elaastic /app/datastore

USER elaastic

# Copy application JAR
COPY build/libs/elaastic-questions-server-$ELAASTIC_VERSION.jar /app/lib/elaastic-questions-server.jar


EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/lib/elaastic-questions-server.jar","-Djava.security.egd=file:/dev/./urandom","-Djava.io.tmpdir=/tmp/","--spring.config.additional-location=file:/app/configuration/"]
