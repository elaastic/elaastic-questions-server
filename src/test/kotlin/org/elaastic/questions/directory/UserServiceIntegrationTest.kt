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
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class UserServiceIntegrationTest(
    @Autowired val userService: UserService,
    @Autowired val roleService: RoleService,
    @Autowired val settingsRepository: SettingsRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager,
    @Autowired val activationKeyRepository: ActivationKeyRepository,
    @Autowired val unsubscribeKeyRepository: UnsubscribeKeyRepository,
    @Autowired val passwordResetKeyRepository: PasswordResetKeyRepository,
    @Autowired val passwordEncoder: PasswordEncoder,
    @Autowired val userRepository: UserRepository
) {

    @Test
    fun addUserWithoutCheckingOfEmail() {

        tWhen {
            // adding a user
            userService.addUser(
                User(
                    username = "foo",
                    firstName = "f",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = "foo@elaastic.org"
                ).addRole(roleService.roleStudent())
            )
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.password, notNullValue())
            assertThat(it.password, not(equalTo("1234")))
            assertThat(it.enabled, equalTo(true))
            assertThat("settings must be set", it.settings, notNullValue())
            // and activation is not set and unsubscribe key is set
            assertThat(activationKeyRepository.findByUser(it), nullValue())
            assertThat(unsubscribeKeyRepository.findByUser(it), notNullValue())
            assertTrue(userService.userHasGivenConsentToActiveTerms(it.username))
            it
        }.tWhen {
            // refreshing the user and fecthing the settings
            entityManager.refresh(it)
            it
        }.tThen {
            assertThat(settingsRepository.findByUser(it), equalTo(it.settings))
        }
    }

    @Test
    fun addUserWithCheckingOfEmail() {

        tWhen {
            // adding a user
            userService.addUser(
                User(
                    username = "foo",
                    firstName = "f",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = "foo@elaastic.org"
                ).addRole(roleService.roleStudent()),
                "fr",
                true
            )
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.password, notNullValue())
            assertThat(it.password, not(equalTo("1234")))
            assertThat(it.enabled, equalTo(false))
            assertThat("settings must be set", it.settings, notNullValue())
            // and activation and unsubscribe key are set
            val activationKey = activationKeyRepository.findByUser(it)!!
            assertThat(activationKey.dateCreated, notNullValue())
            assertThat(unsubscribeKeyRepository.findByUser(it), notNullValue())
            assertTrue(userService.userHasGivenConsentToActiveTerms(it.username))
            it
        }
    }

    @Test
    fun addUserWithError() {
        assertThrows<ConstraintViolationException> {
            userService.addUser(
                User(
                    username = "foo",
                    firstName = "f",
                    lastName = "oo",
                    plainTextPassword = "1",
                    email = "foo@elaastic.org"
                ).addRole(roleService.roleStudent())
            )
        }

        assertThrows<ConstraintViolationException> {
            userService.addUser(
                User(
                    username = "foo",
                    firstName = "f",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = "fooelaastic.org"
                ).addRole(roleService.roleStudent())
            )
        }
    }

    @Test
    fun `test initialization of settings for a given user `() {
        // given a user
        integrationTestingService.getAnyUser().tWhen {
            userService.initializeSettingsForUser(it, "fr")
        }.tThen {
            assertThat(it.user, equalTo(integrationTestingService.getAnyUser()))
            assertThat(it.language, equalTo("fr"))
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(0L))
        }

    }

    @Test
    fun `test initialization of unsubscribe key for a given user `() {
        // given a user
        integrationTestingService.getAnyUser().tWhen {
            userService.initializeUnsubscribeKeyForUser(it)
        }.tThen {
            assertThat(it.user, equalTo(integrationTestingService.getAnyUser()))
            assertThat(it.unsubscribeKey, notNullValue())
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(0L))
        }
    }

    @Test
    fun `test initialization of activation key for a given user `() {
        // given a user
        integrationTestingService.getAnyUser().tWhen {
            userService.initializeActivationKeyForUser(it)
        }.tThen {
            assertThat(it.user, equalTo(integrationTestingService.getAnyUser()))
            assertThat(it.activationKey, notNullValue())
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(0L))
            assertThat(it.dateCreated, notNullValue())
            assertFalse(it.activationEmailSent)
        }
    }

    @Test
    fun `test find user by email`() {
        tGiven {
            // a user
            userService.addUser(
                User(
                    username = "foo",
                    firstName = "f",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = "foo@elaastic.org"
                ).addRole(roleService.roleStudent()),
                "fr",
                true
            )
        }.tWhen {
            // triggering research by email with the email of the user
            userService.findAllByEmail(it.email!!)
        }.tThen {
            assertFalse(it.isEmpty())
            assertThat(it.last().username, equalTo("foo"))
        }.tWhen {
            // triggering research by email with an unknown email
            userService.findAllByEmail("john@doe.fr")
        }.tThen {
            assertTrue(it.isEmpty())
        }
    }

    @Test
    fun `test generate new password reset key`() {
        tGiven {
            // a user without password reset key
            integrationTestingService.getAnyUser().let {
                assertThat(passwordResetKeyRepository.findByUser(it), nullValue())
                it
            }
        }.tWhen {
            // triggering gneration of a password reset key
            userService.generatePasswordResetKeyForUser(it)
        }.tThen {
            // the password reset key has been generated and saved
            assertThat(it.id, notNullValue())
            entityManager.refresh(it)
            assertThat(it.dateCreated, notNullValue())
            assertFalse(it.passwordResetEmailSent)
            assertThat(it.passwordResetKey, notNullValue())
        }

    }


    @Test
    fun `test generate recycled password reset key`() {
        var oldKey: String? = null
        tGiven {
            // a user with an old password reset key
            integrationTestingService.getAnyUser().let {
                userService.generatePasswordResetKeyForUser(it).let { prk ->
                    oldKey = prk.passwordResetKey
                    prk.dateCreated = DateUtils.addHours(Date(), -2)
                    prk.passwordResetEmailSent = true
                    passwordResetKeyRepository.saveAndFlush(prk)
                }
                it
            }
        }.tWhen {
            // triggering gneration of a password reset key
            userService.generatePasswordResetKeyForUser(it)
        }.tThen {
            // the password reset key has been recycled and saved
            entityManager.refresh(it)
            assertThat(it.dateCreated, Matchers.greaterThan(DateUtils.addHours(Date(), -1)))
            assertFalse(it.passwordResetEmailSent)
            assertThat(it.passwordResetKey, notNullValue())
            assertThat(it.passwordResetKey, not(equalTo(oldKey)))
        }
    }

    @Test
    fun `test enabling user with activation key`() {
        tGiven {
            // a user with an activation key
            User(
                username = "foo",
                firstName = "f",
                lastName = "oo",
                plainTextPassword = "1234",
                email = "foo@elaastic.org"
            ).addRole(roleService.roleStudent()).let {
                userService.addUser(it, "fr", true).let { user ->
                    assertFalse(user.enabled)
                }
                it
            }
        }.tWhen {
            // enabling the user with its activation key
            val activationKeyValue = activationKeyRepository.findByUser(it)!!.activationKey
            userService.enableUserWithActivationKey(activationKeyValue).tThen { user ->
                // the user is enabled
                assertThat(user, equalTo(it))
                assertTrue(user!!.enabled)
                // and the activation key has been deleted
                activationKeyRepository.findByUser(user).let { activationKey ->
                    assertThat(activationKey, nullValue())
                }
                user
            }
        }.tWhen {
            // trying enabling a user with a bad key
            userService.enableUserWithActivationKey("dummy-key").tThen { user ->
                // no user is return
                assertThat(user, nullValue())
            }
        }
    }

    @Test
    fun `test change password user`() {
        tGiven {
            // a user
            integrationTestingService.getAnyUser()
        }.tWhen {
            // changing the password with a correct plain password
            userService.changePasswordForUser(it, "abcd").let { user ->
                entityManager.refresh(user)
            }
            it
        }.tThen {
            assertTrue(passwordEncoder.matches("abcd", it.password))
        }

        tGiven {
            // a user
            integrationTestingService.getAnyUser()
        }.tExpect {
            // exception when changing the password with an incorrect plain password
            assertThrows<ValidationException> {
                userService.changePasswordForUser(it, "abc").let { user ->
                    entityManager.refresh(user)
                }
            }
        }
    }

    @Test
    fun `test change password user with password check`() {
        tGiven {
            // a user with "abcd" password
            integrationTestingService.getAnyUser().let {
                userService.changePasswordForUser(it, "abcd")
            }
        }.tWhen {
            // changing the password with a correct plain password and correct current password
            userService.changePasswordForUserWithCurrentPasswordChecking(it, "abcd", "1234").let { user ->
                entityManager.refresh(user)
            }
            it
        }.tThen {
            assertTrue(passwordEncoder.matches("1234", it.password))
            it
        }.tExpect {
            // exception when changing the password with an correct plain password but bad current password
            assertThrows<SecurityException> {
                userService.changePasswordForUserWithCurrentPasswordChecking(it, "abcd", "5678")
            }
        }
    }

    @Test
    fun `test remove old activation keys`() {
        tGiven {
            // 3 users with old activation keys and with only the first one who is enabled
            listOf(
                User(
                    username = "foo",
                    firstName = "f",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = "foo@elaastic.org"
                ).addRole(roleService.roleStudent()).let {
                    userService.addUser(it, "fr", true).let { user ->
                        user.enabled = true
                        userRepository.saveAndFlush(user)
                        assertThat(user.activationKey, notNullValue())
                        assertTrue(user.enabled)
                        user.activationKey!!.dateCreated = DateUtils.addHours(Date(), -4)
                        activationKeyRepository.saveAndFlush(user.activationKey!!)
                        user
                    }
                },
                User(
                    username = "foo2",
                    firstName = "f2",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = "foo2@elaastic.org"
                ).addRole(roleService.roleStudent()).let {
                    userService.addUser(it, "fr", true).let { user ->
                        assertFalse(user.enabled)
                        assertThat(user.activationKey, notNullValue())
                        user.activationKey!!.dateCreated = DateUtils.addHours(Date(), -4)
                        activationKeyRepository.saveAndFlush(user.activationKey!!)
                        user
                    }
                },
                User(
                    username = "foo3",
                    firstName = "f3",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = "foo3@elaastic.org"
                ).addRole(roleService.roleStudent()).let {
                    userService.addUser(it, "fr", true).let { user ->
                        assertFalse(user.enabled)
                        assertThat(user.activationKey, notNullValue())
                        user.activationKey!!.dateCreated = DateUtils.addHours(Date(), -4)
                        activationKeyRepository.saveAndFlush(user.activationKey!!)
                        user
                    }
                }
            )
        }.tWhen {
            // triggering the deletion of old activation keys
            userService.removeOldActivationKeys()
            it
        }.tThen {
            it.forEach { user -> // all activation keys are deleted
                assertThat(activationKeyRepository.findByUser(user), nullValue())
            }
            it.filter {// for enabled users
                it.enabled
            }.forEach { user -> // user and settings are still there
                assertThat(userRepository.getReferenceById(user.id!!), notNullValue())
                assertThat(settingsRepository.findByUser(user), notNullValue())
                assertThat(unsubscribeKeyRepository.findByUser(user), notNullValue())
            }
            it.filter { // for non enabled user
                !it.enabled
            }.forEach { user -> // user and settings are no more present
                assertThat(userRepository.findByIdOrNull(user.id!!), nullValue())
                assertThat(settingsRepository.findByIdOrNull(user.settings!!.id), nullValue())
                assertThat(unsubscribeKeyRepository.findByIdOrNull(user.unsubscribeKey!!.id), nullValue())
            }
        }
    }

    @Test
    fun `test remove old password reset keys`() {
        tGiven {
            // 3 users with the last one only with a password reset key "alive"
            listOf(
                integrationTestingService.getAnyUser().let {
                    userService.generatePasswordResetKeyForUser(it).let { passwordResetKey ->
                        passwordResetKey.dateCreated = DateUtils.addHours(Date(), -2)
                        passwordResetKeyRepository.saveAndFlush(passwordResetKey)
                    }
                    it
                },
                integrationTestingService.getTestStudent().let {
                    userService.generatePasswordResetKeyForUser(it).let { passwordResetKey ->
                        passwordResetKey.dateCreated = DateUtils.addHours(Date(), -2)
                        passwordResetKeyRepository.saveAndFlush(passwordResetKey)
                    }
                    it
                },
                integrationTestingService.getTestTeacher().let {
                    userService.generatePasswordResetKeyForUser(it)
                    it
                }
            )
        }.tWhen {
            // removing old password keys
            userService.removeOldPasswordResetKeys()
        }.tThen {
            // it remains only the last user key
            assertThat(passwordResetKeyRepository.findByUser(integrationTestingService.getAnyUser()), nullValue())
            assertThat(passwordResetKeyRepository.findByUser(integrationTestingService.getTestStudent()), nullValue())
            assertThat(
                passwordResetKeyRepository.findByUser(integrationTestingService.getTestTeacher()),
                notNullValue()
            )
        }
    }

    @Test
    fun `test save user with role change`() {
        tGiven {
            // a teacher
            integrationTestingService.getTestTeacher().let {
                assertTrue(it.isTeacher())
                it
            }
        }.tWhen {
            // changing the main role in student
            it.replaceRolesWithMainRole(roleService.roleForName(Role.RoleId.STUDENT.roleName, true))
            // and saving the user
            userService.saveUser(it, it)
        }.tThen {
            entityManager.refresh(it)
            assertTrue(it.isLearner())
        }
    }

    @Test
    fun `test disable user`() {
        tGiven {
            // a user
            integrationTestingService.getTestTeacher().let {
                assertTrue(it.enabled)
                it
            }
        }.tWhen {
            // triggering disable action
            userService.disableUser(it)
        }.tThen {
            // user is disabled
            entityManager.refresh(it)
            assertFalse(it.enabled)
        }
    }

    @Test
    fun testAddUserConsentToActiveTerms() {
        tGiven {
            // a user without consent to active terms
            integrationTestingService.getAnyUser().let {
                assertFalse(userService.userHasGivenConsentToActiveTerms(it.username))
                it
            }.tWhen {
                // when consent is given and stored
                userService.addUserConsentToActiveTerms(it.username)
            }.tThen {
                // now user has given consent
                assertTrue(userService.userHasGivenConsentToActiveTerms(it))
            }

        }
    }

    @Test
    fun testFakeUserListInitialization() {
        tWhen("Accessing fake user list") {
            userService.fakeUserList
        }.tThen {
            assertThat(it!!.size, equalTo(9))
            for (i in 0..8) {
                assertThat(it[i].username, equalTo("${userService.FAKE_USER_PREFIX}${i + 1}"))
            }
        }
    }


    @Test
    fun `test replace accent`() {
        tWhen {
            userService.replaceAccent("aébècàdêfïg")
        }.tExpect {
            assertThat(it, equalTo("aebecadefig"))
        }
    }

    @Test
    fun `test generate username`() {

        tWhen {
            // "I want to generate a username when there is not already the same username in the database"
            userService.generateUsername("John", "Dorel")
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
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo2"))
        }.tWhen {
            // the username exists with numerical suffix
            User("John", "Dolores15", "johdolo19", "passwd", "joh@doe15.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with litteral suffix
            User("John", "Dolores16", "johdoloabcd", "passwd", "joh@doe16.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with multiple sequences of digit suffix
            User("John", "Dolores16", "johdolo25ab29", "passwd", "joh@doe17.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }.tWhen {
            // the username exists with multiple sequences of digit suffix, the first is smaller than another username numeric suffix
            User("John", "Dolores16", "johdolo18ab29", "passwd", "joh@doe18.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertThat(it, equalTo("johdolo20"))
        }
    }

    @Test
    fun `test generate username with very short name`() {
        tWhen {
            // the username exists with very short name
            userService.generateUsername("Jo", "Do")
        }.tThen {
            assertThat(it, equalTo("jodo"))
        }.tWhen {
            // a user with quadrigramm already used exists
            User("John", "Dolores", "jodo9", "passwd", "joh@doe19.com").let {
                it.addRole(roleService.roleStudent())
            }.let {
                userService.addUser(it)
            }
            userService.generateUsername("Jo", "Do")
        }.tThen {
            assertThat(it, equalTo("jodo10"))
        }
    }

    @Test
    fun `test generate username with accents`() {
        tWhen {
            // I generate a username with firsname and lastname with  accents
            userService.generateUsername("Jérémie", "DÖrèl")
        }.tThen {
            assertThat(it, equalTo("jerdore"))
        }
    }

    @Test
    fun `test generate username with apostrophe`() {
        tWhen {
            // I generate a username with firsname and lastname with  apostrophe
            userService.generateUsername("Pap'", "N'Diaye")
        }.tThen {
            assertThat(it, equalTo("papndia"))
        }.tWhen {
            // I generate a username with firsname and lastname with  apostrophe but very short
            userService.generateUsername("P'", "N'")
        }.tThen {
            assertThat(it, equalTo("pn"))
        }
    }

    @Test
    fun `test generate username with spaces in firstname or lastname`() {
        tWhen {
            // I generate a username with firsname and lastname with  accents
            userService.generateUsername("El Medie", "Ma Patrick")
        }.tThen {
            assertThat(it, equalTo("elmmapa"))
        }
    }

    @Test
    fun `test find more recent username starting wit a given username`() {
        tWhen {
            userService.findMostRecentUsernameStartingWithUsername("John_Doe___")
        }.tExpect {
            assertThat(it, equalTo("John_Doe___9"))
        }
        tWhen {
            userService.findMostRecentUsernameStartingWithUsername("NoUsername___")
        }.tExpect {
            assertThat(it, nullValue())
        }
    }

    @Test
    fun `an elaastic user must have an email`() {
        assertThrows<ValidationException> {
            userService.addUser(
                User(
                    username = "foo",
                    firstName = "f",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = null
                ).addRole(roleService.roleStudent())
            )
        }
    }

    @Test
    fun `a user from an external source may have no email`() {
        tWhen {
            // adding a user
            userService.addUser(
                User(
                    username = "foo",
                    firstName = "f",
                    lastName = "oo",
                    plainTextPassword = "1234",
                    email = "foo@elaastic.org",
                    source = UserSource.CAS
                ).addRole(roleService.roleStudent())
            )
        }.tThen {
            assertThat(it.id, notNullValue())
            it
        }
    }

    @Test
    fun `test of findById`() {
        lateinit var user: User
        tGiven("a user") {
            user = integrationTestingService.getAnyUser()
        }.tWhen("we find the user by id") {
            userService.findById(user.id!!)
        }.tThen("The user is found") {
            assertNotNull(it)
            assertEquals(it, user)
        }

        tGiven("a unknow Id") {
            365843L
        }.tWhen("find the user by id") {
            { userService.findById(it) }
        }.tThen("An exception is throws") {
            assertThrows(IllegalArgumentException::class.java) {
                it()
            }
        }
    }
}
