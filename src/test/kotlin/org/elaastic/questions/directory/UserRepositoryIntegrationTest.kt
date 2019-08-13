package org.elaastic.questions.directory


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.dao.DataIntegrityViolationException
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException


@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest(
        @Autowired val userRepository: UserRepository
) {

    @Test
    fun `check test data`() {
        assertEquals(17, userRepository.findAll().count())
    }

    @Test
    fun `save a valid user`() {
        userRepository.save(
                User(
                        "John",
                        "Tranier",
                        "jtranier",
                        "1234",
                        "john.tranier@ticetime.com"
                ).let {
                    it.password = "encoded"
                    it
                }
        )

        assertEquals(18, userRepository.count())
    }

    @Test
    fun `a user must have a firstname and a lastname`() {
        Assertions.assertThrows(ConstraintViolationException::class.java) { ->
            userRepository.save(
                    User(
                            "",
                            "Tranier",
                            "jtranier",
                            "1234",
                            "john.tranier@ticetime.com"
                    ).let {
                        it.password = "encoded"
                        it
                    }
            )
        }

        Assertions.assertThrows(ConstraintViolationException::class.java) { ->
            User(
                    "John",
                    "",
                    "jtranier",
                    "1234",
                    "john.tranier@ticetime.com"
            ).let {
                it.password = "encoded"
                it
            }. let {
                userRepository.save(it)
            }
        }
    }

    @Test
    fun `username must be unique`() {
        Assertions.assertThrows(DataIntegrityViolationException::class.java) { ->
            userRepository.save(
                    User(
                            "John",
                            "Tranier",
                            "jtranier",
                            "1234",
                            "john.tranier@ticetime.com"
                    ).let {
                        it.password = "encoded"
                        it
                    }
            )

            userRepository.save(
                    User(
                            "J",
                            "T",
                            "jtranier",
                            "1234",
                            "john.tranier@ticetime.com"
                    ).let {
                        it.password = "encoded"
                        it
                    }
            )
        }
    }

    @Test
    fun `findByUsername - existing user`() {
        val user = User(
                "John",
                "Tranier",
                "jtranier",
                "1234",
                "john.tranier@ticetime.com"
        ).let {
            it.password = "encoded"
            it
        }
        userRepository.save(user)

        assertEquals(
                user.id,
                userRepository.findByUsername("jtranier")?.id
                )
    }

    @Test
    fun `findByUsername - unknown username`() {
        Assertions.assertNull(userRepository.findByUsername("foo"))
    }

    @Test
    fun `I can retrieve user roles`() {
        assertEquals(
                listOf("STUDENT_ROLE"),
                userRepository.findByUsername("john_doe___1")?.roles?.map { it -> it.name }
        )
    }
}
