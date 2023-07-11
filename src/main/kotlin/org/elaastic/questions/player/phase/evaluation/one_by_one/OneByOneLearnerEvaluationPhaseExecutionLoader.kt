package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.elaastic.questions.player.phase.LearnerPhase
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseExecutionLoader
import org.elaastic.questions.player.phase.evaluation.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("OneByOneLearnerEvaluationPhaseExecutionLoader")
class OneByOneLearnerEvaluationPhaseExecutionLoader(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
) : AbstractLearnerEvaluationPhaseExecutionLoader() {

    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution = run {
        require(learnerPhase is OneByOneLearnerEvaluationPhase) {
            "LearnerResponsePhaseExecutionService only handle OneByOneLearnerEvaluationPhase interaction ; provided: ${learnerPhase.javaClass}"
        }

        val sequence = learnerPhase.learnerSequence.sequence
        val learner = learnerPhase.learnerSequence.learner

        val secondAttemptAlreadySubmitted = responseService.hasResponseForUser(learner, sequence, 2)

        val responseIdAlreadyGradedList =
            peerGradingService.findAllEvaluation(learner, sequence).map { it.response.id  }

        val responsesToGrade = if (secondAttemptAlreadySubmitted)
            listOf()
        else
            responseService.findAllRecommandedResponsesForUser(
                sequence = sequence,
                attempt = sequence.whichAttemptEvaluate(),
                user = learner
            ).map { ResponseData(it) }
                .filter { responseData -> responseData.id !in responseIdAlreadyGradedList }

        val nextResponseToGrade = responsesToGrade.firstOrNull()

        return OneByOneLearnerEvaluationPhaseExecution(
            userHasCompletedPhase2 = nextResponseToGrade == null,
            secondAttemptAlreadySubmitted = secondAttemptAlreadySubmitted,
            nextResponseToGrade = nextResponseToGrade,
            lastResponseToGrade =  responsesToGrade.size == 1,
            sequence = learnerPhase.learnerSequence.sequence,
            userActiveInteraction = learnerPhase.learnerSequence.activeInteraction,
            firstAttemptResponse = responseService.find(learner, sequence),
        )
    }


}