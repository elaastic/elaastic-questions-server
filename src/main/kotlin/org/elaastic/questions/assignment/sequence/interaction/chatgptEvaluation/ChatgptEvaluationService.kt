package org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation

import org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation.chatgptApi.ChatgptApiClient
import org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation.chatgptPrompt.ChatgptPromptService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class ChatgptEvaluationService (
    @Autowired val chatgptEvaluationRepository: ChatgptEvaluationRepository,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val chatgptApiClient: ChatgptApiClient,
    @Autowired val chatgptPromptService: ChatgptPromptService,
    @Autowired val chatgptEvaluationSaver: ChatgptEvaluationSaver
) {
    @Async
    @Transactional
    fun createEvaluation(response: Response, chatgptExistingEvaluation : ChatgptEvaluation? = null) {

        val language = LocaleContextHolder.getLocale().language
        val title = response.statement.title
        val questionContent = response.statement.content
        val teacherExplanation = response.statement.expectedExplanation
        val studentExplanation = response.explanation

        requireNotNull(teacherExplanation){ throw IllegalArgumentException("Error: You must define an expected explanation to create a ChatGPT evaluation") }
        requireNotNull(studentExplanation){ throw IllegalArgumentException("Error: No explanation to evaluate") }

        val chatgptEvaluation = chatgptExistingEvaluation ?: ChatgptEvaluation(response = response)
        chatgptEvaluation.status = "pending"

        val savedChatgptEvaluation = chatgptEvaluationSaver.saveEvaluation(chatgptEvaluation)

        val regexHtml = Regex("<.*?>")
        val chatgptPrompt = chatgptPromptService.getPrompt(language)
        val prompt = chatgptPrompt.content
            .replace("\${title}", title.replace(regexHtml, ""))
            .replace("\${questionContent}", questionContent.replace(regexHtml, ""))
            .replace("\${teacherExplanation}", teacherExplanation.replace(regexHtml, ""))
            .replace("\${studentExplanation}", studentExplanation.replace(regexHtml, ""))

        val generatedResponse = chatgptApiClient.generateResponseFromPrompt(prompt)

        if (generatedResponse == null) {
            savedChatgptEvaluation.status = "error"
        }
        else {
            val regexGrade = Regex("""(?:Note)?\s*:?\s*\[(\d+(?:[.,]\d+)?)(?:/5)?]""")

            val annotation = regexGrade.replace(generatedResponse, "")

            val matchResult = regexGrade.find(generatedResponse)
            val grade = matchResult?.groupValues?.last()?.toBigDecimal()

            savedChatgptEvaluation.status = "done"
            savedChatgptEvaluation.grade = grade
            savedChatgptEvaluation.annotation = annotation
        }

        chatgptEvaluationRepository.save(savedChatgptEvaluation)
    }

    fun findEvaluationByResponse(response: Response?): ChatgptEvaluation? {
        if (response == null) {
            return null
        }
        return chatgptEvaluationRepository.findByResponse(response)
    }

}