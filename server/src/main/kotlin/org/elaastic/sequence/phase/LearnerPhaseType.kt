package org.elaastic.sequence.phase

/**
 * Enum representing the different types of phases.
 * @property RESPONSE the response phase
 * @property EVALUATION the evaluation phase
 * @property RESULT the result phase
 * @see LearnerPhase
 */
enum class LearnerPhaseType {
    /**
     * The response phase, where the student answers the question
     */
    RESPONSE,

    /**
     * The evaluation phase, where the student evaluated their peers answers
     */
    EVALUATION,

    /**
     * The result phase, where the student can see the results of the sequence
     */
    RESULT
}