package org.elaastic.questions.directory

import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import javax.persistence.EntityManager
import javax.validation.ConstraintViolationException


/**
 * @author John Tranier
 */
@SpringBootTest
@Transactional
@EnableJpaAuditing
internal class UserServiceIntegrationTest(
        @Autowired val userService: UserService,
        @Autowired val roleService: RoleService,
        @Autowired val settingsRepository: SettingsRepository,
        @Autowired val testingService: TestingService,
        @Autowired val entityManager: EntityManager,
        @Autowired val activationKeyRepository: ActivationKeyRepository,
        @Autowired val unsubscribeKeyRepository: UnsubscribeKeyRepository
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
            assertThat(activationKeyRepository.findByUser(it), notNullValue())
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
}
