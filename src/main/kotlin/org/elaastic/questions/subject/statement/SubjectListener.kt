package org.elaastic.questions.subject.statement

import org.elaastic.questions.rabbitmq.RabbitMQService
import org.elaastic.questions.subject.Subject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.sql.Timestamp
import java.time.Instant
import javax.persistence.PostPersist
import javax.persistence.PostRemove
import javax.persistence.PostUpdate

class SubjectListener(@Autowired val rabbitMQService: RabbitMQService) {

    @Value("\${elaastic.questions.url}")
    val elaasticQuestionUrl:String = ""

    private fun publicizeSubject(subject: Subject) {
        // Send a classic DateTime object (2024-10-05 09:55:44.123)
        val now = Timestamp.from(Instant.now())
        subject.lastUpdated = now
        rabbitMQService.publicizeSubject(subject, elaasticQuestionUrl)
    }

    @PostPersist
    fun handleSubjectPersist(subject: Subject) {
        // Mark this as a new entity
        subject.isNew = true
        if (subject.public) {
            publicizeSubject(subject)
        }
    }

    @PostUpdate
    fun handleSubjectUpdate(subject: Subject) {
        // Only publicize if it wasn't just created
        if (!subject.isNew && subject.public) {
            publicizeSubject(subject)
        } else if (!subject.public) {
            rabbitMQService.privatizeSubject(subject)
        }
        // Reset the flag after the update is handled
        subject.isNew = false
    }

    @PostRemove
    fun handleSubjectRemove(subject: Subject) {
        rabbitMQService.deleteSubject(subject)
    }
}
