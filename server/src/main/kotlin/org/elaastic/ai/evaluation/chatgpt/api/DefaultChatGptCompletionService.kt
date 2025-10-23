/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.elaastic.ai.evaluation.chatgpt.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.logging.Logger

@Profile("chatgpt")
@Service
class DefaultChatGptCompletionService(
    val restTemplate: RestTemplate,
    val objectMapper: ObjectMapper,
    @Value("\${chatgptapi.token}")
    val apiKey: String,
    @Value("\${chatgptapi.model}")
    val model: String,
    @Value("\${chatgptapi.maxTokens}")
    val maxTokens: Int
) : ChatGptCompletionService {
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
    override fun getChatGptResponse(messages: List<ChatGptApiMessageData>, nParameter: Int): ChatGptApiResponseData {
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