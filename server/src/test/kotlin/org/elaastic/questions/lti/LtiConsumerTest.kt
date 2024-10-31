/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
