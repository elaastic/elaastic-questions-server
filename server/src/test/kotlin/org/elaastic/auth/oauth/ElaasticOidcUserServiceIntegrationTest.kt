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

package org.elaastic.auth.oauth

import com.nhaarman.mockitokotlin2.*
import org.elaastic.auth.UserLink
import org.elaastic.auth.UserLinkRepository
import org.elaastic.auth.UserLinkService
import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.user.*
import org.elaastic.user.Role.RoleId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.context.ActiveProfiles
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@ActiveProfiles("oidc")
open class ElaasticOidcUserServiceIntegrationTest(
    @Autowired val elaasticOidcUserService: ElaasticOidcUserService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val roleService: RoleService,
) {
    @SpyBean
    lateinit var userLinkRepository: UserLinkRepository

    @SpyBean
    lateinit var userRepository: UserRepository

    @SpyBean
    lateinit var userLinkService: UserLinkService

    @Test
    fun `test loadUser with null`() {
        assertThrows<IllegalArgumentException> {
            elaasticOidcUserService.loadUser(null)
        }
    }

    @Test
    fun `test loadUser with new User`() {
        // Given a user
        val user = User(
            firstName = "John",
            lastName = "Doe",
            username = "johdoe",
            plainTextPassword = "1234"
        )

        tWhen("we load the user") {
            elaasticOidcUserService.loadUser(createUserRequest(user, RoleId.STUDENT))

        }.tThen("the user is created") { elaasticOidcUser ->
            verify(
                userLinkService,
                times(1)).registerNewExternalUser(
                elaasticOidcUserService.oidcProvider,
                user.username,
                UserCreateCommand(user.firstName, user.lastName, user.email, RoleId.STUDENT, UserSource.OIDC, "fr")
                )

            verify(userRepository, times(1)).save(any<User>())
            verify(userLinkRepository, times(1)).save(any<UserLink>())

            assertInstanceOf(ElaasticOidcUser::class.java, elaasticOidcUser)
            elaasticOidcUser as ElaasticOidcUser
            assertEquals(user.email, elaasticOidcUser.email) { "Email should be the same" }
            assertEquals(user.firstName, elaasticOidcUser.givenName) { "First name should be the same" }
            assertEquals(user.lastName, elaasticOidcUser.familyName) { "Last name should be the same" }

            val createdUser = userRepository.findById(elaasticOidcUser.elaasticUser.id!!)
                .let {
                    assertTrue(it.isPresent) { "User should be present" }
                    it.get()
                }
            assertEquals(user.firstName, createdUser.firstName) { "First name should be the same" }
            assertEquals(user.lastName, createdUser.lastName) { "Last name should be the same" }
            assertEquals(user.username, createdUser.username) { "Username should be the same" }
            assertEquals(user.email, createdUser.email) { "Email should be the same" }

            val createdUserLink = userLinkRepository.findByProviderIdAndProviderUserId(
                elaasticOidcUserService.oidcProvider,
                elaasticOidcUser.name
            ).let {
                assertNotNull(it)
                it!!
            }
            assertEquals(createdUser, createdUserLink.user) { "User should be the same" }
            assertEquals(
                elaasticOidcUserService.oidcProvider,
                createdUserLink.providerId
            ) { "Provider id should be the same" }
            assertEquals(
                elaasticOidcUser.name,
                createdUserLink.providerUserId
            ) { "Provider user id should be the same" }
        }
    }

    @Test
    fun `test loadUser with existing User`() {
        // Given a user
        val user = User(
            firstName = "John",
            lastName = "Doe",
            username = "johdoe",
            plainTextPassword = "1234",
            email = "john.doe@mail.com",
        ).also {
            it.roles.clear()
            it.roles.add(Role(name = RoleId.STUDENT.roleName))
            it.password = "1234"
        }.let(userRepository::save)
        UserLink(
            providerId = elaasticOidcUserService.oidcProvider,
            providerUserId = user.username,
            user = user
        ).let(userLinkRepository::save)

        clearInvocations(userLinkRepository, userRepository)
        tWhen("we load the user") {
            val studentRole = RoleId.STUDENT
            assertTrue(user.roles.contains(studentRole))
            elaasticOidcUserService.loadUser(createUserRequest(user, studentRole))
        }.tThen("no other user is created") { elaasticOidcUser ->
            verify(userRepository, never()).save(any<User>())
            verify(userLinkRepository, never()).save(any<UserLink>())
            verify(userLinkRepository, times(1)).findByProviderIdAndProviderUserId(
                elaasticOidcUserService.oidcProvider,
                user.username
            )
            // And the user retrieve have the information we except
            assertEquals(user.email, elaasticOidcUser.email) { "Email should be the same" }
            assertEquals(user.firstName, elaasticOidcUser.givenName) { "First name should be the same" }
            assertEquals(user.lastName, elaasticOidcUser.familyName) { "Last name should be the same" }
            assertInstanceOf(ElaasticOidcUser::class.java, elaasticOidcUser)
            elaasticOidcUser as ElaasticOidcUser
            assertEquals(user, elaasticOidcUser.elaasticUser) { "User should be the same" }
        }
    }

    @Test
    fun `test loadUser with new User and Student Role`() {
        tGiven("a user with the STUDENT role") {
            User(
                firstName = "John",
                lastName = "Doe",
                username = "johdoe",
                plainTextPassword = "1234"
            )
        }.tWhen("we load the user") {
            elaasticOidcUserService.loadUser(createUserRequest(it, RoleId.STUDENT))
                .let { oidcUser -> oidcUser as ElaasticOidcUser }
        }.tThen("the user is created with the student role") {
            assertTrue(
                it.elaasticUser hasRole RoleId.STUDENT,
                "User should have the STUDENT role, but was ${it.elaasticUser.roles}"
            )
            assertEquals(1, it.elaasticUser.roles.size, "Exactly one role should be present")
        }
    }

    @Test
    fun `test loadUser with new User and Teacher Role`() {
        tGiven("a user with the TEACHER role") {
            User(
                firstName = "John",
                lastName = "Doe",
                username = "johdoe",
                plainTextPassword = "1234"
            )
        }.tWhen("we load the user") {
            elaasticOidcUserService.loadUser(createUserRequest(it, RoleId.TEACHER))
                .let { oidcUser -> oidcUser as ElaasticOidcUser }
        }.tThen("the user is created with the teacher role") {
            assertTrue(
                it.elaasticUser hasRole RoleId.TEACHER,
                "User should have the TEACHER role, but was ${it.elaasticUser.roles}"
            )
            assertEquals(1, it.elaasticUser.roles.size, "Exactly one role should be present")
        }
    }

    @Test
    fun `test loadUser with new User and Admin Role`() {
        tGiven("a user with the ADMIN role") {
            User(
                firstName = "John",
                lastName = "Doe",
                username = "johdoe",
                plainTextPassword = "1234"
            )
        }.tWhen("we load the user") {
            elaasticOidcUserService.loadUser(createUserRequest(it, RoleId.ADMIN))
                .let { oidcUser -> oidcUser as ElaasticOidcUser }
        }.tThen("the user is created with the admin role") {
            assertTrue(
                it.elaasticUser hasRole RoleId.ADMIN,
                "User should have the ADMIN role, but was ${it.elaasticUser.roles}"
            )
            assertEquals(1, it.elaasticUser.roles.size, "Exactly one role should be present")
        }
    }

    @Test
    fun `test loadUser with new User and multiple Roles should throw`() {
        tGiven("a user with the STUDENT and TEACHER roles") {
            User(
                firstName = "John",
                lastName = "Doe",
                username = "johdoe",
                plainTextPassword = "1234"
            )
        }.tWhen("we load the user") {
            {
                elaasticOidcUserService.loadUser(
                    createUserRequest(
                        it,
                        listOf(RoleId.STUDENT, RoleId.TEACHER).map(::findKeycloakRoleFrom)
                    )
                )
            }
        }.tThen("an exception is thrown") {
            val exception = assertThrows<RoleException> {
                it()
            }
            assertNotNull(exception.userRequest)
        }
    }

    @Test
    fun `test loadUser with new User and only an unknow role should throw`() {
        tGiven("a user with an unknown role") {
            User(
                firstName = "John",
                lastName = "Doe",
                username = "johdoe",
                plainTextPassword = "1234"
            )
        }.tWhen("we load the user") {
            {
                elaasticOidcUserService.loadUser(createUserRequest(it, "UNKNOWN_ROLE"))
            }
        }.tThen("an exception is thrown") {
            val exception = assertThrows<RoleException> {
                it()
            }
            assertNotNull(exception.userRequest)
        }
    }

    @Test
    fun `test loadUser with new User and an unknow and one known Role shouldn't throw`() {
        tGiven("a user with an unknown and a known role") {
            User(
                firstName = "John",
                lastName = "Doe",
                username = "johdoe",
                plainTextPassword = "1234"
            )
        }.tWhen("we load the user") {
            elaasticOidcUserService.loadUser(createUserRequest(it, listOf(RoleId.STUDENT.roleName, "UNKNOWN_ROLE")))
                .let { oidcUser -> oidcUser as ElaasticOidcUser }
        }.tThen("the user is created with the student role") {
            assertTrue(
                it.elaasticUser hasRole RoleId.STUDENT,
                "User should have the STUDENT role, instead got ${it.elaasticUser.roles}"
            )
            assertEquals(1, it.elaasticUser.roles.size, "Exactly one role should be present")
        }
    }

    @Test
    fun `test loadUser with existing User and the Role don't match should throw`() {
        tGiven("a user with STUDENT Role") {
            // Given a user
            val user = integrationTestingService.getAnyUser()
                .also {
                    it.roles.clear()
                    it.roles.add(Role(name = RoleId.STUDENT.roleName))
                }
            UserLink(
                providerId = elaasticOidcUserService.oidcProvider,
                providerUserId = user.username,
                user = user
            ).let(userLinkRepository::save)
        }.tWhen("we load the user BUT with a different role") {
            val anotherRole = RoleId.TEACHER
            assertFalse(it.user hasRole anotherRole);
            { elaasticOidcUserService.loadUser(createUserRequest(it.user, anotherRole)) }
        }.tThen("an exception is thrown") {
            val exception = assertThrows<RoleException> {
                it()
            }
            assertNotNull(exception.userRequest)
        }
    }

    @Test
    fun `test loadUser with existing User and the Role match`() {
        val studentRole = RoleId.STUDENT
        tGiven("a user with STUDENT Role") {
            val user = integrationTestingService.getTestStudent()
            val userLink = UserLink(
                providerId = elaasticOidcUserService.oidcProvider,
                providerUserId = user.username,
                user = user
            ).let(userLinkRepository::save)
            clearInvocations(userLinkRepository, userRepository)
            assertTrue(user hasRole studentRole)
            userLink
        }.tWhen("we load the user with the same role") {
            elaasticOidcUserService.loadUser(createUserRequest(it.user, studentRole))
                .let { oidcUser -> oidcUser as ElaasticOidcUser }
        }.tThen("the user is created with the student role") {
            verify(userLinkRepository, never()).save(any<UserLink>())
            verify(userRepository, never()).save(any<User>())
            assertTrue(
                it.elaasticUser hasRole studentRole,
                "User should have the ${studentRole.roleName} role, but was ${it.elaasticUser.roles}"
            )
            assertEquals(1, it.elaasticUser.roles.size, "Exactly one role should be present")
        }

        val teacherRole = RoleId.TEACHER
        tGiven("a user with TEACHER Role") {
            val user = integrationTestingService.getTestTeacher()
            val userLink = UserLink(
                providerId = elaasticOidcUserService.oidcProvider,
                providerUserId = user.username,
                user = user
            ).let(userLinkRepository::save)
            clearInvocations(userLinkRepository, userRepository)
            assertTrue(user.isTeacher())
            userLink
        }.tWhen("we load the user with the same role") {
            elaasticOidcUserService.loadUser(createUserRequest(it.user, teacherRole))
                .let { oidcUser -> oidcUser as ElaasticOidcUser }
        }.tThen("the user is created with the teacher role") {
            verify(userLinkRepository, never()).save(any<UserLink>())
            verify(userRepository, never()).save(any<User>())
            assertTrue(
                it.elaasticUser hasRole teacherRole,
                "User should have the ${teacherRole.roleName} role, but was ${it.elaasticUser.roles}"
            )
            assertEquals(1, it.elaasticUser.roles.size, "Exactly one role should be present")
        }
    }
}