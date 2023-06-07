package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptApi.ChatGptApiClient
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptPrompt.ChatGptPromptService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class ChatGptEvaluationService (
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val chatGptApiClient: ChatGptApiClient,
    @Autowired val chatGptPromptService: ChatGptPromptService,
    @Autowired val chatGptEvaluationSaver: ChatGptEvaluationSaver
) {
    @Async
    @Transactional
    fun createEvaluation(response: Response, chatGptExistingEvaluation : ChatGptEvaluation? = null) {

        val language = LocaleContextHolder.getLocale().language
        val title = response.statement.title
        val questionContent = response.statement.content
        val teacherExplanation = response.statement.expectedExplanation
        val studentExplanation = response.explanation

        requireNotNull(teacherExplanation){ throw IllegalArgumentException("Error: You must define an expected explanation to create a ChatGPT evaluation") }
        requireNotNull(studentExplanation){ throw IllegalArgumentException("Error: No explanation to evaluate") }

        val chatGptEvaluation = chatGptExistingEvaluation ?: ChatGptEvaluation(response = response)
        chatGptEvaluation.status = "pending"

        val savedChatGptEvaluation = chatGptEvaluationSaver.saveEvaluation(chatGptEvaluation)

        val regexHtml = Regex("<.*?>")
        val chatGptPrompt = chatGptPromptService.getPrompt(language)
        val prompt = chatGptPrompt.content
            .replace("\${title}", title.replace(regexHtml, ""))
            .replace("\${questionContent}", questionContent.replace(regexHtml, ""))
            .replace("\${teacherExplanation}", teacherExplanation.replace(regexHtml, ""))
            .replace("\${studentExplanation}", studentExplanation.replace(regexHtml, ""))

        val generatedResponse = chatGptApiClient.generateResponseFromPrompt(prompt)

        if (generatedResponse == null) {
            savedChatGptEvaluation.status = "error"
        }
        else {
            val regexGrade = Regex("""(?:Note)?\s*:?\s*\[(\d+(?:[.,]\d+)?)(?:/5)?]""")

            val annotation = regexGrade.replace(generatedResponse, "")

            val matchResult = regexGrade.find(generatedResponse)
            val grade = matchResult?.groupValues?.last()?.toBigDecimal()

            savedChatGptEvaluation.status = "done"
            savedChatGptEvaluation.grade = grade
            savedChatGptEvaluation.annotation = annotation
        }

        chatGptEvaluationRepository.save(savedChatGptEvaluation)
    }

    fun findEvaluationByResponse(response: Response?): ChatGptEvaluation? {
        if (response == null) {
            return null
        }
        return chatGptEvaluationRepository.findByResponse(response)
    }

}