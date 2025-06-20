package org.elaastic.sequence.phase

import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.sequence.phase.evaluation.EvaluationMethod
import org.elaastic.sequence.phase.evaluation.all_at_once.AllAtOnceLearnerEvaluationPhase
import org.elaastic.sequence.phase.evaluation.draxo.DraxoLearnerEvaluationPhase
import org.elaastic.sequence.phase.response.LearnerResponsePhase
import org.elaastic.sequence.phase.result.LearnerResultPhase
import org.springframework.stereotype.Service

/** @see LearnerPhase */
@Service
class LearnerPhaseFactory {

    fun build(
        interactionType: InteractionType,
        learnerSequence: ILearnerSequence,
        phaseIndex: Int,
        active: Boolean,
        state: State,
    ): LearnerPhase = when (interactionType) {
        InteractionType.ResponseSubmission -> LearnerResponsePhase(learnerSequence, phaseIndex, active, state)
        InteractionType.Evaluation ->
            when (learnerSequence.sequence.evaluationMethod) {
                EvaluationMethod.ALL_AT_ONCE -> AllAtOnceLearnerEvaluationPhase(
                    learnerSequence,
                    phaseIndex,
                    active,
                    state,
                )

                EvaluationMethod.DRAXO -> DraxoLearnerEvaluationPhase(
                    learnerSequence,
                    phaseIndex,
                    active,
                    state
                )
            }

        InteractionType.Read -> LearnerResultPhase(learnerSequence, phaseIndex, active, state)
    }
}