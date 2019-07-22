package org.elaastic.questions.assignment.sequence.interaction.specification

import org.elaastic.questions.assignment.sequence.interaction.InteractionType

/**
 * @author John Tranier
 */
interface InteractionSpecification {

    fun getType() : InteractionType
}