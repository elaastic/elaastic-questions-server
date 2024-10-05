package org.elaastic.questions.rabbitmq

import org.elaastic.questions.controller.ControllerUtil.getServerBaseUrl
import org.elaastic.questions.subject.Subject
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class RabbitMQService(@Autowired private val rabbitTemplate: RabbitTemplate) {

    @Value("\${rabbitmq.elaastic.exchange}")
    val exchange:String = ""

    @Value("\${rabbitmq.elaastic.routing.key}")
    val routingKey:String = ""

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
        rabbitTemplate.convertAndSend(exchange, routingKey, message)
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
        rabbitTemplate.convertAndSend("elaastic", "elaastic", message)
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
        rabbitTemplate.convertAndSend("elaastic", "elaastic", message)
    }

}
