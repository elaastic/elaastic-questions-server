package org.elaastic.ai.evaluation.chatgpt

import com.fasterxml.jackson.annotation.JsonProperty
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseRepository
import org.elaastic.activity.response.ResponseService
import org.elaastic.common.util.requireAccess
import org.elaastic.moderation.ReportCandidateService
import org.elaastic.moderation.UtilityGrade
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.user.User
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import java.util.logging.Logger
import javax.persistence.EntityManager

@Service
open class ChatGptEvaluationService(
    val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    val responseRepository: ResponseRepository,
    val reportCandidateService: ReportCandidateService,
    val responseService: ResponseService,
    val entityManager: EntityManager,
    val messageSource: MessageSource,
) {

    val logger = Logger.getLogger(ChatGptEvaluationService::class.java.name)
    val locale: Locale = LocaleContextHolder.getLocale()

    fun findEvaluationByResponse(response: Response): ChatGptEvaluation? =
        chatGptEvaluationRepository.findByResponse(response).takeIf { it?.removedByTeacher == false }

    fun findEvaluationById(id: Long): ChatGptEvaluation? {
        return chatGptEvaluationRepository.findById(id).orElse(null)?.takeIf { !it.removedByTeacher }
    }


    /**
     * Mark a ChatGPT evaluation as hidden by a teacher.
     *
     * @param chatGptEvaluation the ChatGPT evaluation to hide.
     */
    fun markAsHidden(chatGptEvaluation: ChatGptEvaluation) {
        reportCandidateService.markAsHidden(chatGptEvaluation, chatGptEvaluationRepository)
    }

    /**
     * Mark a ChatGPT evaluation as removed by a teacher.
     *
     * @param chatGptEvaluation the ChatGPT evaluation to remove.
     */
    fun markAsRemoved(user: User, chatGptEvaluation: ChatGptEvaluation) {
        requireAccess(user == chatGptEvaluation.response.interaction.owner) {
            "You don't have the permission to remove this evaluation"
        }

        reportCandidateService.markAsRemoved(chatGptEvaluation, chatGptEvaluationRepository)
    }

    /**
     * Update the utility grade associated with a ChatGPT evaluation.
     *
     * @param chatGptEvaluation the ChatGPT evaluation to update.
     * @param utilityGrade the utility grade.
     * @param isTeacher true if the user is a teacher.
     * If it's the teacher then the teacher utility grade [teacherUtilityGrade][ChatGptEvaluation.teacherUtilityGrade] is updated,
     * otherwise the candidate utility grade [utilityGrade][ChatGptEvaluation.utilityGrade] is updated.
     */
    fun changeUtilityGrade(
        chatGptEvaluation: ChatGptEvaluation,
        utilityGrade: UtilityGrade,
        isTeacher: Boolean = false
    ) {
        if (isTeacher) {
            chatGptEvaluation.teacherUtilityGrade = utilityGrade
            chatGptEvaluationRepository.save(chatGptEvaluation)
        } else {
            reportCandidateService.updateGrade(chatGptEvaluation, utilityGrade, chatGptEvaluationRepository)
        }
    }

    /**
     * Update the report associated with a ChatGPT evaluation.
     *
     * @param chatGptEvaluation the ChatGPT evaluation to update.
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

    /**
     * Check if the given user can change the visibility of a ChatGPT evaluation
     *
     * A user can hide a ChatGPT evaluation if the user is the teacher of the sequence and if the evaluation is done.
     *
     * @param chatGptEvaluation the ChatGPT evaluation to check.
     * @param user the user who wants to update the visibility of the evaluation.
     * @return true if the visibility of the ChatGPT evaluation can be changed, false otherwise.
     */
    fun canUpdateVisibilityEvaluation(chatGptEvaluation: ChatGptEvaluation, user: User): Boolean {
        return responseService.canHidePeerGrading(
            user,
            chatGptEvaluation.response
        ) && chatGptEvaluation.status == ChatGptEvaluationStatus.DONE.name
    }

    /**
     * Hide a ChatGPT evaluation.
     *
     * An user must have the permission to hide the evaluation. If the user doesn't have the permission, an exception is
     * thrown.
     *
     * @param chatGptEvaluation the ChatGPT evaluation to hide.
     * @param user the user who wants to hide the evaluation.
     * @throws IllegalAccessException if the user doesn't have the permission to hide the evaluation.
     */
    @Throws(IllegalAccessException::class)
    fun markAsHidden(chatGptEvaluation: ChatGptEvaluation, user: User) {
        requireAccess(
            canUpdateVisibilityEvaluation(
                chatGptEvaluation,
                user
            )
        ) { messageSource.getMessage("chatGPT.error.markAsHidden.accessDenied", null, locale) }
        reportCandidateService.markAsHidden(chatGptEvaluation, chatGptEvaluationRepository)
    }

    /**
     * Unhide a ChatGPT evaluation.
     *
     * An user must have the permission to unhide the evaluation. If the user doesn't have the permission, an exception
     * is thrown.
     *
     * @param chatGPTEvaluation the ChatGPT evaluation to unhide
     * @param user the user who wants to unhide the evaluation.
     * @throws IllegalAccessException if the user doesn't have the permission to unhide the evaluation.
     */
    @Throws(IllegalAccessException::class)
    fun markAsShown(chatGPTEvaluation: ChatGptEvaluation, user: User) {
        requireAccess(
            canUpdateVisibilityEvaluation(
                chatGPTEvaluation,
                user
            )
        ) { messageSource.getMessage("chatGPT.error.markAsShown.accessDenied", null, locale) }
        reportCandidateService.markAsShown(chatGPTEvaluation, chatGptEvaluationRepository)
    }

    /**
     * With the given list of response ids, if the response has been evaluated by ChatGPT, associate the evaluation to
     * true. If the response has not been evaluated by ChatGPT, associate the evaluation to false.
     *
     * @param listIdResponse the list of response ids to evaluate
     * @return a map with the response id as key and a boolean as value
     */
    fun associateResponseToChatGPTEvaluationExistence(listIdResponse: List<Long?>): ChatGptEvaluationResponseStore {
        val result = entityManager.createQuery(
            """
            SELECT r.id
            FROM Response r
            LEFT JOIN ChatGptEvaluation gpt ON r.id = gpt.response.id
            WHERE r.id IN :listIdResponse AND gpt.removedByTeacher = false
        """.trimIndent()
        )
            .setParameter("listIdResponse", listIdResponse)
            .resultList.filterNotNull().map { it as Long }

        return ChatGptEvaluationResponseStore(result)
    }

    /**
     * Find all the evaluations made on a sequence.
     * We retrieve all the responses of the sequence,
     * and then we retrieve all the peer grading that have been made on these responses.
     *
     * @param sequence the sequence.
     * @return the list of peer grading.
     */
    fun findAllBySequence(sequence: Sequence): List<ChatGptEvaluation> =
        chatGptEvaluationRepository.findAllByResponseIn(
            responseRepository.findAllByInteraction(
                sequence.getResponseSubmissionInteraction(),
            )
        ).filter { !it.removedByTeacher }

    /**
     * Find all the evaluations made on a sequence that have been reported and not removed.
     *
     * @param sequence the sequence.
     * @return the list of ChatGPTEvaluation.
     */
    fun findAllReportedNotRemoved(sequence: Sequence): List<ChatGptEvaluation> {
        return findAllReported(sequence, removed = false)
    }

    /** @see countAllReportedNotRemoved(Interaction) */
    fun countAllReportedNotRemoved(sequence: Sequence): Int {
        return countAllReportedNotRemoved(
            sequence.getResponseSubmissionInteraction(),
        )
    }

    /**
     * Count all the evaluations made on a sequence that have been reported and not hidden.
     *
     * @param interaction the interaction.
     * @return the number of peer grading.
     */
    fun countAllReportedNotRemoved(interaction: Interaction): Int {
        return countAllReported(interaction, removed = false)
    }

    fun countAllReported(sequence: Sequence, removed: Boolean): Int {
        return countAllReported(sequence.getResponseSubmissionInteraction(), removed)
    }

    fun countAllReported(interction: Interaction, removed: Boolean): Int {
        return chatGptEvaluationRepository.countAllReported(
            interction,
            removed
        )
    }

    fun findAllReportedRemoved(sequence: Sequence): List<ChatGptEvaluation> {
        return findAllReported(sequence, removed = true)
    }

    fun findAllReported(sequence: Sequence, removed: Boolean): List<ChatGptEvaluation> {
        return chatGptEvaluationRepository.findAllReported(
            sequence.getResponseSubmissionInteraction(),
            removed = removed
        )
    }

    /**
     * Remove the report associated with a ChatGPT evaluation.
     *
     * @param user the user who wants to remove the report.
     * @param chatGptEvaluation the ChatGPT evaluation to update.
     * @throws IllegalAccessException if the user doesn't have the permission to remove the report.
     */
    fun removeReport(
        user: User,
        chatGptEvaluation: ChatGptEvaluation
    ) {
        requireAccess(user == chatGptEvaluation.response.interaction.owner) {
            "You don't have the permission to remove the report"
        }

        chatGptEvaluation.reportReasons = null
        chatGptEvaluation.reportComment = null

        chatGptEvaluationRepository.save(chatGptEvaluation)
    }

    fun removeReport(user: User, id: Long) {
        val chatGptEvaluation = chatGptEvaluationRepository.findById(id).orElseThrow()
        removeReport(user, chatGptEvaluation)
    }

    fun markAsRestored(user: User, chatGptEvaluation: ChatGptEvaluation) {
        requireAccess(user == chatGptEvaluation.response.interaction.owner) {
            "You don't have the permission to restore this evaluation"
        }
        reportCandidateService.markAsRestored(chatGptEvaluation, chatGptEvaluationRepository)
    }

    /** Return the name of the IA use to generate the evaluation. */
    fun getAINameProvider(): String {
        return "ChatGPT"
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