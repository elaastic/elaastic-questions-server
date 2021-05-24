package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.player.phase.response.LearnerResponseFormViewModel

object AllAtOnceLearnerEvaluationPhaseViewModelFactory {

    fun build(learnerPhase: AllAtOnceLearnerEvaluationPhase): AllAtOnceLearnerEvaluationPhaseViewModel = run {
        val sequence = learnerPhase.learnerSequence.sequence

        // TODO we should get rid of interaction here...
        val interactionId = learnerPhase.learnerSequence.getEvaluationInteraction().id
            ?: error("Interaction must have an ID to an evaluation")

        val learnerPhaseExecution: AllAtOnceLearnerEvaluationPhaseExecution =
            learnerPhase.learnerPhaseExecution
                ?: throw IllegalStateException("LearnerEvaluationInteraction has not been loaded")

        AllAtOnceLearnerEvaluationPhaseViewModel(
            sequenceId = sequence.id ?: error("The sequence must have an ID during evaluation phase"),
            interactionId = interactionId,
            userActiveInteractionState = learnerPhase.state, // TODO rename this attribute into the view
            choices = sequence.statement.hasChoices(),
            activeInteractionRank = 2,
            userHasCompletedPhase2 = learnerPhaseExecution.userHasCompletedPhase2,
            userHasPerformedEvaluation = learnerPhaseExecution.userHasPerformedEvaluation,
            responsesToGrade = learnerPhaseExecution.responsesToGrade,
            secondAttemptAllowed = sequence.isSecondAttemptAllowed(),
            secondAttemptAlreadySubmitted = learnerPhaseExecution.secondAttemptAlreadySubmitted,
            responseFormModel = LearnerResponseFormViewModel(
                interactionId = interactionId,
                attempt = 2,
                responseSubmissionSpecification = sequence.getResponseSubmissionSpecification(),
                timeToProvideExplanation = (sequence.executionIsBlended() || sequence.executionIsDistance()), // TODO I don't understand this logic
                hasChoices = sequence.statement.hasChoices(),
                multipleChoice = sequence.statement.isMultipleChoice(),
                nbItem = sequence.statement.choiceSpecification?.nbCandidateItem,
                firstAttemptExplanation = learnerPhaseExecution.firstAttemptResponse?.explanation,
                firstAttemptChoices = learnerPhaseExecution.firstAttemptResponse?.learnerChoice?.toTypedArray() ?: arrayOf(),
                firstAttemptConfidenceDegree = learnerPhaseExecution.firstAttemptResponse?.confidenceDegree
            )
        )
    }

}