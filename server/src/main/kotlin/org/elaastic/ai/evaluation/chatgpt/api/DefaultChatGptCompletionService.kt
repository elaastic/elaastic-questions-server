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
import org.elaastic.activity.response.Response
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluation
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationData
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationRepository
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationStatus
import org.elaastic.ai.evaluation.chatgpt.PromptData
import org.elaastic.ai.evaluation.chatgpt.prompt.ChatGptPrompt
import org.elaastic.ai.evaluation.chatgpt.prompt.ChatGptPromptService
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.util.logging.Level
import java.util.logging.Logger

@Profile("chatgpt")
@Service
open class DefaultChatGptCompletionService(
    val chatGptApiConfiguration: ChatGptApiConfiguration,
    val chatGptPromptService: ChatGptPromptService,
    val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    val restTemplate: RestTemplate,
    val objectMapper: ObjectMapper,
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
     * Create a ChatGPT evaluation for a response. The evaluation is created asynchronously.
     *
     * @param response the response to evaluate
     * @param language the language of the evaluation
     * @param chatGptExistingEvaluation the existing evaluation if it exists
     * @return the created evaluation
     */
    @Async
    @Transactional(propagation = Propagation.NEVER)
    override fun createEvaluation(
        response: Response,
        language: String,
        chatGptExistingEvaluation: ChatGptEvaluation?
    ): ChatGptEvaluation {
        // get the default prompt for the language
        val chatGptDefaultPrompt = chatGptPromptService.getPrompt(language)
        // Initialization of the evaluation
        val chatGptEvaluation = chatGptExistingEvaluation ?: ChatGptEvaluation(response = response)
        chatGptEvaluation.prompt = chatGptDefaultPrompt
        markEvaluationAsPending(chatGptEvaluation)
        // build the prompt
        val prompt = buildThePrompt(chatGptDefaultPrompt, response)
        // build the evaluation
        try {
            // get the response from ChatGPT
            logger.info("Generating response with ChatGPT for response ${response.id}")
            logger.fine("Prompt: $prompt")
            val generatedResponse = getChatGptResponse(
                listOf(ChatGptApiMessageData(role = "user", content = prompt)),
            ).messageList.first().content
            logger.info("Response generated with ChatGPT for response ${response.id}")
            logger.fine("Generated response: $generatedResponse")
            // convert the generated response to a ChatGptEvaluationData object
            val chatGptEvaluationData = ObjectMapper().readValue(
                generatedResponse,
                ChatGptEvaluationData::class.java
            )
            // finalize the evaluation
            chatGptEvaluation.status = ChatGptEvaluationStatus.DONE.name
            chatGptEvaluation.grade = chatGptEvaluationData.grade
            chatGptEvaluation.annotation = chatGptEvaluationData.annotation
        } catch (e: Exception) {
            chatGptEvaluation.status = ChatGptEvaluationStatus.ERROR.name
            logger.log(Level.SEVERE, "Error while evaluating response with ChatGPT: ${e.message}", e)
        }
        return chatGptEvaluationRepository.save(chatGptEvaluation)
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    open fun markEvaluationAsPending(chatGptEvaluation: ChatGptEvaluation): ChatGptEvaluation {
        chatGptEvaluation.status = ChatGptEvaluationStatus.PENDING.name
        return chatGptEvaluationRepository.saveAndFlush(chatGptEvaluation)
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
        set("Authorization", "Bearer ${chatGptApiConfiguration.token}")
    }

    private fun getRequestBody(messages: List<ChatGptApiMessageData>, nParameter: Int=1): String {
        val requestBody = mapOf(
            "model" to chatGptApiConfiguration.model,
            "messages" to messages,
            "max_tokens" to chatGptApiConfiguration.maxTokens,
            "n" to nParameter,
        )
        return objectMapper.writeValueAsString(requestBody)
    }

    private fun buildThePrompt(
        chatGptPrompt: ChatGptPrompt,
        response: Response
    ): String {
        val questionTitle = response.statement.title
        val questionStatement = response.statement.content
        val teacherExplanation = response.statement.expectedExplanation
        val studentExplanation = response.explanation

        requireNotNull(teacherExplanation) { throw IllegalArgumentException("Error: You must define an expected explanation to create a ChatGPT evaluation") }
        requireNotNull(studentExplanation) { throw IllegalArgumentException("Error: No explanation to evaluate") }

        // add to the prompt the title, the question content, the teacher explanation and the student explanation as a json object
        val promptData = PromptData(
            questionTitle = questionTitle,
            questionStatement = questionStatement,
            teacherExplanation = teacherExplanation,
            studentExplanation = studentExplanation,
            studentChoices = response.learnerChoice,
            studentScoreBasedOnChoices = response.score,
        )

        val objectMapper = ObjectMapper()
        val jsonObject = objectMapper.writeValueAsString(promptData)

        return chatGptPrompt.content + "\n" + jsonObject
    }
}