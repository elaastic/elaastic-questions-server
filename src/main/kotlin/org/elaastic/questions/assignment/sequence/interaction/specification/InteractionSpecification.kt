package org.elaastic.questions.assignment.sequence.interaction.specification

import org.elaastic.questions.assignment.sequence.interaction.InteractionType


interface InteractionSpecification {

    fun getType() : InteractionType
}