package org.elaastic.questions.test

import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.StatementRepository
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author John Tranier
 */
@Service
class TestingService(
        @Autowired val userRepository: UserRepository,
        @Autowired val statementRepository: StatementRepository
) {

    fun getAnyUser(): User {
        return userRepository.findAll().iterator().next()
    }

    fun getAnyStatement(): Statement {
        return statementRepository.findAll().iterator().next()
    }

}