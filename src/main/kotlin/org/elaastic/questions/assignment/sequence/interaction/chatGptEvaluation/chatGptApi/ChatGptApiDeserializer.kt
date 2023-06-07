package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptApi

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class ChatGptApiDeserializer : JsonDeserializer<ChatGptApiResponseData>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ChatGptApiResponseData {
        val node: JsonNode = p.codec.readTree(p)
        val id: String = node.get("id").asText()
        val objectValue: String = node.get("object").asText()
        val created: Long = node.get("created").asLong()
        val model: String = node.get("model").asText()
        val usage: JsonNode = node.get("usage")
        val promptTokens: Int = usage.get("prompt_tokens").asInt()
        val completionTokens: Int = usage.get("completion_tokens").asInt()
        val totalTokens: Int = usage.get("total_tokens").asInt()
        val firstChoice: JsonNode = node.get("choices").get(0)
        val messageNode: JsonNode = firstChoice.get("message")
        val finishReason: String = firstChoice.get("finish_reason").asText()
        //val index: Int = firstChoice.get("index").asInt()
        val messageContent: String = messageNode.get("content").asText()
        val messageRole: String = messageNode.get("role").asText()
        return ChatGptApiResponseData(
            id,
            objectValue,
            created,
            model,
            promptTokens,
            completionTokens,
            totalTokens,
            ChatGptApiMessageData(messageRole, messageContent),
            finishReason
        )
    }
}