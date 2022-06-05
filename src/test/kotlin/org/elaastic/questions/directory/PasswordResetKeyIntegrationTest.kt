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

import org.apache.commons.lang3.time.DateUtils
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import java.util.logging.Logger
import javax.validation.Validation
import javax.validation.Validator
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class PasswordResetKeyIntegrationTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val passwordResetKeyRepository: PasswordResetKeyRepository,
    @Autowired val userService: UserService
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
                user = integrationTestingService.getAnyUser()
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
                user = integrationTestingService.getAnyUser()
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
                user = integrationTestingService.getAnyUser()
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
                user = integrationTestingService.getAnyUser()
        )

        // expect an exception is thrown when saving
        assertThrows<ConstraintViolationException> {
            passwordResetKeyRepository.save(key)
        }
    }

    @Test
    fun `test find all password reset keys`() {
        var users: List<User>? = null
        tGiven {
            // 2 users with password reset keys freshly generated
            users = listOf(integrationTestingService.getTestStudent(), integrationTestingService.getTestTeacher())
            users!!.forEach {
                userService.generatePasswordResetKeyForUser(it)
            }
        }.tWhen {
            // searching all pasword reset keys
            passwordResetKeyRepository.findAllPasswordResetKeys(DateUtils.addHours(Date(), -1))
        }.tThen {
            assertThat (it.size, equalTo(2))
            it.forEach { key ->
                assertThat(users, hasItem(key.user) )
                assertThat(key.user.settings, notNullValue())
            }
        }.tWhen {
            // the email has been sent for one the users
            passwordResetKeyRepository.findByUser(users!![1]).let {
                it!!.passwordResetEmailSent = true
                passwordResetKeyRepository.saveAndFlush(it)
            }
        }.tWhen {
            // searching all pasword reset keys
            passwordResetKeyRepository.findAllPasswordResetKeys(DateUtils.addHours(Date(), -1))
        }.tThen {
            // only one key is found
            assertThat (it.size, equalTo(1))
            it.forEach { key ->
                assertThat(key.user, equalTo(users!![0]))
            }
        }.tWhen {
            // the date of the last guy has expired
            passwordResetKeyRepository.findByUser(users!![0]).let {
                it!!.dateCreated = DateUtils.addHours(it.dateCreated, -2)
                passwordResetKeyRepository.saveAndFlush(it)
            }
        }.tWhen {
            // searching all pasword reset keys
            passwordResetKeyRepository.findAllPasswordResetKeys(DateUtils.addHours(Date(), -1))
        }.tThen {
            // no more key is found
            assertThat(it.size, equalTo(0))
        }
    }
}
