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

package org.elaastic.user

import org.elaastic.test.IntegrationTestingService

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class UnsubscribeKeyIntegrationTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
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
                user = integrationTestingService.getAnyUser()
        )

        // expect validating the object succeeds
        assertThat(validator.validate(validObj).isEmpty(), equalTo(true))
    }

    @Test
    fun `test validation of an invalid unsubscribe key`() {
        // given a non valid object
        val noValidObj = UnsubscribeKey(
                unsubscribeKey = "",
                user = integrationTestingService.getAnyUser()
        )

        // expect validating the object
        assertThat(validator.validate(noValidObj).isEmpty(), equalTo(false))

    }

    @Test
    fun `test save of a valid unsubscribe key`() {
        // given a valid object
        val validObj = UnsubscribeKey(
                unsubscribeKey = "fr",
                user = integrationTestingService.getAnyUser()
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
                user = integrationTestingService.getAnyUser()
        )

        // expect an exception is thrown when saving
        assertThrows<ConstraintViolationException> { unsubscribeKeyRepository.save(nonValidObj) }
    }

}

