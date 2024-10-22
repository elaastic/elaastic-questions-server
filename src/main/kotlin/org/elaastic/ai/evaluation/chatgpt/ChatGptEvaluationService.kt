package org.elaastic.ai.evaluation.chatgpt

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.elaastic.ai.evaluation.chatgpt.api.ChatGptApiMessageData
import org.elaastic.ai.evaluation.chatgpt.api.ChatGptCompletionService
import org.elaastic.ai.evaluation.chatgpt.prompt.ChatGptPrompt
import org.elaastic.ai.evaluation.chatgpt.prompt.ChatGptPromptService
import org.elaastic.common.util.requireAccess
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.report.ReportCandidateService
import org.elaastic.questions.directory.User
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.logging.Logger
import javax.persistence.EntityManager

@Service
class ChatGptEvaluationService(
    val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    val responseRepository: ResponseRepository,
    val chatGptPromptService: ChatGptPromptService,
    val reportCandidateService: ReportCandidateService,
    val responseService: ResponseService,
    val entityManager: EntityManager,
    val chatGptCompletionService: ChatGptCompletionService,
) {

    val logger = Logger.getLogger(ChatGptEvaluationService::class.java.name)

    /**
     * Create a ChatGPT evaluation for a response. The evaluation is created
     * asynchronously.
     *
     * @param response the response to evaluate
     * @param language the language of the evaluation
     * @param chatGptExistingEvaluation the existing evaluation if it exists
     * @return the created evaluation
     */
    @Async
    @Transactional(propagation = Propagation.NEVER)
    fun createEvaluation(
        response: Response,
        language: String,
        chatGptExistingEvaluation: ChatGptEvaluation? = null
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
            val generatedResponse = chatGptCompletionService.getChatGptResponse(
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
            logger.severe("Error while evaluating response with ChatGPT: ${e.message}")
        }
        return chatGptEvaluationRepository.save(chatGptEvaluation)
    }


    fun findEvaluationByResponse(response: Response): ChatGptEvaluation? =
        chatGptEvaluationRepository.findByResponse(response)

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
     * Update the utility grade associated with a chatGPT evaluation.
     *
     * @param chatGptEvaluation the chatGPT evaluation to update.
     * @param utilityGrade the utility grade.
     */
    fun changeUtilityGrade(chatGptEvaluation: ChatGptEvaluation, utilityGrade: UtilityGrade) {
        reportCandidateService.updateGrade(chatGptEvaluation, utilityGrade, chatGptEvaluationRepository)
    }

    /**
     * Update the report associated with a chatGPT evaluation.
     *
     * @param chatGptEvaluation the chatGPT evaluation to update.
     * @param reportReasons the reasons for the report.
     * @param reportComment the comment for the report.
     */
    fun reportEvaluation(
        chatGptEvaluation: ChatGptEvaluation,
        reportReasons: List<String>,
        reportComment: String? = null
    ) {
        reportCandidateService.updateReport(
            chatGptEvaluation,
            reportReasons,
            reportComment,
            chatGptEvaluationRepository
        )
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun markEvaluationAsPending(chatGptEvaluation: ChatGptEvaluation): ChatGptEvaluation {
        chatGptEvaluation.status = ChatGptEvaluationStatus.PENDING.name
        return chatGptEvaluationRepository.saveAndFlush(chatGptEvaluation)
    }

    /**
     * Check if the visibility of a chatGPT evaluation can be changed by the
     * given user
     *
     * A user can hide a chatGPT evaluation if the user is the teacher of the
     * sequence and if the evaluation is done.
     *
     * @param chatGptEvaluation the chatGPT evaluation to check.
     * @param user the user who wants to update the visibility of the
     *    evaluation.
     * @return true if the visibility of the chatGPT evaluation can be changed,
     *    false otherwise.
     */
    fun canUpdateVisibilityEvaluation(chatGptEvaluation: ChatGptEvaluation, user: User): Boolean {
        return responseService.canHidePeerGrading(
            user,
            chatGptEvaluation.response
        ) && chatGptEvaluation.status == ChatGptEvaluationStatus.DONE.name
    }

    /**
     * Hide a chatGPT evaluation.
     *
     * An user must have the permission to hide the evaluation. If the user
     * doesn't have the permission, an exception is thrown.
     *
     * @param chatGptEvaluation the chatGPT evaluation to hide.
     * @param user the user who wants to hide the evaluation.
     * @throws IllegalAccessException if the user doesn't have the permission
     *    to hide the evaluation.
     */
    @Throws(IllegalAccessException::class)
    fun markAsHidden(chatGptEvaluation: ChatGptEvaluation, user: User) {
        requireAccess(
            canUpdateVisibilityEvaluation(
                chatGptEvaluation,
                user
            )
        ) { "You don't have the permission to hide this evaluation" } // TODO make i18n
        reportCandidateService.markAsHidden(chatGptEvaluation, chatGptEvaluationRepository)
    }

    /**
     * Unhide a chatGPT evaluation.
     *
     * An user must have the permission to unhide the evaluation. If the user
     * doesn't have the permission, an exception is thrown.
     *
     * @param chatGPTEvaluation the chatGPT evaluation to unhide
     * @param user the user who wants to unhide the evaluation.
     * @throws IllegalAccessException if the user doesn't have the permission
     *    to unhide the evaluation.
     */
    @Throws(IllegalAccessException::class)
    fun markAsShown(chatGPTEvaluation: ChatGptEvaluation, user: User) {
        requireAccess(
            canUpdateVisibilityEvaluation(
                chatGPTEvaluation,
                user
            )
        ) { "You don't have the permission to unhide this evaluation" } // TODO make i18n
        reportCandidateService.markAsShown(chatGPTEvaluation, chatGptEvaluationRepository)
    }

    /**
     * With the given list of response ids, if the response has been evaluated
     * by ChatGPT, associate the evaluation to true. If the response has
     * not been evaluated by ChatGPT, associate the evaluation to false.
     *
     * @param listIdResponse the list of response ids to evaluate
     * @return a map with the response id as key and a boolean as value
     */
    fun associateResponseToChatGPTEvaluationExistence(listIdResponse: List<Long?>): Map<Long, Boolean> {
        entityManager.createQuery(
            """
            SELECT r.id, gpt.response.id
            FROM Response r
            LEFT JOIN ChatGptEvaluation gpt ON r.id = gpt.response.id
            WHERE r.id IN :listIdResponse
        """.trimIndent()
        )
            .setParameter("listIdResponse", listIdResponse)
            .resultList
            .map { it as Array<*> }
            .associate { (it[0] as Long) to (it[1] != null) }
            .let { return it }
    }

    /**
     * Find all the evaluations made on a sequence. We retrieve all the
     * responses of the sequence, and then we retrieve all the peer grading
     * that have been made on these responses.
     *
     * @param sequence the sequence.
     * @return the list of peer grading.
     */
    fun findAllBySequence(sequence: Sequence): List<ChatGptEvaluation> =
        chatGptEvaluationRepository.findAllByResponseIn(
            responseRepository.findAllByInteraction(
                sequence.getResponseSubmissionInteraction(),
            )
        )

    /**
     * Find all the evaluations made on a sequence that have been reported and
     * not hidden.
     *
     * @param sequence the sequence.
     * @return the list of peer grading.
     */
    fun findAllReportedNotHidden(sequence: Sequence): List<ChatGptEvaluation> {
        return chatGptEvaluationRepository.findAllByHiddenByTeacherIsFalseAndReportReasonsIsNotNullAndResponseIn(
            responseRepository.findAllByInteraction(
                sequence.getResponseSubmissionInteraction(),
            )
        )
    }

    /**
     * Count all the evaluations made on a sequence that have been reported and
     * not hidden.
     *
     * @param sequence the sequence.
     * @return the number of peer grading.
     */
    fun countAllReportedNotHidden(sequence: Sequence): Int {
        return chatGptEvaluationRepository.countAllByHiddenByTeacherIsFalseAndReportReasonsIsNotNullAndResponseIn(
            responseRepository.findAllByInteraction(
                sequence.getResponseSubmissionInteraction(),
            )
        )
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


data class PromptData(
    val questionTitle: String,
    val questionStatement: String,
    val teacherExplanation: String,
    val studentExplanation: String,
    val studentChoices: List<Int>? = null,
    val studentScoreBasedOnChoices: BigDecimal? = null,
)

data class ChatGptEvaluationData(
    @JsonProperty("grade") val grade: BigDecimal,
    @JsonProperty("annotation") val annotation: String,
)