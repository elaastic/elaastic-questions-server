package org.elaastic.questions.player.phase

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.descriptor.PhaseDescriptor
import org.elaastic.questions.player.phase.evaluation.EvaluationPhaseConfig
import org.elaastic.questions.player.phase.evaluation.all_at_once.AllAtOnceLearnerEvaluationPhase
import org.elaastic.questions.player.phase.evaluation.draxo.DraxoLearnerEvaluationPhase
import org.elaastic.questions.player.phase.response.LearnerResponsePhase
import org.elaastic.questions.player.phase.result.LearnerResultPhase
import org.springframework.stereotype.Service

/**
 * @see LearnerPhase
 */
@Service
class LearnerPhaseFactory {

    fun build(
        phaseDescriptor: PhaseDescriptor,
        learnerSequence: ILearnerSequence,
        phaseIndex: Int,
        active: Boolean,
        state: State,
    ): LearnerPhase = when (phaseDescriptor.type) {
        LearnerPhaseType.RESPONSE -> LearnerResponsePhase(learnerSequence, phaseIndex, active, state)
        LearnerPhaseType.EVALUATION ->
            when (learnerSequence.sequence.evaluationPhaseConfig) {
                EvaluationPhaseConfig.ALL_AT_ONCE -> AllAtOnceLearnerEvaluationPhase(
                    learnerSequence,
                    phaseIndex,
                    active,
                    state,
                )

                EvaluationPhaseConfig.DRAXO -> DraxoLearnerEvaluationPhase(
                    learnerSequence,
                    phaseIndex,
                    active,
                    state
                )
            }

        LearnerPhaseType.RESULT -> LearnerResultPhase(learnerSequence, phaseIndex, active, state)
    }
}