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


import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.lti.controller.LtiLaunchData
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.logging.Logger
import javax.transaction.Transactional

@SpringBootTest
@Transactional
internal class LmsUserAccountCreationServiceIntegrationTest(
        @Autowired val lmsUserAccountCreationService: LmsUserAccountCreationService,
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService,
        @Autowired val testingService: TestingService
) {

    internal var logger = Logger.getLogger(LmsUserAccountCreationServiceIntegrationTest::class.java.name)

    @Test
    fun `test generate password`() {
        tWhen {
            lmsUserAccountCreationService.generatePassword()
        }.tExpect {
            // expect generated password is OK
            logger.info("generated password: $it")
            assertThat(it.length, equalTo(8))
            it
        }.tExpect {
            // 2 generated password are not identical
            val p2 = lmsUserAccountCreationService.generatePassword()
            logger.info("Second generated password: $p2")
            assertThat(it, not(equalTo(p2)))
        }
    }

    @Test
    fun `test find more recent username starting wit a given username`() {
        tWhen {
            lmsUserAccountCreationService.findMostRecentUsernameStartingWithUsername("John_Doe___")
        }.tExpect {
            assertThat(it, equalTo("John_Doe___9"))
        }
        tWhen {
            lmsUserAccountCreationService.findMostRecentUsernameStartingWithUsername("NoUsername___")
        }.tExpect {
            assertThat(it, nullValue())
        }
    }

    @Test
    fun `test replace accent`() {
        tWhen {
            lmsUserAccountCreationService.replaceAccent("aébècàdêfïg")
        }.tExpect {
            assertThat(it, equalTo("aebecadefig"))
        }
    }

    @Test
    fun `test generate username`() {

        tWhen {
            // "I want to generate a username when there is not already the same username in the database"
            lmsUserAccountCreationService.generateUsername("John", "Dorel")
        }.tThen {
            // I obtain a username without index as suffix
            assertThat(it, equalTo("johdore"))
        }.tWhen {
            // the username exists
            User("John", "Dolores", "johdolo", "passwd", "joh@doe.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsUserAccountCreationService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo2"))
        }.tWhen {
            // the username exists with numerical suffix
            User("John", "Dolores15", "johdolo19", "passwd", "joh@doe15.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsUserAccountCreationService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with litteral suffix
            User("John", "Dolores16", "johdoloabcd", "passwd", "joh@doe16.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsUserAccountCreationService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with multiple sequences of digit suffix
            User("John", "Dolores16", "johdolo25ab29", "passwd", "joh@doe17.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsUserAccountCreationService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with multiple sequences of digit suffix, the first is smaller than another username numeric suffix
            User("John", "Dolores16", "johdolo18ab29", "passwd", "joh@doe18.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsUserAccountCreationService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }
    }

    @Test
    fun `test generate username with very short name`() {
        tWhen {
            // the username exists with very short name
                        lmsUserAccountCreationService.generateUsername("Jo", "Do")
        }.tThen {
            assertThat(it, equalTo("jodo"))
        }.tWhen {
            // a user with quadrigramm already used exists
            User("John", "Dolores", "jodo9", "passwd", "joh@doe19.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsUserAccountCreationService.generateUsername("Jo", "Do")
        }.tThen {
            assertThat(it, equalTo("jodo10"))
        }
    }

    @Test
    fun `test generate username with accents`() {
        tWhen {
            // I generate a username with firsname and lastname with  accents
            lmsUserAccountCreationService.generateUsername("Jérémie", "DÖrèl")
        }.tThen {
            assertThat(it, equalTo("jerdore"))
        }
    }

    @Test
    fun `test generate username with spaces in firstname or lastname`() {
        tWhen {
            // I generate a username with firsname and lastname with  accents
            lmsUserAccountCreationService.generateUsername("El Medie", "Ma Patrick")
        }.tThen {
            assertThat(it, equalTo("elmmapa"))
        }
    }

   @Test
   fun testCreateUserFromLtiData() {
       tGiven {
            testingService.getLtiLaunchDataComingFromBoBDeniroTeacher().toLtiUser()
       }.tWhen {
           lmsUserAccountCreationService.createUserFromLtiData(it)
       }.tThen {
           assertThat(it.id, notNullValue())
           assertThat(it.firstName, equalTo("Bob"))
           assertThat(it.lastName, equalTo("Deniro"))
           assertTrue(it.isTeacher())
       }
   }


}
