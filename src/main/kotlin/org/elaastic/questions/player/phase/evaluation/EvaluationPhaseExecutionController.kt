package org.elaastic.questions.player.phase.evaluation

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.PlayerController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

// TODO Check if it used + move to the proper package
@Controller
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation")
class EvaluationPhaseExecutionController(
    @Autowired val sequenceService: SequenceService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
    ) {

    @PostMapping("/submit-evaluation")
    fun submitEvaluation(
        authentication: Authentication,
        model: Model,
        @PathVariable sequenceId: Long,
        @ModelAttribute evaluationData: PlayerController.EvaluationData,
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment

        sequenceService.get(sequenceId, true).let { sequence ->
            assignment = sequence.assignment!!
            evaluationData.getGrades().forEach {
                peerGradingService.createOrUpdate(
                    user,
                    responseService.getOne(it.key),
                    it.value.toBigDecimal()
                )
            }

            return "redirect:/player/assignment/${assignment.id}/play/sequence/${sequenceId}"
        }
    }
}