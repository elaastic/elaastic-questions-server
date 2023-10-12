package org.elaastic.questions.player.phase.evaluation.draxo

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.elaastic.questions.player.phase.LearnerPhase
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseExecutionLoader
import org.elaastic.questions.player.phase.evaluation.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("DraxoLearnerEvaluationPhaseExecutionLoader")
class DraxoLearnerEvaluationPhaseExecutionLoader(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
) : AbstractLearnerEvaluationPhaseExecutionLoader() {

    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution = run {
        require(learnerPhase is DraxoLearnerEvaluationPhase) {
            "LearnerResponsePhaseExecutionService only handle DraxoLearnerEvaluationPhase interaction ; provided: ${learnerPhase.javaClass}"
        }

        val sequence = learnerPhase.learnerSequence.sequence
        val learner = learnerPhase.learnerSequence.learner

        val secondAttemptAlreadySubmitted = responseService.hasResponseForUser(learner, sequence, 2)

        val responseIdAlreadyGradedList =
            peerGradingService.findAllEvaluation(learner, sequence).map { it.response.id }


        val responsesToGrade = responseService.findAllRecommandedResponsesForUser(
            sequence = sequence,
            attempt = sequence.whichAttemptEvaluate(),
            user = learner
        ).map { ResponseData(it) }
            .filter { responseData -> responseData.id !in responseIdAlreadyGradedList }


        val nextResponseToGrade = responsesToGrade.firstOrNull()

        return DraxoLearnerEvaluationPhaseExecution(
            userHasCompletedPhase2 = nextResponseToGrade == null,
            secondAttemptAlreadySubmitted = secondAttemptAlreadySubmitted,
            nextResponseToGrade = nextResponseToGrade,
            lastResponseToGrade = responsesToGrade.size == 1,
            sequence = learnerPhase.learnerSequence.sequence,
            userActiveInteraction = learnerPhase.learnerSequence.activeInteraction,
            lastAttemptResponse = responseService.find(learner, sequence, 2) ?: responseService.find(
                learner,
                sequence,
                1
            ),
        )
    }


}