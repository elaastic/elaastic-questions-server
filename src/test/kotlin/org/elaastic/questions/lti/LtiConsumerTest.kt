package org.elaastic.questions.lti

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.logging.Logger
import javax.validation.Validation
import javax.validation.Validator


internal class LtiConsumerTest {

    val logger = Logger.getLogger(LtiConsumerTest::class.java.name)
    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `test validation on valid lti consumer`() {
        // given a valid obj
        val validObj = LtiConsumer(
                consumerName = "Moodle From",
                secret = "secret pass",
                key = "abcd1234"
        ).let {
            it.dateCreated = Date()
            it.lastUpdated = Date()
            it
        }

        logger.info(validObj.toString())
        logger.info(validator.validate(validObj).toString())
        // expect validation succeeds
        assertThat(validator.validate(validObj).isEmpty(), equalTo(true))
        // when setting correctly properties
        validObj.enableFrom = Date()
        // then validation still succeeds
        assertThat(validator.validate(validObj).isEmpty(), equalTo(true))
    }

    @Test
    fun `test validation on invalid lti consumer`() {
        // given a non valid object
        val nonValidObj = LtiConsumer(
                consumerName = "Moodle From",
                secret = "secret pass",
                key = ""
        ).let {
            it.dateCreated = Date()
            it.lastUpdated = Date()
            it
        }
        // expect validation fails
        assertThat(validator.validate(nonValidObj).isEmpty(), equalTo(false))
    }

}
