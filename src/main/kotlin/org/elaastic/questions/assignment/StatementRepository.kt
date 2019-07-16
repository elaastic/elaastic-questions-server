package org.elaastic.questions.assignment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

/**
 * @author John Tranier
 */

interface StatementRepository : JpaRepository<Statement, Long> {

    fun save(statement: Statement) : Statement
    fun saveAndFlush(statement: Statement) : Statement
    override fun findAll(): List<Statement>

}