package org.elaastic.questions.rabbitmq

import org.elaastic.questions.controller.ControllerUtil.getServerBaseUrl
import org.elaastic.questions.subject.Subject
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class RabbitMQService(@Autowired private val rabbitTemplate: RabbitTemplate) {

    fun publicizeSubject(subject: Subject, request: HttpServletRequest) {
        val serverUrl = getServerBaseUrl(request)
        val message = """
            {
                "title": "${subject.title}",
                "owner": "${subject.owner}",
                "last_updated": "${subject.lastUpdated}",
                "uuid": "${subject.globalId}",
                "link": "${serverUrl}/subject/shared?globalId=${subject.globalId}"
            }
        """.trimIndent()
        rabbitTemplate.convertAndSend("elaastic", "elaastic", message)
    }

    fun privatizeSubject(subject: Subject) {
        val message = """
        {
            "uuid": "${subject.globalId}",
            "private": true
        }
        """.trimIndent()
        rabbitTemplate.convertAndSend("elaastic", "elaastic", message)
    }

}
