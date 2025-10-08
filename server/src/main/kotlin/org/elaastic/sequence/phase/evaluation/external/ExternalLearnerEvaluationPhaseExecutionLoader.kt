package org.elaastic.sequence.phase.evaluation.external

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ResponseService
import org.elaastic.sequence.phase.LearnerPhase
import org.elaastic.sequence.phase.LearnerPhaseExecution
import org.elaastic.sequence.phase.evaluation.AbstractLearnerEvaluationPhaseExecutionLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("ExternalLearnerEvaluationPhaseExecutionLoader")
class ExternalLearnerEvaluationPhaseExecutionLoader(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
) : AbstractLearnerEvaluationPhaseExecutionLoader() {
    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution = run {
        require(learnerPhase is ExternalLearnerEvaluationPhase) {
            "LearnerResponsePhaseExecutionService only handle ExternalLearnerEvaluationPhase interaction ; provided: ${learnerPhase.javaClass}"
        }

        val sequence = learnerPhase.learnerSequence.sequence
        val learner = learnerPhase.learnerSequence.learner

        val secondAttemptAlreadySubmitted = responseService.hasResponseForUser(learner, sequence, 2)

        ExternalLearnerEvaluationPhaseExecution(
            userHasCompletedPhase2 = false, // TODO: find a way to get this information right!
            secondAttemptAlreadySubmitted = secondAttemptAlreadySubmitted,
            sequence = sequence,
            userActiveInteraction = learnerPhase.learnerSequence.activeInteraction,
            lastAttemptResponse = responseService.find(learner, sequence, 2) ?: responseService.find(
                learner,
                sequence,
                1
            ),
        )
    }
}
