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

package org.elaastic.questions.directory

import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.user.Settings
import org.elaastic.user.SettingsRepository

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
internal class SettingsIntegrationTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val settingsRepository: SettingsRepository
) {

    val logger = Logger.getLogger(SettingsIntegrationTest::class.java.name)
    lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `test validaton of a valid settings`() {
        // given a valid object
        val validObj = Settings(
                language = "fr",
                user = integrationTestingService.getAnyUser()
        )

        // expect validating the object succeeds
        assertThat(validator.validate(validObj).isEmpty(), equalTo(true))
    }

    @Test
    fun `test validation of an invalid settings`() {
        // given a non valid object
        val noValidObj = Settings(
                language = "",
                user = integrationTestingService.getAnyUser()
        )

        // expect validating the object
        assertThat(validator.validate(noValidObj).isEmpty(), equalTo(false))

    }

    @Test
    fun `test save of a valid settings`() {
        // given a valid object
        val validObj = Settings(
                language = "fr",
                user = integrationTestingService.getAnyUser()
        )

        // when saving the obj
        settingsRepository.saveAndFlush(validObj)

        // then object has an id a version
        assertThat(validObj.id, notNullValue())
        assertThat(validObj.version, equalTo(0L))

    }

    @Test
    fun `test save of a non valid settings`() {
        // given a non valid obj
        val nonValidObj = Settings(
                language = "",
                user = integrationTestingService.getAnyUser()
        )

        // expect an exception is thrown when saving
        assertThrows<ConstraintViolationException> { settingsRepository.save(nonValidObj) }
    }



}

