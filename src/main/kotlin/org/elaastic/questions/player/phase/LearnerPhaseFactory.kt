package org.elaastic.questions.player.phase

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.descriptor.PhaseDescriptor
import org.elaastic.questions.player.phase.evaluation.LearnerEvaluationPhaseConfig
import org.elaastic.questions.player.phase.evaluation.all_at_once.AllAtOnceLearnerEvaluationPhase
import org.elaastic.questions.player.phase.evaluation.draxo.DraxoLearnerEvaluationPhase
import org.elaastic.questions.player.phase.evaluation.one_by_one.OneByOneLearnerEvaluationPhase
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
        LearnerPhaseType.EVALUATION -> when (phaseDescriptor.config) {
            LearnerEvaluationPhaseConfig.ALL_AT_ONCE -> AllAtOnceLearnerEvaluationPhase(
                learnerSequence,
                phaseIndex,
                active,
                state,
            )

            LearnerEvaluationPhaseConfig.ONE_BY_ONE -> OneByOneLearnerEvaluationPhase(
                learnerSequence,
                phaseIndex,
                active,
                state,
            )

            LearnerEvaluationPhaseConfig.DRAXO -> DraxoLearnerEvaluationPhase(
                learnerSequence,
                phaseIndex,
                active,
                state
            )

            else -> error("the phase config '${phaseDescriptor.config}' is not supported here")
        }

        LearnerPhaseType.RESULT -> LearnerResultPhase(learnerSequence, phaseIndex, active, state)
    }
}