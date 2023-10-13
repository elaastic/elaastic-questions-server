package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
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
import java.util.HashMap

@Controller
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/all-at-once")
class AllAtOnceEvaluationPhaseExecutionController(
    @Autowired override val sequenceService: SequenceService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired override val responseService: ResponseService,
    @Autowired override val chatGptEvaluationService: ChatGptEvaluationService
) : AbstractEvaluationPhaseExecutionController(
    sequenceService,
    responseService,
    chatGptEvaluationService
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
            evaluationData.getGrades().forEach {
                peerGradingService.createOrUpdateLikert(
                    user,
                    responseService.getReferenceById(it.key),
                    it.value.toBigDecimal()
                )
            }

            var lastResponse: Response? = null
            if (sequence.isSecondAttemptAllowed()
                && !responseService.hasResponseForUser(user, sequence, 2)
            ) {
                lastResponse = changeAnswer(
                    user,
                    sequence,
                    Answer(
                        evaluationData.choiceList,
                        evaluationData.confidenceDegree,
                        evaluationData.explanation
                    )
                )
            }

            return finalizePhaseExecution(user, sequence, assignment.id!!, lastResponse)
        }
    }

    class EvaluationData(
        val id: Long,
        val choiceList: List<ItemIndex>?,
        val confidenceDegree: ConfidenceDegree?,
        val explanation: String?
    ) {
        private var grades = HashMap<Long, Int>()

        fun getGrades() = grades

        fun setGrades(value: HashMap<Long, Int>) {
            grades = value
        }
    }
}