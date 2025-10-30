package org.elaastic.ai.evaluation.chatgpt.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Profile

@Profile("chatgpt")
@ConfigurationProperties(prefix = "chatgptapi")
data class ChatGptApiConfiguration

@ConstructorBinding constructor(
    val token: String,
    val model: String,
    val maxTokens: Int,
)