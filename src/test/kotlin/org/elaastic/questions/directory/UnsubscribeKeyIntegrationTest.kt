package org.elaastic.questions.directory

import org.elaastic.questions.test.TestingService

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.logging.Logger
import javax.validation.Validation
import javax.validation.Validator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest
@Transactional
internal class UnsubscribeKeyIntegrationTest(
        @Autowired val testingService: TestingService,
        @Autowired val unsubscribeKeyRepository: UnsubscribeKeyRepository
) {

    val logger = Logger.getLogger(UnsubscribeKeyIntegrationTest::class.java.name)
    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `test validaton of a valid unsubscribe key`() {
        // given a valid object
        val validObj = UnsubscribeKey(
                unsubscribeKey = "1234",
                user = testingService.getAnyUser()
        )

        // expect validating the object succeeds
        assertThat(validator.validate(validObj).isEmpty(), equalTo(true))
    }

    @Test
    fun `test validation of an invalid unsubscribe key`() {
        // given a non valid object
        val noValidObj = UnsubscribeKey(
                unsubscribeKey = "",
                user = testingService.getAnyUser()
        )

        // expect validating the object
        assertThat(validator.validate(noValidObj).isEmpty(), equalTo(false))

    }

    @Test
    fun `test save of a valid unsubscribe key`() {
        // given a valid object
        val validObj = UnsubscribeKey(
                unsubscribeKey = "fr",
                user = testingService.getAnyUser()
        )

        // when saving the obj
        unsubscribeKeyRepository.saveAndFlush(validObj)

        // then object has an id a version
        assertThat(validObj.id, notNullValue())
        assertThat(validObj.version, equalTo(0L))

    }

    @Test
    fun `test save of a non valid unsubscribe key`() {
        // given a non valid obj
        val nonValidObj = UnsubscribeKey(
                unsubscribeKey = "",
                user = testingService.getAnyUser()
        )

        // expect an exception is thrown when saving
        assertThrows<ConstraintViolationException> { unsubscribeKeyRepository.save(nonValidObj) }
    }

}

