package org.elaastic.questions.player.phase

interface LearnerPhaseExecutionService {

    fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution
}