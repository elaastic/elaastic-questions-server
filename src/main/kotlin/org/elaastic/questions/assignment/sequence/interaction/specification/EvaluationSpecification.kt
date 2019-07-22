package org.elaastic.questions.assignment.sequence.interaction.specification

import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import javax.validation.constraints.Max

/**
 * @author John Tranier
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