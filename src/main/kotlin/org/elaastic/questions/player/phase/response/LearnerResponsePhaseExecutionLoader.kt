package org.elaastic.questions.player.phase.response

import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.elaastic.questions.player.phase.LearnerPhaseExecutionLoader
import org.elaastic.questions.player.phase.LearnerPhase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service("LearnerResponsePhaseExecutionLoader")
class LearnerResponsePhaseExecutionLoader(
    @Autowired val responseService: ResponseService
) : LearnerPhaseExecutionLoader {

    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution =
        when (learnerPhase) {
            is LearnerResponsePhase ->
                LearnerResponsePhaseExecution(
                    responseSubmitted = when (learnerPhase.state) {
                        State.beforeStart -> false
                        else -> responseService.hasResponseForUser(
                            learnerPhase.learnerSequence.learner,
                            learnerPhase.learnerSequence.sequence,
                            1
                        )

                    },
                    responseSubmissionSpecification = learnerPhase.learnerSequence.sequence.getResponseSubmissionInteraction().specification as ResponseSubmissionSpecification,
                )
            else -> throw IllegalArgumentException("LearnerResponsePhaseExecutionService only handle LearnerResponsePhase interaction ; provided: ${learnerPhase.javaClass}")
        }

}