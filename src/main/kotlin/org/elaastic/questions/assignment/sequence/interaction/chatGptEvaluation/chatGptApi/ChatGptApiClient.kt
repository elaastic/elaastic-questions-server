package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptApi

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

    @Value("\${chatgptapi.orgtoken}")
    val orgToken: String,

) {

    private fun getApi(

        model: String,
        messages: List<ChatGptApiMessageData>,
        maxTokens: Int

    ): ChatGptApiResponseData? {

        val url = "https://api.openai.com/v1/chat/completions"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Bearer $apiToken")
            set("OpenAI-Organization", orgToken)
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

        return try {
            val responseEntity = restTemplate.exchange(requestEntity, String::class.java)

            val module = SimpleModule()
            module.addDeserializer(ChatGptApiResponseData::class.java, ChatGptApiDeserializer())
            objectMapper.registerModule(module)

            objectMapper.readValue(responseEntity.body, ChatGptApiResponseData::class.java)
        } catch (e: Exception) {
            println("An exception occurred in API request: ${e.message}")
            println(requestEntity.headers.toString())
            println(requestEntity.body.toString())
            null
        }

    }

    fun generateResponseFromPrompt(
        prompt : String,
        model: String = "gpt-3.5-turbo",
        maxTokens: Int = 2000
    ): String? {
        val response = getApi(model, listOf(ChatGptApiMessageData("user",prompt)), maxTokens)
        return response?.message?.content
    }

}