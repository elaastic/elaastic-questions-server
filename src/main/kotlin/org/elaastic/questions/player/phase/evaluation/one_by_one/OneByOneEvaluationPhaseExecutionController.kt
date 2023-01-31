package org.elaastic.questions.player.phase.evaluation.one_by_one

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

@Controller
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/one-by-one")
class OneByOneEvaluationPhaseExecutionController(
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
                val choiceListSpecification = evaluationData.choiceList?.let {
                    LearnerChoice(it)
                }

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



            return "redirect:/player/assignment/${assignment.id}/play/sequence/${sequenceId}"


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