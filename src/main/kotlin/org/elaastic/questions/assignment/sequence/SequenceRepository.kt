package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.Assignment
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository


interface SequenceRepository : JpaRepository<Sequence, Long> {

    @EntityGraph(value = "Sequence.statement", type = EntityGraph.EntityGraphType.LOAD)
    fun findOneById(id: Long) : Sequence?

    fun countAllByAssignment(assignment: Assignment) : Int
}