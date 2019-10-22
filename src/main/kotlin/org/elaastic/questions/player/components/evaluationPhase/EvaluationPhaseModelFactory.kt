package org.elaastic.questions.player.components.evaluationPhase

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.player.components.responsePhase.ResponseFormModel

object EvaluationPhaseModelFactory {

    fun build(userHasCompletedPhase2: Boolean,
              userHasPerformedEvaluation: Boolean,
              secondAttemptAlreadySubmitted: Boolean,
              responsesToGrade: List<ResponseData>,
              sequence: Sequence,
              userActiveInteraction: Interaction?,
              firstAttemptResponse: Response?) = run {
        val interaction = sequence.getEvaluationInteraction()
        val sequenceId = sequence.id ?: error("The sequence must have an ID during evaluation phase")
        val interactionId = interaction.id ?: error("The interaction must have an ID during evaluation phase")

        EvaluationPhaseModel(
                sequenceId = sequenceId,
                interactionId = interactionId,
                userActiveInteractionState = userActiveInteraction?.state ?: State.beforeStart,
                choices = sequence.statement.hasChoices(),
                activeInteractionRank = interaction.rank,
                userHasCompletedPhase2 = userHasCompletedPhase2,
                userHasPerformedEvaluation = userHasPerformedEvaluation,
                responsesToGrade= responsesToGrade,
                secondAttemptAllowed = sequence.isSecondAttemptAllowed(),
                secondAttemptAlreadySubmitted = secondAttemptAlreadySubmitted,
                responseFormModel = ResponseFormModel(
                        interactionId = interactionId,
                        attempt = 2,
                        responseSubmissionSpecification = sequence.getResponseSubmissionSpecification(),
                        timeToProvideExplanation = (sequence.executionIsBlended() || sequence.executionIsDistance()), // TODO I don't understand this logic
                        hasChoices = sequence.statement.hasChoices(),
                        multipleChoice = sequence.statement.isMultipleChoice(),
                        nbItem = sequence.statement.choiceSpecification?.nbCandidateItem,
                        firstAttemptExplanation = firstAttemptResponse?.explanation,
                        firstAttemptChoices = firstAttemptResponse?.learnerChoice?.toTypedArray() ?: arrayOf(),
                        firstAttemptConfidenceDegree = firstAttemptResponse?.confidenceDegree
                )
        )
    }

}