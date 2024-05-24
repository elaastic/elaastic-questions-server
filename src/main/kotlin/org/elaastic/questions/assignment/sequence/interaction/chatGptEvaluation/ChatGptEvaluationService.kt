package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.report.ReportCandidateService
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptApi.ChatGptApiClient
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptPrompt.ChatGptPromptService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ChatGptEvaluationService (
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val chatGptApiClient: ChatGptApiClient,
    @Autowired val chatGptPromptService: ChatGptPromptService,
    @Autowired val reportCandidateService: ReportCandidateService
) {


    @Async
    @Transactional(propagation = Propagation.NEVER)
    fun createEvaluation(response: Response, language: String, chatGptExistingEvaluation : ChatGptEvaluation? = null): ChatGptEvaluation {

        val title = response.statement.title
        val questionContent = response.statement.content
        val teacherExplanation = response.statement.expectedExplanation
        val studentExplanation = response.explanation

        requireNotNull(teacherExplanation){ throw IllegalArgumentException("Error: You must define an expected explanation to create a ChatGPT evaluation") }
        requireNotNull(studentExplanation){ throw IllegalArgumentException("Error: No explanation to evaluate") }

        val chatGptEvaluation = chatGptExistingEvaluation ?: ChatGptEvaluation(response = response)
        markEvaluationAsPending(chatGptEvaluation)

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

            chatGptEvaluation.status = ChatGptEvaluationStatus.DONE.name
            chatGptEvaluation.grade = grade
            chatGptEvaluation.annotation = annotation

        } catch (e: Exception) {
            chatGptEvaluation.status = ChatGptEvaluationStatus.ERROR.name
        }

        return chatGptEvaluationRepository.save(chatGptEvaluation)

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


    /**
     * Mark a chatGPT evaluation as hidden by a teacher.
     *
     * @param chatGptEvaluation the chatGPT evaluation to hide.
     */
    fun markAsHidden(chatGptEvaluation: ChatGptEvaluation) {
        reportCandidateService.markAsHidden(chatGptEvaluation, chatGptEvaluationRepository)
    }

    /**
     * Mark a chatGPT evaluation as removed by a teacher.
     *
     * @param chatGptEvaluation the chatGPT evaluation to remove.
     */
    fun markAsRemoved(chatGptEvaluation: ChatGptEvaluation) {
        reportCandidateService.markAsRemoved(chatGptEvaluation, chatGptEvaluationRepository)
    }

    /**
     *  Update the utility grade associated with a chatGPT evaluation.
     *
     *  @param chatGptEvaluation the chatGPT evaluation to update.
     *  @param utilityGrade the utility grade.
     */
    fun changeUtilityGrade(chatGptEvaluation: ChatGptEvaluation, utilityGrade: UtilityGrade) {
        reportCandidateService.updateGrade(chatGptEvaluation, utilityGrade, chatGptEvaluationRepository)
    }

    /**
     *  Update the report associated with a chatGPT evaluation.
     *
     *  @param chatGptEvaluation the chatGPT evaluation to update.
     *  @param reportReasons the reasons for the report.
     *  @param reportComment the comment for the report.
     */
    fun reportEvaluation(chatGptEvaluation: ChatGptEvaluation, reportReasons: List<String>, reportComment : String? = null) {
        reportCandidateService.updateReport(chatGptEvaluation, reportReasons, reportComment, chatGptEvaluationRepository)
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun markEvaluationAsPending(chatGptEvaluation: ChatGptEvaluation) : ChatGptEvaluation {
        chatGptEvaluation.status = ChatGptEvaluationStatus.PENDING.name
        return chatGptEvaluationRepository.saveAndFlush(chatGptEvaluation)
    }

    /**
     * Check if a chatGPT evaluation can be hidden.
     *
     *
     *
     * @param chatGptEvaluation the chatGPT evaluation to check.
     * @param user the user who wants to hide the evaluation.
     * @return true if the chatGPT evaluation can be hidden, false otherwise.
     */
    fun canHideEvaluation(chatGptEvaluation: ChatGptEvaluation, user: User): Boolean {
        return false //STUB
    }
}