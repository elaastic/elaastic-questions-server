package org.elaastic.questions.rabbitmq

import org.elaastic.questions.subject.Subject
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * Service to interact with RabbitMQ.
 * @param rabbitTemplate The RabbitMQ template.
 * @param exchange The exchange to use.
 * @param queueName The queue to use.
 */
@Service
class RabbitMQService(
    @Autowired private val rabbitTemplate: RabbitTemplate,
    @Value("\${rabbitmq.elaastic.exchange}") private val exchange: String,
    @Value("\${rabbitmq.elaastic.queue}") private val queueName: String
) {
    /**
     * Send a message to publicize a subject.
     * @param subject The subject to publicize.
     * @param serverUrl The URL of the server.
     */
    fun publicizeSubject(subject: Subject, serverUrl: String) {
        val message = """
            {
                "action": "publicize",
                "data": {
                    "title": "${subject.title}",
                    "owner": "${subject.owner}",
                    "last_updated": "${subject.lastUpdated}",
                    "uuid": "${subject.globalId}",
                    "link": "${serverUrl}subject/shared?globalId=${subject.globalId}",
                    "description": "${subject.description}"
                }
            }
        """.trimIndent()

        this.send(message)
    }

    /**
     * Send a message to privatize a subject.
     * @param subject The subject to privatize.
     */
    fun privatizeSubject(subject: Subject) {
        val message = """
            {
                "action": "privatize",
                "data": {
                    "uuid": "${subject.globalId}"
                }
            }
        """.trimIndent()

        this.send(message)
    }

    /**
     * Send a message to delete a subject.
     * @param subject The subject to delete.
     */
    fun deleteSubject(subject: Subject) {
        val message = """
            {
                "action": "delete",
                "data": {
                    "uuid": "${subject.globalId}"
                }
            }
        """.trimIndent()
        this.send(message)
    }

    /**
     * Send a message to the RabbitMQ server.
     * @param message The message to send.
     */
    private fun send(message: String) {
        try {
            rabbitTemplate.convertAndSend(exchange, queueName, message)
        } catch (e: Exception) {
            println("Error sending message: $e")
        }
    }
}
