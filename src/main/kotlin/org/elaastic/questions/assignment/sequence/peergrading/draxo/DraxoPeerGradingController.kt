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
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.components.draxo.DraxoEvaluationModel
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
    @Autowired val messageSource: MessageSource
) {

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

        model.addAttribute("user", user)
        model.addAttribute(
            "evaluationModelList",
            draxoPeerGradingList.mapIndexed { index, draxoPeerGrading ->
                DraxoEvaluationModel(index, draxoPeerGrading, user == assignment.owner)
            }
        )
        if (hideName == true) {
            model.addAttribute("hideName", true)
        }

        // A user can moderate the evaluation if he is the owner of the response
        model["canModeratePeerGrading"] = responseService.canModerate(user, response)
        model["canHidePeerGrading"] = responseService.canHidePeerGrading(user, response)

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
     * @return [ResponseSubmitUtilityGrade] the response of the submission in
     *     JSON format
     * @see ResponseSubmitUtilityGrade
     */
    @ResponseBody
    @PostMapping("/submit-utility-grade")
    fun submitDRAXOEvaluationUtilityGrade(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = true) evaluationId: Long,
        @RequestParam(required = true) utilityGrade: UtilityGrade
    ): ResponseSubmitUtilityGrade {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.getDraxoPeerGrading(evaluationId)
        val locale: Locale = LocaleContextHolder.getLocale()

        val responseSubmitUtilityGrade = kotlin.run {
            try {
                peerGradingService.updateUtilityGrade(user, evaluation, utilityGrade)

                ResponseSubmitUtilityGrade(
                    success = true,
                    header = messageSource.getMessage("draxo.submitUtilityGrade.success.header", null, locale),
                    content = messageSource.getMessage("draxo.submitUtilityGrade.success.content", null, locale)
                )
            } catch (e: Exception) {
                ResponseSubmitUtilityGrade(
                    success = false,
                    header = messageSource.getMessage("draxo.submitUtilityGrade.error.header", null, locale),
                    content = messageSource.getMessage("draxo.submitUtilityGrade.error.content", null, locale)
                )
            }
        }

        return responseSubmitUtilityGrade
    }


    @PostMapping("/report-draxo-evaluation")
    fun reportDRAXOEvaluation(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = true) evaluationId: Long,
        @RequestParam(value = "reason", required = true) reasons: List<String>,
        @RequestParam(value = "other-reason-comment", required = false) otherReasonComment: String
    ): String {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.getDraxoPeerGrading(evaluationId)
        val reasonComment = if (otherReasonComment.isNotEmpty()) otherReasonComment else null

        peerGradingService.updateReport(user, evaluation, reasons, reasonComment)

        val sequenceId: Long = evaluation.response.interaction.sequence.id!!
        val assignement = evaluation.response.interaction.sequence.assignment!!.id
        return "redirect:/player/assignment/${assignement}/play/sequence/${sequenceId}"
    }

    @GetMapping("/hide/{id}")
    fun hide(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.getDraxoPeerGrading(id)

        if (!responseService.canHidePeerGrading(user, evaluation.response)) {
            throw AccessDeniedException("You are not authorized to hide this feedback")
        }

        peerGradingService.markAsHidden(user, evaluation)

        val sequenceId: Long = evaluation.response.interaction.sequence.id!!
        val assignement = evaluation.response.interaction.sequence.assignment!!.id
        return "redirect:/player/assignment/${assignement}/play/sequence/${sequenceId}"
    }

    @GetMapping("/unhide/{id}")
    fun unhide(
        authentication: Authentication,
        model: Model,
        @PathVariable id: Long
    ): String {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.getDraxoPeerGrading(id)

        if (!responseService.canHidePeerGrading(user, evaluation.response)) {
            throw AccessDeniedException("You are not authorized to unhide this feedback")
        }

        peerGradingService.markAsShow(user, evaluation)

        val sequenceId: Long = evaluation.response.interaction.sequence.id!!
        val assignement = evaluation.response.interaction.sequence.assignment!!.id
        return "redirect:/player/assignment/${assignement}/play/sequence/${sequenceId}"
    }

    /**
     * Response for the submission of a DRAXO evaluation utility grade through
     * asynchronous request
     *
     * @param success true if the submission was successful, false otherwise
     * @param header the header of the response (Meant to be used in a
     *     semantic-ui message)
     * @param content the content of the response (Meant to be used in a
     *     semantic-ui message)
     */
    data class ResponseSubmitUtilityGrade(val success: Boolean, val header: String, val content: String)
}