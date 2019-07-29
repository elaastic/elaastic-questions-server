package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author John Tranier
 */
interface AssignmentRepository : JpaRepository<Assignment, Long> {

    fun findAllByOwner(owner: User, pageable: Pageable): Page<Assignment>

    @EntityGraph(value = "Assignment.sequences", type = EntityGraph.EntityGraphType.LOAD)
    fun getWithSequencesById(id: Long): Assignment

    fun getById(id: Long): Assignment
}