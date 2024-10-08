package org.elaastic.ai.evaluation.chatgpt.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class ChatGptApiDeserializer : JsonDeserializer<ChatGptApiResponseData>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ChatGptApiResponseData {
        val node: JsonNode = p.codec.readTree(p)
        val usage: JsonNode = node["usage"]
        val firstChoice: JsonNode = node["choices"][0]
        val messageNode: JsonNode = firstChoice["message"]

        return ChatGptApiResponseData(
            id = node["id"].asText(),
            objectValue = node["object"].asText(),
            created = node["created"].asLong(),
            model = node["model"].asText(),
            promptTokens = usage["prompt_tokens"].asInt(),
            completionTokens = usage["completion_tokens"].asInt(),
            totalTokens = usage["total_tokens"].asInt(),
            ChatGptApiMessageData(
                role = messageNode["role"].asText(),
                content = messageNode["content"].asText()
            ),
            finishReason = firstChoice["finish_reason"].asText()
        )
    }
}