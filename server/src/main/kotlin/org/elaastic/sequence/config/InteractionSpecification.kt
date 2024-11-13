package org.elaastic.sequence.config

import org.elaastic.sequence.interaction.InteractionType

interface InteractionSpecification {

    fun getType() : InteractionType
}