package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.interaction.results.ItemIndex
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.phase.evaluation.AbstractEvaluationPhaseExecutionController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/one-by-one")
class OneByOneEvaluationPhaseExecutionController(
    @Autowired override val sequenceService: SequenceService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired override val responseService: ResponseService,
) : AbstractEvaluationPhaseExecutionController(
    sequenceService,
    responseService
) {

    @PostMapping("/submit-evaluation")
    fun submitEvaluation(
        authentication: Authentication,
        model: Model,
        @PathVariable sequenceId: Long,
        @ModelAttribute evaluationData: EvaluationData,
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment

        sequenceService.get(sequenceId, true).let { sequence ->
            assignment = sequence.assignment!!
            peerGradingService.createOrUpdate(
                user,
                responseService.getOne(evaluationData.responseId),
                evaluationData.grade.toBigDecimal()
            )

            if (evaluationData.changeAnswer
                && sequence.isSecondAttemptAllowed()
                && !responseService.hasResponseForUser(user, sequence, 2)
            ) {
                changeAnswer(
                    user, sequence, Answer(
                        evaluationData.choiceList,
                        evaluationData.confidenceDegree,
                        evaluationData.explanation
                    )
                )
            }

            return finalizePhaseExecution(user, sequence, assignment.id!!)
        }
    }

    class EvaluationData(
        val responseId: Long,
        val grade: Int,

        val changeAnswer: Boolean = false,
        val choiceList: List<ItemIndex>?,
        val confidenceDegree: ConfidenceDegree?,
        val explanation: String?
    )
}