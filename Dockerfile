# Production stage
FROM openjdk:17

# Create a non-root user
RUN useradd -m elaastic
USER elaastic

VOLUME /tmp

# Copy application JAR
COPY build/libs/elaastic-questions-server-5.1.5.jar /app/lib/elaastic-questions-server.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/lib/elaastic-questions-server.jar","-Djava.security.egd=file:/dev/./urandom","--spring.config.additional-location=file:/configuration/"]
