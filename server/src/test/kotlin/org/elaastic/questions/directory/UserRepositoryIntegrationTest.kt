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


import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.user.User
import org.elaastic.user.UserRepository
import org.elaastic.user.UserService
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.dao.DataIntegrityViolationException
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class UserRepositoryIntegrationTest(
    @Autowired val userRepository: UserRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userService: UserService
) {

    @Test
    fun `check test data`() {
        assertEquals(20, userRepository.findAll().count())
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

        assertEquals(21, userRepository.count())
    }

    @Test
    fun `save a  user without plain password`() {
        userRepository.save(
                User(
                        "John",
                        "Tranier",
                        "jtranier",
                        null,
                        "john.tranier@ticetime.com"
                ).let {
                    it.password = "encoded"
                    it
                }
        )

        assertEquals(21, userRepository.count())
    }

    @Test
    fun `save a  user without plain password and with paswword encoded`() {
        Assertions.assertThrows(ValidationException::class.java) { ->
            userRepository.save(
                    User(
                            "John",
                            "Tranier",
                            "jtranier",
                            null,
                            "john.tranier@ticetime.com"
                    ).let {
                        it.password = null
                        it
                    }
            )
        }
    }

    @Test
    fun `save a  user with too short plain password and with paswword encoded`() {
        Assertions.assertThrows(ValidationException::class.java) { ->
            userRepository.save(
                    User(
                            "John",
                            "Tranier",
                            "jtranier",
                            "123",
                            "john.tranier@ticetime.com"
                    ).let {
                        it.password = "encoded"
                        it
                    }
            )
        }
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

    @Test
    fun `find user by password reset key value`() {
        tWhen {
            // triggering user search by password reset key value with a bad value
            userRepository.findByPasswordResetKeyValue("bad-key")
        }.tThen {
            assertThat(it, nullValue())
        }
        var keyValue: String? = null
        tGiven {
            // user with password reset key
            integrationTestingService.getAnyUser().let {
                userService.generatePasswordResetKeyForUser(it).let { key ->
                    keyValue = key.passwordResetKey
                }
            }
        }
        tWhen {
            // triggering user search by password reset key value with a good value
            userRepository.findByPasswordResetKeyValue(keyValue!!)
        }.tThen {
            assertThat(it, equalTo(integrationTestingService.getAnyUser()))
        }
    }
}
