package org.elaastic.questions.player.phase.evaluation

import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.interaction.results.ItemIndex
import org.elaastic.questions.directory.User

abstract class AbstractEvaluationPhaseExecutionController(
    open val sequenceService: SequenceService,
    open val responseService: ResponseService,
) {

    fun changeAnswer(user: User, sequence: Sequence, answer: Answer) {
        val choiceListSpecification = answer.choiceList?.let {
            LearnerChoice(it)
        }

        Response(
            learner = user,
            interaction = sequence.getResponseSubmissionInteraction(),
            attempt = 2,
            confidenceDegree = answer.confidenceDegree,
            explanation = answer.explanation,
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
                    ?: error("No active interaction, cannot submit a response")

                responseService.save(
                    userActiveInteraction,
                    it
                )
            }
    }

    fun finalizePhaseExecution(user: User, sequence: Sequence, assignmentId: Long): String {
        if (sequence.executionIsDistance() || sequence.executionIsBlended()) {
            sequenceService.nextInteractionForLearner(sequence, user)
        }



        return "redirect:/player/assignment/${assignmentId}/play/sequence/${sequence.id}"
    }

    class Answer(
        val choiceList: List<ItemIndex>?,
        val confidenceDegree: ConfidenceDegree?,
        val explanation: String?
    )
}