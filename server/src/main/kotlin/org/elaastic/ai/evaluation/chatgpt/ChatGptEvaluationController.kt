package org.elaastic.ai.evaluation.chatgpt

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoPeerGradingController.ResponseSubmissionAsynchronous
import org.elaastic.activity.response.ResponseService
import org.elaastic.common.util.requireAccessThrowDenied
import org.elaastic.player.evaluation.EvaluationModel
import org.elaastic.player.evaluation.chatgpt.ChatGptEvaluationModelFactory
import org.elaastic.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.EntityNotFoundException

@Controller
@RequestMapping("/chatGptEvaluation")
class ChatGptEvaluationController(
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
    @Autowired val messageSource: MessageSource,
    @Autowired val responseService: ResponseService,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val peerGradingService: PeerGradingService,
    private val sequenceService: SequenceService,
) {

    @ResponseBody
    @GetMapping("/hide/{gradingId}")
    fun hideChatGptEvaluation(
        authentication: Authentication,
        model: Model,
        @PathVariable gradingId: Long,
    ): ResponseSubmissionAsynchronous {
        val user = authentication.principal as User
        val chatGptEvaluation = chatGptEvaluationService.findEvaluationById(gradingId)

        val locale: Locale = LocaleContextHolder.getLocale()

        return if (chatGptEvaluation != null) {
            try {
                chatGptEvaluationService.markAsHidden(chatGptEvaluation, user)
                ResponseSubmissionAsynchronous(
                    success = true,
                    header = messageSource.getMessage("evaluation.hideEvaluation.success.header", null, locale),
                    content = messageSource.getMessage("evaluation.hideEvaluation.success.content", null, locale)
                )
            } catch (e: IllegalAccessException) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("evaluation.accesDenied.header", null, locale),
                    content = messageSource.getMessage("evaluation.hideEvaluation.accesDenied.content", null, locale)
                )
            }
        } else {
            ResponseSubmissionAsynchronous(
                success = false,
                header = messageSource.getMessage("evaluation.hideEvaluation.error.header", null, locale),
                content = messageSource.getMessage("evaluation.hideEvaluation.error.content", null, locale)
            )
        }
    }

    @ResponseBody
    @GetMapping("/unhide/{gradingId}")
    fun unhideChatGptEvaluation(
        authentication: Authentication,
        model: Model,
        @PathVariable gradingId: Long,
    ): ResponseSubmissionAsynchronous {
        val user = authentication.principal as User
        val chatGptEvaluation = chatGptEvaluationService.findEvaluationById(gradingId)

        val locale: Locale = LocaleContextHolder.getLocale()

        return if (chatGptEvaluation != null) {
            try {
                chatGptEvaluationService.markAsShown(chatGptEvaluation, user)
                ResponseSubmissionAsynchronous(
                    success = true,
                    header = messageSource.getMessage("evaluation.unhideEvaluation.success.header", null, locale),
                    content = messageSource.getMessage("evaluation.unhideEvaluation.success.content", null, locale)
                )
            } catch (e: IllegalAccessException) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("evaluation.accesDenied.header", null, locale),
                    content = messageSource.getMessage("evaluation.unhideEvaluation.accesDenied.content", null, locale)
                )
            }
        } else {
            ResponseSubmissionAsynchronous(
                success = false,
                header = messageSource.getMessage("evaluation.unhideEvaluation.error.header", null, locale),
                content = messageSource.getMessage("evaluation.unhideEvaluation.error.content", null, locale)
            )
        }
    }

    @GetMapping("/{responseId}")
    fun getChatGptEvaluation(
        authentication: Authentication,
        model: Model,
        @PathVariable responseId: Long,
    ): String {
        val user: User = authentication.principal as User

        val evaluationModel = try {
            val response = responseService.findById(responseId)

            val assignment = response.interaction.sequence.assignment

            // Check authorizations
            requireAccessThrowDenied(
                assignment?.owner == user
                        || (assignment != null
                        && assignmentService.userIsRegisteredInAssignment(user, assignment))
            ) {
                messageSource.getMessage("evaluation.chatGPT.error.access", null, LocaleContextHolder.getLocale())
            }

            val chatGptEvaluation = chatGptEvaluationService.findEvaluationByResponse(response)
            val chatGptEvaluationModel =
            // If it isn't the teacher, we check if the teacher has hidden the ChatGPT evaluation.
            // Eq. If it's a student, we check if the teacher has hidden the ChatGPT evaluation.
                // If it's hidden, we give a null value to the model.
                if (response.interaction.sequence.chatGptEvaluationEnabled
                    && !(user != assignment!!.owner && chatGptEvaluation?.hiddenByTeacher == true)
                    && assignment.owner != response.learner // We don't need a ChatGPT evaluation for the teacher
                    && chatGptEvaluation != null
                ) {
                    // If the ChatGPT evaluation is enabled, we add it to the model
                    ChatGptEvaluationModelFactory.build(
                        evaluation = chatGptEvaluation,
                        sequence = response.interaction.sequence,
                        canHideGrading = responseService.canHidePeerGrading(user, response),
                        responseId = response.id
                    )
                } else null

            EvaluationModel(
                null,
                chatGptEvaluationModel,
                false,
                canSeeChatGPTEvaluation = user == assignment!!.owner || user == response.learner, // Only the teacher and the learner of the question can see the ChatGPT evaluation
                isTeacher = user == assignment.owner
            )
        } catch (e: EntityNotFoundException) {
            // If the response doesn't exist, we give a null value to the model.
            EvaluationModel(
                null,
                null,
                false,
                canSeeChatGPTEvaluation = false,
                isTeacher = false
            )
        }

        model["user"] = user
        model["evaluationModel"] = evaluationModel

        return "player/assignment/sequence/phase/evaluation/method/draxo/_draxo-show-list::draxoShowList"
    }

    @PostMapping("sequence/{sequenceId}/submit-utility-grade")
    @PreAuthorize("@featureManager.isActive(@featureResolver.getFeature('CHATGPT_EVALUATION'))")
    fun submitChatGptEvaluationUtilityGrade(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = true) evaluationId: Long,
        @RequestParam(required = true) utilityGrade: UtilityGrade,
        @RequestParam(required = false) isTeacher: Boolean = false,
        @PathVariable sequenceId: Long
    ): String {
        val user: User = authentication.principal as User
        val sequence = sequenceService.get(sequenceId, true)
        val chatGptEvaluation = chatGptEvaluationService.findEvaluationById(evaluationId)

        //Check consistency
        check((user == sequence.assignment!!.owner) == isTeacher) {
            messageSource.getMessage(
                "chatGPT.error.updateUtilityGrade.consistency",
                null,
                LocaleContextHolder.getLocale()
            )
        }

        // Check authorizations
        requireAccessThrowDenied(user == chatGptEvaluation!!.response.learner || isTeacher) {
            messageSource.getMessage(
                "evaluation.chatGPT.error.access.utilityGrade",
                null,
                LocaleContextHolder.getLocale()
            )
        }

        chatGptEvaluationService.changeUtilityGrade(chatGptEvaluation, utilityGrade, isTeacher)
        return "redirect:/player/assignment/${sequence.assignment!!.id}/play/sequence/${sequenceId}"
    }

    /**
     * Report a chatGptEvaluation Only the learner of the response can report
     * the evaluation
     *
     * @param authentication the current authentication
     * @param model the model
     * @param evaluationId the id of the ChatGPT evaluation to report
     * @param reasons the list of reasons to report the evaluation
     * @param otherReasonComment the comment of the other reason
     * @param id the id of the sequence where the ChatGPT evaluation is
     * @return the view of the sequence
     * @throws AccessDeniedException if the user is not the learner of the
     *    response
     */
    @PostMapping("sequence/{id}/report-chat-gpt-evaluation")
    @PreAuthorize("@featureManager.isActive(@featureResolver.getFeature('CHATGPT_EVALUATION'))")
    fun reportchatGptEvaluation(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = true) evaluationId: Long,
        @RequestParam(value = "reason", required = true) reasons: List<String>,
        @RequestParam(value = "other-reason-comment", required = false) otherReasonComment: String,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        val sequence = sequenceService.get(id, true)
        val chatGptEvaluation = chatGptEvaluationService.findEvaluationById(evaluationId)
        val reasonComment = otherReasonComment.ifEmpty { null }

        // Check authorizations
        requireAccessThrowDenied(user == chatGptEvaluation!!.response.learner) {
            messageSource.getMessage("evaluatio.chatGPT.error.access.report", null, LocaleContextHolder.getLocale())
        }

        chatGptEvaluationService.reportEvaluation(chatGptEvaluation, reasons, reasonComment)
        return "redirect:/player/assignment/${sequence.assignment!!.id}/play/sequence/${id}"
    }
}