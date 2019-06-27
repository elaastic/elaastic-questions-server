package org.elaastic.questions

import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author John Tranier
 */
interface StatementRepository : JpaRepository<Statement, Long> {

    fun getByTitle(title: String): Statement?

    fun getAllByTitleIsNotNull(): List<Statement>
}