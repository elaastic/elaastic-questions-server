package org.elaastic.sequence.phase

/**
 * @see LearnerPhase
 */
interface LearnerPhaseExecutionLoader {

    fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution
}