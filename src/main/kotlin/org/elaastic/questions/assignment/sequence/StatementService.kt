package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.StatementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class StatementService(
        @Autowired val statementRepository: StatementRepository
) {
    fun save(statement: Statement) : Statement {
        return statementRepository.save(statement)
    }

    fun delete(statementId: Long) {
        statementRepository.deleteById(statementId)
    }
}