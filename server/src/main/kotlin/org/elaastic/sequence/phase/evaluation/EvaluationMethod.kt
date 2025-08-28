package org.elaastic.sequence.phase.evaluation

import org.elaastic.sequence.phase.descriptor.PhaseConfig

/**
 * Enumeration of the different configuration for the evaluation phase.
 *
 * @property ALL_AT_ONCE Show all the response to evaluate with a Likert scale
 * @property DRAXO Evaluate a response with DRAXO method
 */
enum class EvaluationMethod {
    /** Show all the response to evaluate with a Likert scale */
    ALL_AT_ONCE,

    /**
     * Evaluate a response with DRAXO method
     * This method allows the evaluator to provide textual feedback.
     */
    DRAXO;
}