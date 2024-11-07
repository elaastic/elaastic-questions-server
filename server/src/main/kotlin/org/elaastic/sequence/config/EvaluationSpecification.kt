package org.elaastic.sequence.config

import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import javax.validation.constraints.Max

/**
 * EvaluationSpecification is a class that represents the specification of an evaluation interaction.
 * It contains the number of responses that the user has to evaluate.
 *
 * @property responseToEvaluateCount The number of responses that the user has to evaluate.
 */
data class EvaluationSpecification(

        @field:Max(5)
        var responseToEvaluateCount: Int
) : InteractionSpecification {

    override fun getType(): InteractionType {
        return InteractionType.Evaluation
    }

    fun setType(value: InteractionType) {
        assert(value == InteractionType.Evaluation)
    }

}