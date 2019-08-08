package org.elaastic.questions.directory

import org.elaastic.questions.test.TestingService

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.logging.Logger
import javax.validation.Validation
import javax.validation.Validator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest
@Transactional
internal class PasswordResetKeyIntegrationTest(
        @Autowired val testingService: TestingService,
        @Autowired val passwordResetKeyRepository: PasswordResetKeyRepository
) {

    val logger = Logger.getLogger(PasswordResetKeyIntegrationTest::class.java.name)
    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `test validaton of a valid password reset key`() {
        // given a valid password reset key
        val passKey = PasswordResetKey(
                passwordResetKey = "1234",
                user = testingService.getAnyUser()
        )
        passKey.dateCreated = Date()

        // expect validating the pass key succeeds
        assertThat(validator.validate(passKey).isEmpty(), equalTo(true))
    }

    @Test
    fun `test validation of an invalid password reset key`() {
        // given a pass key with a blank key
        val key = PasswordResetKey(
                passwordResetKey = "",
                user = testingService.getAnyUser()
        )
        key.dateCreated = Date()

        // expect validating the pass key fails
        assertThat(validator.validate(key).isEmpty(), equalTo(false))

    }

    @Test
    fun `test save of a valid password reset key`() {
        // given a valid pass key
        val key = PasswordResetKey(
                passwordResetKey = "1234",
                user = testingService.getAnyUser()
        )

        // when saving the pass key
        passwordResetKeyRepository.saveAndFlush(key)

        // then actKey has an id a version and a created date
        assertThat(key.id, notNullValue())
        assertThat(key.version, equalTo(0L))
        assertThat(key.dateCreated, notNullValue())
    }

    @Test
    fun `test save of a non valid password reset key`() {
        // given a non valid pass key
        val key = PasswordResetKey(
                passwordResetKey = "",
                user = testingService.getAnyUser()
        )

        // expect an exception is thrown when saving
        assertThrows<ConstraintViolationException> {
            passwordResetKeyRepository.save(key)
        }
    }
}
