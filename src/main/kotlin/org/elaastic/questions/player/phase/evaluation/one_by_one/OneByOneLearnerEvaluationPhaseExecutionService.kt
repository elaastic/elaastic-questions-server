package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.elaastic.questions.player.phase.LearnerPhase
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseExecutionService
import org.elaastic.questions.player.phase.evaluation.ResponseData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OneByOneLearnerEvaluationPhaseExecutionService(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
) : AbstractLearnerEvaluationPhaseExecutionService() {

    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution = run {
        if (learnerPhase !is OneByOneLearnerEvaluationPhase)
            throw IllegalArgumentException("LearnerResponsePhaseExecutionService only handle OneByOneLearnerEvaluationPhase interaction ; provided: ${learnerPhase.javaClass}")

        val sequence = learnerPhase.learnerSequence.sequence
        val learner = learnerPhase.learnerSequence.learner

        val secondAttemptAlreadySubmitted = responseService.hasResponseForUser(learner, sequence, 2)

        val responsesToGrade = if (secondAttemptAlreadySubmitted)
            listOf()
        else
            responseService.findAllRecommandedResponsesForUser(
                sequence = sequence,
                attempt = sequence.whichAttemptEvaluate(),
                user = learner
            ).map { ResponseData(it) }

        val responsesAlreadyGraded: List<PeerGrading> = peerGradingService.findAllEvaluation(learner, sequence)

        val nextResponseToGrade = responsesToGrade.find { responseData ->
            responseData.id !in responsesAlreadyGraded.map {
                it.response.id // TODO should be fetched first
            }
        }

        return OneByOneLearnerEvaluationPhaseExecution(
            userHasCompletedPhase2 = nextResponseToGrade == null,
            secondAttemptAlreadySubmitted = secondAttemptAlreadySubmitted,
            nextResponseToGrade = nextResponseToGrade,
            sequence = learnerPhase.learnerSequence.sequence,
            userActiveInteraction = learnerPhase.learnerSequence.activeInteraction,
            firstAttemptResponse = responseService.find(learner, sequence),
        )
    }


}