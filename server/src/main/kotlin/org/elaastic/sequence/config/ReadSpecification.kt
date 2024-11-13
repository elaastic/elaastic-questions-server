package org.elaastic.sequence.config

import org.elaastic.sequence.interaction.InteractionType

/**
 * ReadSpecification is a class that represents the specification of the
 * Read phase.
 */
class ReadSpecification : InteractionSpecification {
    override fun getType(): InteractionType =
        InteractionType.Read

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReadSpecification) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


}