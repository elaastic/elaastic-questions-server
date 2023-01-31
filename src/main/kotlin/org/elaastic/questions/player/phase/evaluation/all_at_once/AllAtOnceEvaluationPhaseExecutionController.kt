package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.interaction.results.ItemIndex
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.HashMap

@Controller
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/all-at-once")
class AllAtOnceEvaluationPhaseExecutionController(
    @Autowired val sequenceService: SequenceService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
) {

    @PostMapping("/submit-evaluation")
    fun submitEvaluation(
        authentication: Authentication,
        model: Model,
        @PathVariable sequenceId: Long,
        @ModelAttribute evaluationData: EvaluationData,
    ): String {
        val user: User = authentication.principal as User
        var assignment: Assignment?

        sequenceService.get(sequenceId, true).let { sequence ->
            assignment = sequence.assignment!!
            evaluationData.getGrades().forEach {
                peerGradingService.createOrUpdate(user, responseService.getOne(it.key), it.value.toBigDecimal())
            }

            if (sequence.isSecondAttemptAllowed()
                && !responseService.hasResponseForUser(user, sequence, 2)
            ) {
                val choiceListSpecification = evaluationData.choiceList?.let {
                    LearnerChoice(it)
                }

                // TODO I guess this code should be factorized
                Response(
                    learner = user,
                    interaction = sequence.getResponseSubmissionInteraction(),
                    attempt = 2,
                    confidenceDegree = evaluationData.confidenceDegree,
                    explanation = evaluationData.explanation,
                    learnerChoice = choiceListSpecification,
                    score = choiceListSpecification?.let {
                        Response.computeScore(
                            it,
                            sequence.statement.choiceSpecification
                                ?: error("The choice specification is undefined")
                        )
                    },
                    statement = sequence.statement

                )
                    .let {
                        val userActiveInteraction = sequenceService.getActiveInteractionForLearner(sequence, user)
                            ?: error("No active interaction, cannot submit a response") // TODO we should provide a user-friendly error page for this

                        responseService.save(
                            userActiveInteraction,
                            it
                        )
                    }


            }

            if (sequence.executionIsDistance() || sequence.executionIsBlended()) {
                sequenceService.nextInteractionForLearner(sequence, user)
            }

            return "redirect:/player/assignment/${assignment!!.id}/play/sequence/${sequenceId}"
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