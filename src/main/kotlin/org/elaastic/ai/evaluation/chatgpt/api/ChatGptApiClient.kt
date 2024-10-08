package org.elaastic.ai.evaluation.chatgpt.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
class ChatGptApiClient(

    @Value("\${chatgptapi.token}")
    val apiToken: String,

    @Value("\${chatgptapi.model}")
    val model: String,

    @Value("\${chatgptapi.maxTokens}")
    val maxTokens: Int,

) {

    private fun get(

        model: String,
        messages: List<ChatGptApiMessageData>,
        maxTokens: Int

    ): ChatGptApiResponseData {

        val url = "https://api.openai.com/v1/chat/completions"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiToken")
        }

        val requestBody = mapOf(
            "model" to model,
            "messages" to messages,
            "max_tokens" to maxTokens
        )

        val objectMapper = ObjectMapper()
        val body = objectMapper.writeValueAsString(requestBody)

        val restTemplate = RestTemplate()
        val requestEntity = RequestEntity<Any>(body, headers, HttpMethod.POST, URI(url))

        val responseEntity = restTemplate.exchange(requestEntity, String::class.java)

        val module = SimpleModule()
        // TODO Review JT : It looks weird to reconstruct the tooling for querying at each query. I guess we should define a service for that
        module.addDeserializer(ChatGptApiResponseData::class.java, ChatGptApiDeserializer())
        objectMapper.registerModule(module)

        return objectMapper.readValue(responseEntity.body, ChatGptApiResponseData::class.java)

    }

    fun generateResponseFromPrompt(
        prompt : String,
    ): String {
        val response = get(model, listOf(ChatGptApiMessageData("user",prompt)), maxTokens)
        return response?.message?.content
    }

}