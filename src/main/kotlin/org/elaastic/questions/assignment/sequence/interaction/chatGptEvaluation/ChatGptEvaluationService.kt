package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import com.fasterxml.jackson.databind.ObjectMapper
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptApi.ChatGptApiClient
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptPrompt.ChatGptPromptService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Service
class ChatGptEvaluationService (
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val chatGptApiClient: ChatGptApiClient,
    @Autowired val chatGptPromptService: ChatGptPromptService,
    @Autowired @Lazy val self: ChatGptEvaluationService // self-injection to bypass Spring AOP limitations to create a new transaction
) {


    @Async
    @Transactional
    fun createEvaluation(response: Response, chatGptExistingEvaluation : ChatGptEvaluation? = null): ChatGptEvaluation {


        val language = LocaleContextHolder.getLocale().language
        val title = response.statement.title
        val questionContent = response.statement.content
        val teacherExplanation = response.statement.expectedExplanation
        val studentExplanation = response.explanation

        requireNotNull(teacherExplanation){ throw IllegalArgumentException("Error: You must define an expected explanation to create a ChatGPT evaluation") }
        requireNotNull(studentExplanation){ throw IllegalArgumentException("Error: No explanation to evaluate") }

        val chatGptEvaluation = chatGptExistingEvaluation ?: ChatGptEvaluation(response = response)
        chatGptEvaluation.status = ChatGptEvaluationStatus.PENDING.name

        val savedChatGptEvaluation = self.saveEvaluation(chatGptEvaluation)

        val regexHtml = Regex("<.*?>")
        val chatGptPrompt = chatGptPromptService.getPrompt(language)

        // TODO Review JT : This deserves a dedicated method
        val prompt = chatGptPrompt.content
            .replace("\${title}", title.replace(regexHtml, ""))
            .replace("\${questionContent}", questionContent.replace(regexHtml, ""))
            .replace("\${teacherExplanation}", teacherExplanation.replace(regexHtml, ""))
            .replace("\${studentExplanation}", studentExplanation.replace(regexHtml, ""))

        try {
            val generatedResponse = chatGptApiClient.generateResponseFromPrompt(prompt)

            val regexGrade = Regex("""(?:Note)?\s*:?\s*\[(\d+(?:[.,]\d+)?)(?:/5)?]""")

            val annotation = regexGrade.replace(generatedResponse, "")

            val matchResult = regexGrade.find(generatedResponse)
            val grade = matchResult?.groupValues?.last()?.toBigDecimal()

            savedChatGptEvaluation.status = ChatGptEvaluationStatus.DONE.name
            savedChatGptEvaluation.grade = grade
            savedChatGptEvaluation.annotation = annotation

        } catch (e: Exception) {
            savedChatGptEvaluation.status = ChatGptEvaluationStatus.ERROR.name
        }

        return chatGptEvaluationRepository.save(savedChatGptEvaluation)

    }

    fun findEvaluationByResponse(response: Response?): ChatGptEvaluation? {
        // TODO Review JT : Code smell ==> How comes one can ask for findEvaluationByResponse(null) ?
        if (response == null) {
            return null
        }
        return chatGptEvaluationRepository.findByResponse(response)
    }

    fun findEvaluationById(id: Long): ChatGptEvaluation? {
        return chatGptEvaluationRepository.findById(id).get()
    }

    fun changeUtilityGrade(chatGptEvaluation: ChatGptEvaluation, utilityGrade: UtilityGrade) {
        chatGptEvaluation.utilityGrade = utilityGrade
        chatGptEvaluationRepository.save(chatGptEvaluation)
    }

    fun reportEvaluation(chatGptEvaluation: ChatGptEvaluation, reportReasons: List<String>, reportComment : String? = null) {
        val objectMapper = ObjectMapper()
        val jsonString = objectMapper.writeValueAsString(reportReasons)
        chatGptEvaluation.reportReasons = jsonString
        chatGptEvaluation.reportComment = reportComment
        chatGptEvaluationRepository.save(chatGptEvaluation)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveEvaluation(chatGptEvaluation: ChatGptEvaluation) = chatGptEvaluationRepository.saveAndFlush(chatGptEvaluation)
}