package org.elaastic.questions.assignment.sequence.interaction.specification

import org.elaastic.questions.assignment.sequence.interaction.InteractionType

class ReadSpecification : InteractionSpecification {
    override fun getType(): InteractionType =
            InteractionType.Read

}