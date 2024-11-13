package org.elaastic.material.instructional.statement

import org.elaastic.material.instructional.subject.Subject
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface StatementRepository : JpaRepository<Statement, Long> {

    @EntityGraph(value = "Statement.title", type = EntityGraph.EntityGraphType.LOAD)
    fun findOneById(id: Long) : Statement?

    fun countAllBySubject(subject: Subject) : Int

    fun findBySubject(subject: Subject): List<Statement>

}