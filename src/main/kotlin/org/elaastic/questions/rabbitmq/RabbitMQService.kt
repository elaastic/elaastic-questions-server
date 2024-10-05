package org.elaastic.questions.rabbitmq

import org.elaastic.questions.subject.Subject
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RabbitMQService(
    @Autowired private val rabbitTemplate: RabbitTemplate,
    @Value("\${rabbitmq.elaastic.exchange}") private val exchange: String,
    @Value("\${rabbitmq.elaastic.queue}") private val queueName: String
) {
    fun publicizeSubject(subject: Subject, serverUrl: String?) {
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

    private fun send(message: String) {
        try {
            rabbitTemplate.convertAndSend(exchange, queueName, message)
        } catch (e: Exception) {
            println("Error sending message: $e")
        }
    }
}
