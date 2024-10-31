package org.elaastic.ai.evaluation.chatgpt.api

data class ChatGptApiResponseData (
    val id: String,
    val objectValue: String,
    val created: Long,
    val model: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val messageList: List<ChatGptApiMessageData>,
) {
    fun toSimpleString(): String {
        return messageList.joinToString("\n") { it.content }
    }
}