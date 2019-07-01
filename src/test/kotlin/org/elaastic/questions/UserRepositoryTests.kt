package org.elaastic.questions


import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions.assertEquals

/**
 * @author John Tranier
 */
@SpringBootTest
class UserRepositoryTests(
        @Autowired val userRepository: UserRepository
) {

    @Test
    fun foo() {
        // assertEquals(2, userRepository.findAll().count())
    }
}