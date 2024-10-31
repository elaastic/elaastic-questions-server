package org.elaastic.ai.evaluation.chatgpt.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.logging.Logger

@Service
class ChatGptCompletionService(
    val restTemplate: RestTemplate,
    val objectMapper: ObjectMapper,
    @Value("\${chatgptapi.token}")
    val apiKey: String,
    @Value("\${chatgptapi.model}")
    val model: String,
    @Value("\${chatgptapi.maxTokens}")
    val maxTokens: Int
) {
    companion object {
        const val apiUrl = "https://api.openai.com/v1/chat/completions"
        val module = SimpleModule().apply {
            addDeserializer(ChatGptApiResponseData::class.java, ChatGptApiDeserializer())
        }
        // logger for this class
        val logger = Logger.getLogger(ChatGptEvaluationService::class.java.name)
    }

    /**
     * Get the response from the ChatGPT API
     * @param messages List of messages to send to the API
     * @return ChatGptApiResponseData
     */
    fun getChatGptResponse(messages: List<ChatGptApiMessageData>, nParameter: Int = 1): ChatGptApiResponseData {
        val requestBody = getRequestBody(messages, nParameter)
        val entity = HttpEntity(requestBody, getHeaders())
        val responseEntity = restTemplate.postForEntity(apiUrl, entity, String::class.java)
        objectMapper.registerModule(module)
        return objectMapper.readValue(responseEntity.body, ChatGptApiResponseData::class.java)
    }

    private fun getHeaders() = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
        set("Authorization", "Bearer $apiKey")
    }

    private fun getRequestBody(messages: List<ChatGptApiMessageData>, nParameter: Int=1): String {
        val requestBody = mapOf(
            "model" to model,
            "messages" to messages,
            "max_tokens" to maxTokens,
            "n" to nParameter,
        )
        return objectMapper.writeValueAsString(requestBody)
    }

}