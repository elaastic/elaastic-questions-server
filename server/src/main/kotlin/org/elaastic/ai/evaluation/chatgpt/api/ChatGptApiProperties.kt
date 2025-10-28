package org.elaastic.ai.evaluation.chatgpt.api

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "chatgptapi")
class ChatGptApiProperties(
    val token: String,
    val model: String,
    val maxTokens: Int,
)