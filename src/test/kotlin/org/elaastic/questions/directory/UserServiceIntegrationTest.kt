package org.elaastic.questions.directory

import org.apache.commons.lang3.time.DateUtils
import org.elaastic.questions.test.TestingService
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
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException
import javax.validation.ValidationException


@SpringBootTest
@Transactional
internal class UserServiceIntegrationTest(
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService,
        @Autowired val settingsRepository: SettingsRepository,
        @Autowired val testingService: TestingService,
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
        testingService.getAnyUser().tWhen {
            userService.initializeSettingsForUser(it, "fr")
        }.tThen {
            assertThat(it.user, equalTo(testingService.getAnyUser()))
            assertThat(it.language, equalTo("fr"))
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(0L))
        }

    }

    @Test
    fun `test initialization of unsubscribe key for a given user `() {
        // given a user
        testingService.getAnyUser().tWhen {
            userService.initializeUnsubscribeKeyForUser(it)
        }.tThen {
            assertThat(it.user, equalTo(testingService.getAnyUser()))
            assertThat(it.unsubscribeKey, notNullValue())
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(0L))
        }
    }

    @Test
    fun `test initialization of activation key for a given user `() {
        // given a user
        testingService.getAnyUser().tWhen {
            userService.initializeActivationKeyForUser(it)
        }.tThen {
            assertThat(it.user, equalTo(testingService.getAnyUser()))
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
            userService.findByEmail(it.email!!)
        }.tThen {
            assertThat(it, notNullValue())
            assertThat(it!!.username, equalTo("foo"))
        }.tWhen {
            // triggering research by email with an unknown email
            userService.findByEmail("john@doe.fr")
        }.tThen {
            assertThat(it, nullValue())
        }
    }

    @Test
    fun `test generate new password reset key`() {
        tGiven {
            // a user without password reset key
            testingService.getAnyUser().let {
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
            testingService.getAnyUser().let {
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
            testingService.getAnyUser()
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
            testingService.getAnyUser()
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
            testingService.getAnyUser(). let {
                userService.changePasswordForUser(it, "abcd")
            }
        }.tWhen {
            // changing the password with a correct plain password and correct current password
            userService.changePasswordForUserWithCurrentPasswordChecking(it,"abcd", "1234").let { user ->
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
                            user.activationKey!!.dateCreated = DateUtils.addHours(Date(),-4)
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
                            user.activationKey!!.dateCreated = DateUtils.addHours(Date(),-4)
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
                            user.activationKey!!.dateCreated = DateUtils.addHours(Date(),-4)
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
                assertThat(userRepository.getOne(user.id!!), notNullValue())
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
                    testingService.getAnyUser().let {
                        userService.generatePasswordResetKeyForUser(it).let { passwordResetKey ->
                            passwordResetKey.dateCreated = DateUtils.addHours(Date(), -2)
                            passwordResetKeyRepository.saveAndFlush(passwordResetKey)
                        }
                        it
                    },
                    testingService.getTestStudent().let {
                        userService.generatePasswordResetKeyForUser(it).let { passwordResetKey ->
                            passwordResetKey.dateCreated = DateUtils.addHours(Date(), -2)
                            passwordResetKeyRepository.saveAndFlush(passwordResetKey)
                        }
                        it
                    },
                    testingService.getTestTeacher().let {
                        userService.generatePasswordResetKeyForUser(it)
                        it
                    }
            )
        }.tWhen {
            // removing old password keys
            userService.removeOldPasswordResetKeys()
        }.tThen {
            // it remains only the last user key
            assertThat(passwordResetKeyRepository.findByUser(testingService.getAnyUser()), nullValue())
            assertThat(passwordResetKeyRepository.findByUser(testingService.getTestStudent()), nullValue())
            assertThat(passwordResetKeyRepository.findByUser(testingService.getTestTeacher()), notNullValue())
        }
    }

    @Test
    fun `test save user with role change`() {
        tGiven {
            // a teacher
            testingService.getTestTeacher().let {
                assertTrue(it.isTeacher())
                it
            }
        }.tWhen {
            // changing the main role in student
            it.replaceRolesWithMainRole(roleService.roleForName(Role.RoleId.STUDENT.roleName,true))
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
            testingService.getTestTeacher().let {
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
            testingService.getAnyUser().let {
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
}
