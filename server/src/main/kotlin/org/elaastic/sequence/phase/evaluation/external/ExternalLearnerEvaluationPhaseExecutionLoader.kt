package org.elaastic.sequence.phase.evaluation.external

import org.elaastic.sequence.phase.LearnerPhase
import org.elaastic.sequence.phase.LearnerPhaseExecution
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseExecutionLoader
import org.springframework.stereotype.Service

@Service("ExternalLearnerEvaluationPhaseExecutionLoader")
class ExternalLearnerEvaluationPhaseExecutionLoader : AbstractLearnerEvaluationPhaseExecutionLoader() {
    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution = run {
        require(learnerPhase is ExternalLearnerEvaluationPhase) {
            "LearnerResponsePhaseExecutionService only handle ExternalLearnerEvaluationPhase interaction ; provided: ${learnerPhase.javaClass}"
        }

        ExternalLearnerEvaluationPhaseExecution(
            sequence = learnerPhase.learnerSequence.sequence,
            userActiveInteraction = learnerPhase.learnerSequence.activeInteraction,
        )
    }
}
