package org.elaastic.ai.evaluation.chatgpt.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class ChatGptApiDeserializer : JsonDeserializer<ChatGptApiResponseData>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ChatGptApiResponseData {
        val node: JsonNode = p.codec.readTree(p)
        val usage: JsonNode = node["usage"]
        val messageList = node["choices"].map {
            ChatGptApiMessageData(
                role = it["message"]["role"].asText(),
                content = it["message"]["content"].asText(),
                finishReason = it["finish_reason"].asText()
            )
        }


        return ChatGptApiResponseData(
            id = node["id"].asText(),
            objectValue = node["object"].asText(),
            created = node["created"].asLong(),
            model = node["model"].asText(),
            promptTokens = usage["prompt_tokens"].asInt(),
            completionTokens = usage["completion_tokens"].asInt(),
            totalTokens = usage["total_tokens"].asInt(),
            messageList = messageList,
        )
    }
}