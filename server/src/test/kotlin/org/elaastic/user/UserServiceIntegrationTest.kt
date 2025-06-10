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

import org.apache.commons.lang3.time.DateUtils
import org.elaastic.assertIsEmpty
import org.elaastic.auth.UserLinkRepository
import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tExpect
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import javax.persistence.EntityManager
import javax.transaction.Transactional
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
    @Autowired val userRepository: UserRepository,
    @Autowired val userLinkRepository: UserLinkRepository,
) {

    @Test
    fun addUserWithoutCheckingOfEmail() {

        tWhen("adding a user") {
            User(
                username = "foo",
                firstName = "f",
                lastName = "oo",
                plainTextPassword = "1234",
                email = "foo@elaastic.org"
            )
                .addRole(roleService.roleStudent())
                .let(userService::addUser)
        }.tThen("the user is created") {
            assertNotNull(it.id)
            assertNotNull(it.password)
            assertNotEquals("1234", it.password)
            assertTrue(it.enabled)
            assertNotNull(it.settings, "settings must be set")

            // and activation is not set and an "unsubscribe key" is set
            assertNull(activationKeyRepository.findByUser(it))
            assertNotNull(unsubscribeKeyRepository.findByUser(it))
            assertTrue(userService.userHasGivenConsentToActiveTerms(it.username))
            it
        }.tWhen("refreshing the user and fetching the settings") {
            entityManager.refresh(it)
            it
        }.tThen {
            assertEquals(it.settings, settingsRepository.findByUser(it))
        }
    }

    @Test
    fun addUserWithCheckingOfEmail() {
        tWhen("adding a user") {
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
            assertNotNull(it.id)
            assertNotNull(it.password)
            assertNotEquals("1234", it.password)
            assertEquals(false, it.enabled)
            assertNotNull(it.settings, "settings must be set")
            // and activation and unsubscribe key are set
            val activationKey = activationKeyRepository.findByUser(it)!!
            assertNotNull(activationKey.dateCreated)
            assertNotNull(unsubscribeKeyRepository.findByUser(it))
            assertTrue(userService.userHasGivenConsentToActiveTerms(it.username))
            it
        }
    }

    @Test
    fun addUserWithError() {
        assertThrows<ConstraintViolationException>(
            "The password is too short (min length is 4), an exception have should be thrown"
        ) {
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

        assertThrows<ConstraintViolationException>(
            "The email is incorrect, an exception have should be thrown"
        ) {
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
        tGiven("a user") {
            integrationTestingService.getAnyUser()
        }.tWhen {
            userService.initializeSettingsForUser(it, "fr")
        }.tThen {
            assertEquals(integrationTestingService.getAnyUser(), it.user)
            assertEquals("fr", it.language)
            assertNotNull(it.id)
            assertEquals(0L, it.version)
        }

    }

    @Test
    fun `test initialization of unsubscribe key for a given user `() {
        tGiven("a user") {
            integrationTestingService.getAnyUser()
        }.tWhen {
            userService.initializeUnsubscribeKeyForUser(it)
        }.tThen {
            assertEquals(integrationTestingService.getAnyUser(), it.user)
            assertNotNull(it.unsubscribeKey)
            assertNotNull(it.id)
            assertEquals(0L, it.version)
        }
    }

    @Test
    fun `test initialization of activation key for a given user `() {
        tGiven("a user") {
            integrationTestingService.getAnyUser()
        }.tWhen {
            userService.initializeActivationKeyForUser(it)
        }.tThen {
            assertEquals(integrationTestingService.getAnyUser(), it.user)
            assertNotNull(it.activationKey)
            assertNotNull(it.id)
            assertEquals(0L, it.version)
            assertNotNull(it.dateCreated)
            assertFalse(it.activationEmailSent)
        }
    }

    @Test
    fun `test find user by email`() {
        tGiven("a user") {
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
        }.tWhen("triggering research by email with the email of the user") {
            userService.findAllByEmail(it.email!!)
        }.tThen {
            assertFalse(it.isEmpty())
            assertEquals("foo", it.last().username)
        }.tWhen("triggering research by email with an unknown email") {
            userService.findAllByEmail("john@doe.fr")
        }.tThen {
            assertTrue(it.isEmpty())
        }
    }

    @Test
    fun `test generate new password reset key`() {
        tGiven("a user without password reset key") {
            integrationTestingService.getAnyUser().let {
                assertNull(passwordResetKeyRepository.findByUser(it))
                it
            }
        }.tWhen("triggering generation of a password reset key") {
            userService.generatePasswordResetKeyForUser(it)
        }.tThen("the password reset key has been generated and saved") {
            assertNotNull(it.id)
            entityManager.refresh(it)
            assertNotNull(it.dateCreated)
            assertFalse(it.passwordResetEmailSent)
            assertNotNull(it.passwordResetKey)
        }

    }


    @Test
    fun `test generate recycled password reset key`() {
        var oldKey: String? = null
        tGiven("a user with an old password reset key") {
            integrationTestingService.getAnyUser().let {
                userService.generatePasswordResetKeyForUser(it).let { prk ->
                    oldKey = prk.passwordResetKey
                    prk.dateCreated = DateUtils.addHours(Date(), -2)
                    prk.passwordResetEmailSent = true
                    passwordResetKeyRepository.saveAndFlush(prk)
                }
                it
            }
        }.tWhen("triggering generation of a password reset key") {
            userService.generatePasswordResetKeyForUser(it)
        }.tThen("the password reset key has been recycled and saved") {
            entityManager.refresh(it)
            assertTrue(it.dateCreated > DateUtils.addHours(Date(), -1))
            assertFalse(it.passwordResetEmailSent)
            assertNotNull(it.passwordResetKey)
            assertNotEquals(oldKey, it.passwordResetKey)
        }
    }

    @Test
    fun `test enabling user with activation key`() {
        tGiven("a user with an activation key") {
            User(
                username = "foo",
                firstName = "f",
                lastName = "oo",
                plainTextPassword = "1234",
                email = "foo@elaastic.org"
            ).addRole(roleService.roleStudent()).let {
                userService.addUser(it, "fr", true).let { user ->
                    assertFalse(user.enabled)
                    user
                }
            }
        }.tWhen("enabling the user with its activation key") {
            val activationKeyValue = activationKeyRepository.findByUser(it)!!.activationKey
            userService.enableUserWithActivationKey(activationKeyValue).tThen("the user is enabled") { user ->
                assertEquals(it, user)
                assertTrue(user!!.enabled)
                // and the activation key has been deleted
                assertNull(activationKeyRepository.findByUser(user))
                user
            }
        }.tWhen("trying enabling a user with a bad key") {
            userService.enableUserWithActivationKey("dummy-key")
        }.tThen("no user is returned") { user ->
            assertNull(user)
        }
    }

    @Test
    fun `test change password user`() {
        tGiven("a user") {
            integrationTestingService.getAnyUser()
        }.tWhen("changing the password with a correct plain password") {
            userService.changePasswordForUser(it, "abcd").let { user ->
                entityManager.refresh(user)
            }
            it
        }.tThen {
            assertTrue(passwordEncoder.matches("abcd", it.password))
        }

        tGiven("a user") {
            integrationTestingService.getAnyUser()
        }.tExpect("exception when changing the password with an incorrect plain password") {
            assertThrows<ValidationException> {
                userService.changePasswordForUser(it, "abc").let { user ->
                    entityManager.refresh(user)
                }
            }
        }
    }

    @Test
    fun `test change password user with password check`() {
        tGiven("a user with \"abcd\" password") {
            integrationTestingService.getAnyUser().let {
                userService.changePasswordForUser(it, "abcd")
            }
        }.tWhen("changing the password with a correct plain password and correct current password") {
            userService.changePasswordForUserWithCurrentPasswordChecking(it, "abcd", "1234").let { user ->
                entityManager.refresh(user)
            }
            it
        }.tThen {
            assertTrue(passwordEncoder.matches("1234", it.password))
            it
        }.tExpect("exception when changing the password with a correct plain password but bad current password") {
            assertThrows<SecurityException> {
                userService.changePasswordForUserWithCurrentPasswordChecking(it, "abcd", "5678")
            }
        }
    }

    @Test
    fun `test remove old activation keys`() {
        tGiven("3 users with old activation keys and with only the first one who is enabled") {
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
                        assertNotNull(user.activationKey)
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
                        assertNotNull(user.activationKey)
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
                        assertNotNull(user.activationKey)
                        user.activationKey!!.dateCreated = DateUtils.addHours(Date(), -4)
                        activationKeyRepository.saveAndFlush(user.activationKey!!)
                        user
                    }
                }
            )
        }.tWhen("triggering the deletion of old activation keys") {
            userService.removeOldActivationKeys()
            it
        }.tThen {
            it.forEach { user -> // all activation keys are deleted
                assertNull(activationKeyRepository.findByUser(user))
            }
            it.filter { user -> // for enabled users
                user.enabled
            }.forEach { user -> // user and settings are still there
                assertNotNull(userRepository.getReferenceById(user.id!!))
                assertNotNull(settingsRepository.findByUser(user))
                assertNotNull(unsubscribeKeyRepository.findByUser(user))
            }
            it.filter { user -> // for non enabled user
                !user.enabled
            }.forEach { user -> // user and settings are no more present
                assertNull(userRepository.findByIdOrNull(user.id!!))
                assertNull(settingsRepository.findByIdOrNull(user.settings!!.id))
                assertNull(unsubscribeKeyRepository.findByIdOrNull(user.unsubscribeKey!!.id))
            }
        }
    }

    @Test
    fun `test remove old password reset keys`() {
        tGiven("3 users with the last one only with a password reset key \"alive\"") {
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
        }.tWhen("removing old password keys") {
            userService.removeOldPasswordResetKeys()
        }.tThen("it remains only the last user key") {
            assertNull(passwordResetKeyRepository.findByUser(integrationTestingService.getAnyUser()))
            assertNull(passwordResetKeyRepository.findByUser(integrationTestingService.getTestStudent()))
            assertNotNull(passwordResetKeyRepository.findByUser(integrationTestingService.getTestTeacher()))
        }
    }

    @Test
    fun `test save user with role change`() {
        tGiven("a teacher") {
            integrationTestingService.getTestTeacher().let {
                assertTrue(it.isTeacher())
                it
            }
        }.tWhen("changing the main role to a student role") {
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
        tGiven("a user") {
            integrationTestingService.getTestTeacher().let {
                assertTrue(it.enabled)
                it
            }
        }.tWhen("triggering disable action") {
            userService.disableUser(it)
        }.tThen("user is disabled") {
            entityManager.refresh(it)
            assertFalse(it.enabled)
        }
    }

    @Test
    fun testAddUserConsentToActiveTerms() {
        tGiven("a user without consent to active terms") {
            integrationTestingService.getAnyUser().let {
                assertFalse(userService.userHasGivenConsentToActiveTerms(it.username))
                it
            }
        }.tWhen("when consent is given and stored") {
            userService.addUserConsentToActiveTerms(it.username)
        }.tThen("now user has given consent") {
            assertTrue(userService.userHasGivenConsentToActiveTerms(it))
        }
    }

    @Test
    fun testFakeUserListInitialization() {
        tWhen("Accessing fake user list") {
            userService.fakeUserList
        }.tThen {
            assertEquals(9, it!!.size)
            for (i in 0..8) {
                assertEquals("${UserService.FAKE_USER_PREFIX}${i + 1}", it[i].username)
            }
        }
    }


    @Test
    fun `test replace accent`() {
        tWhen {
            userService.replaceAccent("aébècàdêfïg")
        }.tExpect {
            assertEquals("aebecadefig", it)
        }
    }

    @Test
    fun `test generate username`() {
        tWhen("I want to generate a username when there is not already the same username in the database.") {
            userService.generateUsername("John", "Dorel")
        }.tThen("I get a username without an index as suffix") {
            assertEquals("johdore", it)
        }
        tWhen("the username exists") {
            User("John", "Dolores", "johdolo", "passwd", "joh@doe.com")
                .addRole(roleService.roleStudent())
                .let(userService::addUser)
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertEquals("johdolo2", it)
        }
        tWhen("the username exists with numerical suffix") {
            User("John", "Dolores15", "johdolo19", "passwd", "joh@doe15.com")
                .addRole(roleService.roleStudent())
                .let(userService::addUser)
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertEquals("johdolo20", it)
        }
        tWhen("the username exists with literal suffix") {
            User("John", "Dolores16", "johdoloabcd", "passwd", "joh@doe16.com")
                .addRole(roleService.roleStudent())
                .let(userService::addUser)
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertEquals("johdolo20", it)
        }
        tWhen("the username exists with multiple sequences of digit suffix") {
            User("John", "Dolores16", "johdolo25ab29", "passwd", "joh@doe17.com")
                .addRole(roleService.roleStudent())
                .let(userService::addUser)
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertEquals("johdolo20", it)
        }
        tWhen("the username exists with multiple sequences of digit suffix, the first is smaller than another username numeric suffix") {
            User("John", "Dolores16", "johdolo18ab29", "passwd", "joh@doe18.com")
                .addRole(roleService.roleStudent())
                .let(userService::addUser)
            userService.generateUsername("John", "Dolorus")
        }.tThen {
            assertEquals("johdolo20", it)
        }
    }

    @Test
    fun `test generate username with very short name`() {
        tWhen("the username exists with a very short name") {
            userService.generateUsername("Jo", "Do")
        }.tThen {
            assertEquals("jodo", it)
        }
        tGiven("a user with quadrigramm already used exists") {
            User("John", "Dolores", "jodo9", "passwd", "joh@doe19.com")
                .addRole(roleService.roleStudent())
                .let(userService::addUser)
        }.tWhen("generating a username with the same quadrigramm") {
            userService.generateUsername("Jo", "Do")
        }.tThen("I get a username with an index as suffix") {
            assertEquals("jodo10", it)
        }
    }

    @Test
    fun `test generate username with accents`() {
        tWhen("I generate a username with firstname and lastname with accents") {
            userService.generateUsername("Jérémie", "DÖrèl")
        }.tThen {
            assertEquals("jerdore", it)
        }
    }

    @Test
    fun `test generate username with apostrophe`() {
        tWhen("I generate a username with firstname and lastname with apostrophe") {
            userService.generateUsername("Pap'", "N'Diaye")
        }.tThen {
            assertEquals("papndia", it)
        }.tWhen("I generate a username with firstname and lastname with apostrophe but very short") {
            userService.generateUsername("P'", "N'")
        }.tThen {
            assertEquals("pn", it)
        }
    }

    @Test
    fun `test generate username with spaces in firstname or lastname`() {
        tWhen("I generate a username with firstname and lastname with accents") {
            userService.generateUsername("El Medie", "Ma Patrick")
        }.tThen {
            assertEquals("elmmapa", it)
        }
    }

    @Test
    fun `test find more recent username starting wit a given username`() {
        tWhen {
            userService.findMostRecentUsernameStartingWithUsername("John_Doe___")
        }.tExpect {
            assertEquals("John_Doe___9", it)
        }
        tWhen {
            userService.findMostRecentUsernameStartingWithUsername("NoUsername___")
        }.tExpect {
            assertNull(it)
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
        tWhen("adding a user") {
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
            assertNotNull(it.id)
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
            assertThrows<IllegalArgumentException> {
                it()
            }
        }
    }

    @Test
    fun `test addUser without role`() {
        tGiven("a user") {
            User(
                username = "foo",
                firstName = "f",
                lastName = "oo",
                plainTextPassword = "1234",
                email = "foo@elaastic.org"
            )
        }.tWhen("The user is added") {
            assertIsEmpty(it.roles);
            {
                userService.addUser(it)
            }
        }.tThen("an exception is thrown") {
            assertThrows<IllegalArgumentException> {
                it()
            }
        }
    }
}
