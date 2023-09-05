package org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation.chatgptApi

data class ChatgptApiResponseData (
    val id: String,
    val objectValue: String,
    val created: Long,
    val model: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val message: ChatgptApiMessageData,
    val finishReason: String,
)