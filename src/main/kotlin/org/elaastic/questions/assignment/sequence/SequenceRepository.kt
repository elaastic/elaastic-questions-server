package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.Assignment
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository


interface SequenceRepository : JpaRepository<Sequence, Long> {

    fun findAllByAssignment(assignment: Assignment, sort: Sort) : List<Sequence>
}