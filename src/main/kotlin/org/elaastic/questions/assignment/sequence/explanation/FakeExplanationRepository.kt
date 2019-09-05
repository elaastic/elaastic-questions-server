package org.elaastic.questions.assignment.sequence.explanation

import org.elaastic.questions.assignment.Statement
import org.springframework.data.jpa.repository.JpaRepository


interface FakeExplanationRepository : JpaRepository<FakeExplanation, Long> {

    fun findAllByStatement(statement: Statement): List<FakeExplanation>

    fun deleteAllByStatement(statement: Statement): Long
}