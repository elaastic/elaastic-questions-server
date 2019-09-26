package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.Sequence
import org.springframework.data.jpa.repository.JpaRepository


interface InteractionRepository : JpaRepository<Interaction, Long> {

    // TODO Sorting
    fun findAllBySequence(sequence: Sequence): List<Interaction>
}