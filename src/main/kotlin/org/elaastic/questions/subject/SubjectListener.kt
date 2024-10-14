package org.elaastic.questions.subject

import org.elaastic.questions.rabbitmq.RabbitMQService
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

    /**
     * Publicize a subject.
     * @param subject The subject to publicize.
     * TODO: check if lastUpdated could be set in right format without having to convert it to a Timestamp
     */
    private fun publicizeSubject(subject: Subject) {
        // Send a classic DateTime object (2024-10-05 09:55:44.123)
        val now = Timestamp.from(Instant.now())
        subject.lastUpdated = now
        rabbitMQService.publicizeSubject(subject, elaasticQuestionUrl)
    }

    /**
     * Handle the publicization of a subject after it is persisted.
     * @param subject The subject to persist.
     */
    @PostPersist
    fun handleSubjectPersist(subject: Subject) {
        // Mark this as a new entity
        subject.isNew = true
        if (subject.public) {
            publicizeSubject(subject)
        }
    }

    /**
     * Handle the publicization or privatization of a subject after it is updated.
     * @param subject The subject to update.
     */
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

    /**
     * Handle the deletion of a subject.
     * @param subject The subject to delete.
     */
    @PostRemove
    fun handleSubjectRemove(subject: Subject) {
        rabbitMQService.deleteSubject(subject)
    }
}
