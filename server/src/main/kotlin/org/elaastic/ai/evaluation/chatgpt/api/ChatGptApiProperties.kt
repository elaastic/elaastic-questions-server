package org.elaastic.ai.evaluation.chatgpt.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile

@Profile("chatgpt")
@ConfigurationProperties(prefix = "chatgptapi")
class ChatGptApiProperties(
    val token: String,
    val model: String,
    val maxTokens: Int,
)