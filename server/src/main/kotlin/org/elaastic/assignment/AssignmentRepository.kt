package org.elaastic.assignment

import org.elaastic.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface AssignmentRepository : JpaRepository<Assignment, Long> {

    fun findAllByOwner(owner: User, pageable: Pageable): Page<Assignment>

    @EntityGraph(value = "Assignment.sequences", type = EntityGraph.EntityGraphType.LOAD)
    fun findOneWithSequencesById(id: Long): Assignment?

    fun findOneById(id: Long): Assignment?

    fun findByGlobalId(globalId: UUID): Assignment?

    @EntityGraph(value = "Assignment.sequences", type = EntityGraph.EntityGraphType.LOAD)
    fun findWithSequenceByGlobalId(globalId: UUID): Assignment?

    @EntityGraph(value = "Assignment.sequences", type = EntityGraph.EntityGraphType.LOAD)
    fun findAllBySubjectIsNull(): List<Assignment>


    @Query("SELECT DISTINCT a FROM Assignment a LEFT JOIN FETCH a.sequences s " +
            "WHERE a.lastUpdated > :since OR s.lastUpdated > :since")
    fun findAllAssignmentUpdatedSince(since: Date): List<Assignment>

    @Modifying
    @Query("UPDATE assignment a SET a.last_updated = :now WHERE a.id = :assignmentId", nativeQuery = true)
    fun updateLastUpdated(assignmentId: Long, now: Date)
}