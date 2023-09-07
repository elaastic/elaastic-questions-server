package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptApi

data class ChatGptApiResponseData (
    val id: String,
    val objectValue: String,
    val created: Long,
    val model: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val message: ChatGptApiMessageData,
    val finishReason: String,
)