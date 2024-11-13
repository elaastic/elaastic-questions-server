package org.elaastic.sequence.phase.evaluation.draxo

import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.State
import org.elaastic.sequence.phase.*
import org.elaastic.sequence.phase.response.LearnerResponseFormViewModelFactory
import org.elaastic.sequence.phase.LearnerPhaseExecution
import org.elaastic.sequence.phase.LearnerPhaseType
import org.elaastic.sequence.phase.PhaseTemplate

class DraxoLearnerEvaluationPhase(
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
                "${PhaseTemplate.TEMPLATE_PACKAGE}/evaluation/draxo/_evaluation-phase.html",
                "evaluationPhase"
            )
    }

    override val phaseType = LearnerPhaseType.EVALUATION
    override var learnerPhaseExecution: DraxoLearnerEvaluationPhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if (learnerPhaseExecution is DraxoLearnerEvaluationPhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }

    override fun getViewModel(): PhaseViewModel = run {
        val sequence = learnerSequence.sequence

        // TODO we should get rid of interaction here...
        val interactionId = learnerSequence.sequence.getEvaluationInteraction().id
            ?: error("Interaction must have an ID to an evaluation")

        val learnerPhaseExecution: DraxoLearnerEvaluationPhaseExecution =
            learnerPhaseExecution
                ?: throw IllegalStateException("LearnerEvaluationInteraction has not been loaded")

        val lastUserResponse = learnerPhaseExecution.lastAttemptResponse

        DraxoLearnerEvaluationPhaseViewModel(
            sequenceId = sequence.id ?: error("The sequence must have an ID during evaluation phase"),
            interactionId = interactionId, // TODO we should get rid of interaction here...
            phaseState = state,
            choices = sequence.statement.hasChoices(),
            userHasCompletedPhase2 = learnerPhaseExecution.userHasCompletedPhase2,
            nextResponseToGrade = learnerPhaseExecution.nextResponseToGrade,
            secondAttemptAllowed = sequence.isSecondAttemptAllowed(),
            secondAttemptAlreadySubmitted = learnerPhaseExecution.secondAttemptAlreadySubmitted,
            responseFormModel = LearnerResponseFormViewModelFactory.buildFor2ndAttempt(
                learnerSequence,
                lastUserResponse
            ),
            lastResponseToGrade = learnerPhaseExecution.lastResponseToGrade,
            draxoEvaluation = learnerPhaseExecution.draxoEvaluation
        )
    }
}