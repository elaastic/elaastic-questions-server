package org.elaastic.questions.lti

import org.elaastic.questions.directory.Role
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.logging.Logger
import javax.transaction.Transactional

@SpringBootTest
@Transactional
internal class LmsServiceIntegrationTest(
        @Autowired val lmsService: LmsService,
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService
) {

    internal var logger = Logger.getLogger(LmsServiceIntegrationTest::class.java.name)

    @Test
    fun `test generate password`() {
        tWhen {
            lmsService.generatePassword()
        }.tExpect {
            // expect generated password is OK
            logger.info("generated password: $it")
            assertThat(it.length, equalTo(8))
            it
        }.tExpect {
            // 2 generated password are not identical
            val p2 = lmsService.generatePassword()
            logger.info("Second generated password: $p2")
            assertThat(it, not(equalTo(p2)))
        }
    }

    @Test
    fun `test find more recent username starting wit a given username`() {
        tWhen {
            lmsService.findMostRecentUsernameStartingWithUsername("John_Doe___")
        }.tExpect {
            assertThat(it, equalTo("John_Doe___9"))
        }
        tWhen {
            lmsService.findMostRecentUsernameStartingWithUsername("NoUsername___")
        }.tExpect {
            assertThat(it, nullValue())
        }
    }

    @Test
    fun `test replace accent`() {
        tWhen {
            lmsService.replaceAccent("aébècàdêfïg")
        }.tExpect {
            assertThat(it, equalTo("aebecadefig"))
        }
    }

    @Test
    fun `test generate username`() {

        tWhen {
            // "I want to generate a username when there is not already the same username in the database"
            lmsService.generateUsername("John", "Dorel")
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
            lmsService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo2"))
        }.tWhen {
            // the username exists with numerical suffix
            User("John", "Dolores15", "johdolo19", "passwd", "joh@doe15.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with litteral suffix
            User("John", "Dolores16", "johdoloabcd", "passwd", "joh@doe16.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with multiple sequences of digit suffix
            User("John", "Dolores16", "johdolo25ab29", "passwd", "joh@doe17.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with multiple sequences of digit suffix, the first is smaller than another username numeric suffix
            User("John", "Dolores16", "johdolo18ab29", "passwd", "joh@doe18.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }
    }

    @Test
    fun `test generate username with very short name`() {
        tWhen {
            // the username exists with very short name
                        lmsService.generateUsername("Jo", "Do")
        }.tThen {
            assertThat(it, equalTo("jodo"))
        }.tWhen {
            // a user with quadrigramm already used exists
            User("John", "Dolores", "jodo9", "passwd", "joh@doe19.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            lmsService.generateUsername("Jo", "Do")
        }.tThen {
            assertThat(it, equalTo("jodo10"))
        }
    }

    @Test
    fun `test generate username with accents`() {
        tWhen {
            // I generate a username with firsname and lastname with  accents
            lmsService.generateUsername("Jérémie", "DÖrèl")
        }.tThen {
            assertThat(it, equalTo("jerdore"))
        }
    }

    @Test
    fun `test generate username with spaces in firstname or lastname`() {
        tWhen {
            // I generate a username with firsname and lastname with  accents
            lmsService.generateUsername("El Medie", "Ma Patrick")
        }.tThen {
            assertThat(it, equalTo("elmmapa"))
        }
    }

}
