package org.elaastic.questions.rabbitmq

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RabbitMQSender(@Autowired private val rabbitTemplate: RabbitTemplate) {

    fun sendMessage(message: String) {
        println("Sending message: $message")
        rabbitTemplate.convertAndSend("elaastic", "elaastic", message)
    }
}
