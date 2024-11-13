package org.elaastic.material.instructional.subject

import org.elaastic.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SubjectRepository : JpaRepository<Subject?, Long> {

    fun findAllByOwner(owner: User, pageable: Pageable): Page<Subject>

    fun findAllByOwnerAndCourseIsNull(owner: User, pageable: Pageable): Page<Subject>

    @EntityGraph(value = "Subject.statements_assignments", type = EntityGraph.EntityGraphType.LOAD)
    fun findOneWithStatementsAndAssignmentsById(id: Long): Subject?

    fun findOneById(id: Long): Subject?

    fun findByGlobalId(globalId: UUID): Subject?

    @Query("select count(s.id) from Subject as s where s.owner=?1 AND s.parentSubject = ?2")
    fun countAllByParentSubject(owner: User, parentSubject: Subject): Int

    @Query("select count(s.id) from Subject as s where s.owner=?1 AND s.title like ?2%")
    fun countAllStartingWithTitle(owner: User, title: String): Int

    fun countByCourseIsNullAndOwner(owner: User): Long

    fun findFirstByOwner(owner: User): Subject?

}