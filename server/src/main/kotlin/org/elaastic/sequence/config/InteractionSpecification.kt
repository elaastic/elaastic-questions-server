package org.elaastic.sequence.config

import org.elaastic.questions.assignment.sequence.interaction.InteractionType

interface InteractionSpecification {

    fun getType() : InteractionType
}