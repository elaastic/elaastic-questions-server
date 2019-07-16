package org.elaastic.questions.test

import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author John Tranier
 */
@Service
class TestingService(
        @Autowired val userRepository: UserRepository
) {

    fun getAnyUser(): User {
        return userRepository.findAll().iterator().next()
    }

}