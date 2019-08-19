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
        @Autowired val passwordEncoder: PasswordEncoder
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
            assertTrue(passwordEncoder.matches("abcd",it.password))
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

}
