# Production stage
FROM openjdk:17

# Create a non-root user
RUN useradd -m elaastic
USER elaastic

VOLUME /tmp

# Copy application JAR
COPY build/libs/*.jar /app/lib/

EXPOSE 8080

ENTRYPOINT ["java","-cp","app:app/lib/*","org.elaastic.questions.ElaasticQuestionsServer","-Djava.security.egd=file:/dev/./urandom","-Dspring.config.additional-location=file:/config/"]
