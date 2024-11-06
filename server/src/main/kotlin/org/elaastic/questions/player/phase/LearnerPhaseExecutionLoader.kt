package org.elaastic.questions.player.phase

/**
 * @see LearnerPhase
 */
interface LearnerPhaseExecutionLoader {

    fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution
}