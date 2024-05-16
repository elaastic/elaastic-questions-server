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
package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.components.evaluation.EvaluationModel
import org.elaastic.questions.player.components.evaluation.chatGptEvaluation.ChatGptEvaluationModelFactory
import org.elaastic.questions.player.components.evaluation.draxo.DraxoEvaluationModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*
import java.util.Locale

@Controller
@RequestMapping("/peer-grading/draxo")
class DraxoPeerGradingController(
    @Autowired val responseService: ResponseService,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val messageSource: MessageSource,
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
) {

    /**
     * Get all the DRAXO evaluations of a response
     *
     * @param authentication the current user authentication
     * @param model the model
     * @param responseId the id of the response
     * @param hideName if the name of the grader should be hidden
     * @return the view of the list of DRAXO evaluations
     */
    @GetMapping("/{responseId}")
    fun getAll(
        authentication: Authentication,
        model: Model,
        @PathVariable responseId: Long,
        @RequestParam hideName: Boolean?
    ): String {
        val user: User = authentication.principal as User

        val response = responseService.findById(responseId)
        val assignment = response.interaction.sequence.assignment

        // Check authorizations
        if (
            assignment?.owner != user &&
            (assignment == null || !assignmentService.userIsRegisteredInAssignment(user, assignment))
        ) {
            throw AccessDeniedException("You are not authorized to access to those feedbacks")
        }

        var draxoPeerGradingList = peerGradingService.findAllDraxo(response)

        // The student can't see the hidden feedbacks
        if (user.isLearner()) draxoPeerGradingList = draxoPeerGradingList.filter { !it.hiddenByTeacher }

        val draxoEvaluationModels = draxoPeerGradingList.mapIndexed { index, draxoPeerGrading ->
            DraxoEvaluationModel(
                index,
                draxoPeerGrading,
                user == assignment.owner,
                responseService.canReactOnFeedbackOfResponse(user, response),
                responseService.canHidePeerGrading(user, response)
            )
        }
        // If the ChatGPT evaluation is enabled, we add it to the model
        val chatGptEvaluationModel =
            if (response.interaction.sequence.chatGptEvaluationEnabled) {
                ChatGptEvaluationModelFactory.build(
                    chatGptEvaluationService.findEvaluationByResponse(response),
                    response.interaction.sequence
                )
            } else null

        val evaluationModel = EvaluationModel(
            draxoEvaluationModels,
            chatGptEvaluationModel,
            hideName ?: false,
            canSeeChatGPTEvaluation = user == assignment.owner // Only the teacher can see the chatGPT evaluation
        )

        model["user"] = user
        model["evaluationModel"] = evaluationModel

        return "player/assignment/sequence/phase/evaluation/method/draxo/_draxo-show-list::draxoShowList"
    }


    /**
     * Handle the submission of a DRAXO evaluation utility grade through
     * asynchronous request
     *
     * @param authentication the current user authentication
     * @param model the model
     * @param evaluationId the id of the evaluation to update
     * @param utilityGrade the utility grade to set
     * @return [ResponseSubmissionAsynchronous] the response of the submission
     *     in JSON format
     * @see ResponseSubmissionAsynchronous
     */
    @ResponseBody
    @PostMapping("/submit-utility-grade")
    fun submitDRAXOEvaluationUtilityGrade(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = true) evaluationId: Long,
        @RequestParam(required = true) utilityGrade: UtilityGrade
    ): ResponseSubmissionAsynchronous {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.getDraxoPeerGrading(evaluationId)
        val locale: Locale = LocaleContextHolder.getLocale()

        val responseSubmissionAsynchronous = kotlin.run {
            try {
                peerGradingService.updateUtilityGrade(user, evaluation, utilityGrade)

                ResponseSubmissionAsynchronous(
                    success = true,
                    header = messageSource.getMessage("draxo.submitUtilityGrade.success.header", null, locale),
                    content = messageSource.getMessage("draxo.submitUtilityGrade.success.content", null, locale)
                )
            } catch (e: Exception) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("draxo.submitUtilityGrade.error.header", null, locale),
                    content = messageSource.getMessage("draxo.submitUtilityGrade.error.content", null, locale)
                )
            }
        }

        return responseSubmissionAsynchronous
    }


    /**
     * Handle the submission of a DRAXO evaluation report through asynchronous
     * request
     *
     * @param authentication the current user authentication
     * @param model the model
     * @param evaluationId the id of the evaluation to update
     * @param reasons the reasons to report the evaluation
     * @param otherReasonComment the comment of the other reason
     * @return [ResponseSubmissionAsynchronous] the response of the submission
     *     in JSON format
     */
    @ResponseBody
    @PostMapping("/report-draxo-evaluation")
    fun reportDRAXOEvaluation(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = true) evaluationId: Long,
        @RequestParam(value = "reason", required = true) reasons: List<String>,
        @RequestParam(value = "other-reason-comment", required = false) otherReasonComment: String
    ): ResponseSubmissionAsynchronous {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.getDraxoPeerGrading(evaluationId)
        val reasonComment = if (otherReasonComment.isNotEmpty()) otherReasonComment else null
        val locale: Locale = LocaleContextHolder.getLocale()

        val responseSubmissionAsynchronous = kotlin.run {
            try {
                peerGradingService.updateReport(user, evaluation, reasons, reasonComment)

                ResponseSubmissionAsynchronous(
                    success = true,
                    header = messageSource.getMessage("draxo.reportEvaluation.success.header", null, locale),
                    content = messageSource.getMessage("draxo.reportEvaluation.success.content", null, locale)
                )
            } catch (e: Exception) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("draxo.reportEvaluation.error.header", null, locale),
                    content = messageSource.getMessage("draxo.reportEvaluation.error.content", null, locale)
                )
            }
        }

        return responseSubmissionAsynchronous
    }

    /**
     * Handle the submission of a hiding request for a DRAXO evaluation through
     * asynchronous request
     *
     * @param authentication the current user authentication
     * @param model the model
     * @param id the id of the evaluation to hide
     * @return [ResponseSubmissionAsynchronous] the response of the submission
     *     in JSON format
     * @see ResponseSubmissionAsynchronous
     */
    @ResponseBody
    @GetMapping("/hide/{id}")
    fun hide(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): ResponseSubmissionAsynchronous {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.getDraxoPeerGrading(id)
        val locale: Locale = LocaleContextHolder.getLocale()

        val responseSubmissionAsynchronous = kotlin.run {
            try {
                if (!responseService.canHidePeerGrading(user, evaluation.response)) {
                    throw AccessDeniedException("You are not authorized to hide this feedback")
                }

                peerGradingService.markAsHidden(user, evaluation)

                ResponseSubmissionAsynchronous(
                    success = true,
                    header = messageSource.getMessage("draxo.hideEvaluation.success.header", null, locale),
                    content = messageSource.getMessage("draxo.hideEvaluation.success.content", null, locale)
                )
            } catch (e: AccessDeniedException) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("draxo.accesDenied.header", null, locale),
                    content = messageSource.getMessage("draxo.hideEvaluation.accesDenied.content", null, locale)
                )
            } catch (e: Exception) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("draxo.hideEvaluation.error.header", null, locale),
                    content = messageSource.getMessage("draxo.hideEvaluation.error.content", null, locale)
                )
            }
        }

        return responseSubmissionAsynchronous
    }

    /**
     * Handle the submission of an unhiding request for a DRAXO evaluation
     * through asynchronous request
     *
     * @param authentication the current user authentication
     * @param model the model
     * @param id the id of the evaluation to unhide
     * @return [ResponseSubmissionAsynchronous] the response of the submission
     *     in JSON format
     * @see ResponseSubmissionAsynchronous
     */
    @ResponseBody
    @GetMapping("/unhide/{id}")
    fun unhide(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): ResponseSubmissionAsynchronous {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.getDraxoPeerGrading(id)
        val locale: Locale = LocaleContextHolder.getLocale()

        val responseSubmissionAsynchronous = kotlin.run {
            try {

                if (!responseService.canHidePeerGrading(user, evaluation.response)) {
                    throw AccessDeniedException("You are not authorized to unhide this feedback")
                }

                peerGradingService.markAsShow(user, evaluation)

                ResponseSubmissionAsynchronous(
                    success = true,
                    header = messageSource.getMessage("draxo.unhideEvaluation.success.header", null, locale),
                    content = messageSource.getMessage("draxo.unhideEvaluation.success.content", null, locale)
                )
            } catch (e: AccessDeniedException) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("draxo.accesDenied.header", null, locale),
                    content = messageSource.getMessage("draxo.unhideEvaluation.accesDenied.content", null, locale)
                )
            } catch (e: Exception) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("draxo.unhideEvaluation.error.header", null, locale),
                    content = messageSource.getMessage("draxo.unhideEvaluation.error.content", null, locale)
                )
            }
        }

        return responseSubmissionAsynchronous
    }

    /**
     * Response for submission through asynchronous request
     *
     * @param success true if the submission was successful, false otherwise
     * @param header the header of the response (Meant to be used in a
     *     semantic-ui message)
     * @param content the content of the response (Meant to be used in a
     *     semantic-ui message)
     */
    data class ResponseSubmissionAsynchronous(val success: Boolean, val header: String, val content: String)
}