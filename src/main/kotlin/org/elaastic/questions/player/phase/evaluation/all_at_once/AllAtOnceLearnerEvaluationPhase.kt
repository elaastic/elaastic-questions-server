package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.*
import org.elaastic.questions.player.phase.response.LearnerResponseFormViewModelFactory

class AllAtOnceLearnerEvaluationPhase(
    learnerSequence: ILearnerSequence,
    index: Int,
    active: Boolean,
    state: State,
) : LearnerPhase(
    learnerSequence,
    index, active,
    state,
    TEMPLATE
) {

    companion object {
        val TEMPLATE =
            PhaseTemplate(
                "${PhaseTemplate.TEMPLATE_PACKAGE}/evaluation/all-at-once/_evaluation-phase.html",
                "evaluationPhase"
            )
    }

    override val phaseType = LearnerPhaseType.EVALUATION
    override var learnerPhaseExecution: AllAtOnceLearnerEvaluationPhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if(learnerPhaseExecution is AllAtOnceLearnerEvaluationPhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }

    override fun getViewModel(): PhaseViewModel = run {
        val sequence = learnerSequence.sequence

        // TODO we should get rid of interaction here...
        val interactionId = learnerSequence.sequence.getEvaluationInteraction().id
            ?: error("Interaction must have an ID to an evaluation")

        val learnerPhaseExecution: AllAtOnceLearnerEvaluationPhaseExecution =
            learnerPhaseExecution
                ?: throw IllegalStateException("LearnerEvaluationInteraction has not been loaded")

        AllAtOnceLearnerEvaluationPhaseViewModel(
            sequenceId = sequence.id ?: error("The sequence must have an ID during evaluation phase"),
            interactionId = interactionId,
            phaseState = state,
            choices = sequence.statement.hasChoices(),
            userHasCompletedPhase2 = learnerPhaseExecution.userHasCompletedPhase2,
            userHasPerformedEvaluation = learnerPhaseExecution.userHasPerformedEvaluation,
            responsesToGrade = learnerPhaseExecution.responsesToGrade,
            secondAttemptAllowed = sequence.isSecondAttemptAllowed(),
            secondAttemptAlreadySubmitted = learnerPhaseExecution.secondAttemptAlreadySubmitted,
            responseFormModel = LearnerResponseFormViewModelFactory.buildFor2ndAttempt(
                learnerSequence,
                learnerPhaseExecution.firstAttemptResponse
            )
        )
    }
}