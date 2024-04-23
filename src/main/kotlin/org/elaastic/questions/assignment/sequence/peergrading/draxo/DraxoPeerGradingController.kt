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
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/peer-grading/draxo")
class DraxoPeerGradingController(
    @Autowired val responseService: ResponseService,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val peerGradingService: PeerGradingService,
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

        val draxoPeerGradingList = peerGradingService.findAllDraxo(response)

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

        // An user can moderate the evaluation if he is the owner of the assignment (teacher) or the owner of the sequence (learner)
        model["canModerate"] = responseService.canModerate(user, response)
        model["canModerate"] = true // TODO Remove STUB

        return "player/assignment/sequence/phase/evaluation/method/draxo/_draxo-show-list::draxoShowList"
    }


    @PostMapping("submit-utility-grade")
    fun submitDRAXOEvaluationUtilityGrade(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = true) evaluationId: Long,
        @RequestParam(required = true) utilityGrade: UtilityGrade
    ): String {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.findDraxoById(evaluationId)

        peerGradingService.updateUtilityGrade(user, evaluation, utilityGrade)

        val sequenceId: Long = evaluation.response.interaction.sequence.id!!
        val assignement = evaluation.response.interaction.sequence.assignment!!.id
        return "redirect:/player/assignment/${assignement}/play/sequence/${sequenceId}"
    }

    @PostMapping("report-draxo-evaluation")
    fun reportDRAXOEvaluation(
        authentication: Authentication,
        model: Model,
        @RequestParam(required = true) evaluationId: Long,
        @RequestParam(value = "reason", required = true) reasons: List<String>,
        @RequestParam(value = "other-reason-comment", required = false) otherReasonComment: String
    ): String {
        val user: User = authentication.principal as User
        val evaluation: DraxoPeerGrading = peerGradingService.findDraxoById(evaluationId)
        val reasonComment = if (otherReasonComment.isNotEmpty()) otherReasonComment else null

        peerGradingService.updateReport(user, evaluation, reasons, reasonComment)

        val sequenceId: Long = evaluation.response.interaction.sequence.id!!
        val assignement = evaluation.response.interaction.sequence.assignment!!.id
        return "redirect:/player/assignment/${assignement}/play/sequence/${sequenceId}"
    }
}