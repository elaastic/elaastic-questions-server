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
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
class ElaasticOidcUserServiceIntegrationTest(
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
            elaasticOidcUserService.loadUser(getUserRequest(user, RoleId.STUDENT))

        }.tThen("the user is created") { elaasticOidcUser ->
            verify(userLinkService, times(1)).registerNewOidcUser(any<OidcUser>(), any<RoleId>())
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
                userLinkService.oidcProvider,
                elaasticOidcUser.name
            ).let {
                assertNotNull(it)
                it!!
            }
            assertEquals(createdUser, createdUserLink.user) { "User should be the same" }
            assertEquals(
                userLinkService.oidcProvider,
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
            providerId = userLinkService.oidcProvider,
            providerUserId = user.username,
            user = user
        ).let(userLinkRepository::save)

        clearInvocations(userLinkRepository, userRepository)
        tWhen("we load the user") {
            val studentRole = RoleId.STUDENT
            assertTrue(user.roles.contains(studentRole))
            elaasticOidcUserService.loadUser(getUserRequest(user, studentRole))
        }.tThen("no other user is created") { elaasticOidcUser ->
            verify(userRepository, never()).save(any<User>())
            verify(userLinkRepository, never()).save(any<UserLink>())
            verify(userLinkRepository, times(1)).findByProviderIdAndProviderUserId(
                userLinkService.oidcProvider,
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
            ).also {
                it.roles.clear()
                it.roles.add(Role(name = RoleId.STUDENT.roleName))
            }
        }.tWhen("we load the user") {
            elaasticOidcUserService.loadUser(getUserRequest(it))
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
            elaasticOidcUserService.loadUser(getUserRequest(it, RoleId.TEACHER))
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
            elaasticOidcUserService.loadUser(getUserRequest(it, RoleId.ADMIN))
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
            ).also {
                it.roles.clear()
                it.roles.add(Role(name = RoleId.STUDENT.roleName))
                it.roles.add(Role(name = RoleId.TEACHER.roleName))
            }
        }.tWhen("we load the user") {
            {
                elaasticOidcUserService.loadUser(getUserRequest(it))
            }
        }.tThen("an exception is thrown") {
            assertThrows<IllegalStateException> {
                it()
            }
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
            ).also {
                it.roles.clear()
                it.roles.add(Role(name = "UNKNOWN_ROLE"))
            }
        }.tWhen("we load the user") {
            {
                elaasticOidcUserService.loadUser(getUserRequest(it))
            }
        }.tThen("an exception is thrown") {
            assertThrows<IllegalStateException> {
                it()
            }

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
            ).also {
                it.roles.clear()
                it.roles.add(Role(name = RoleId.STUDENT.roleName))
                it.roles.add(Role(name = "UNKNOWN_ROLE"))
            }
        }.tWhen("we load the user") {
            elaasticOidcUserService.loadUser(getUserRequest(it))
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
                providerId = userLinkService.oidcProvider,
                providerUserId = user.username,
                user = user
            ).let(userLinkRepository::save)
        }.tWhen("we load the user BUT with a different role") {
            val anotherRole = RoleId.TEACHER
            assertFalse(it.user hasRole anotherRole);
            { elaasticOidcUserService.loadUser(getUserRequest(it.user, anotherRole)) }
        }.tThen("an exception is thrown") {
            assertThrows<IllegalStateException> {
                it()
            }
        }
    }

    @Test
    fun `test loadUser with existing User and the Role match`() {
        val studentRole = RoleId.STUDENT
        tGiven("a user with STUDENT Role") {
            val user = integrationTestingService.getTestStudent()
            val userLink = UserLink(
                providerId = userLinkService.oidcProvider,
                providerUserId = user.username,
                user = user
            ).let(userLinkRepository::save)
            clearInvocations(userLinkRepository, userRepository)
            userLink
        }.tWhen("we load the user with the same role") {
            elaasticOidcUserService.loadUser(getUserRequest(it.user, studentRole))
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
                providerId = userLinkService.oidcProvider,
                providerUserId = user.username,
                user = user
            ).let(userLinkRepository::save)
            clearInvocations(userLinkRepository, userRepository)
            assertTrue(user.isTeacher())
            userLink
        }.tWhen("we load the user with the same role") {
            elaasticOidcUserService.loadUser(getUserRequest(it.user, teacherRole))
                .let { oidcUser -> oidcUser as ElaasticOidcUser }
        }.tThen("the user is created with the student role") {
            verify(userLinkRepository, never()).save(any<UserLink>())
            verify(userRepository, never()).save(any<User>())
            assertTrue(
                it.elaasticUser hasRole teacherRole,
                "User should have the ${teacherRole.roleName} role, but was ${it.elaasticUser.roles}"
            )
            assertEquals(1, it.elaasticUser.roles.size, "Exactly one role should be present")
        }
    }

    /**
     * Create a UserRequest for the given user
     *
     * The role is determined by the user roles if none, the STUDENT role is
     * used
     */
    private fun getUserRequest(user: User): OidcUserRequest {
        return getUserRequest(
            user,
            user.roles
                .mapNotNull { RoleId.values().find { roleId -> it.equals(roleId) } }
        )
    }

    private fun getUserRequest(user: User, role: RoleId): OidcUserRequest = getUserRequest(user, listOf(role))

    /**
     * Create a UserRequest for the given user and role
     *
     * To see how the role is added, see `oidcIdToken(User, List<Role.RoleId>)`
     *
     * @see oidcIdToken
     */
    private fun getUserRequest(user: User, roles: List<RoleId>): OidcUserRequest = OidcUserRequest(
        clientRegistration(user),
        oAuth2AccessToken(),
        oidcIdToken(user, roles),
        emptyMap()
    )

    private fun oidcIdToken(user: User, roles: List<RoleId>): OidcIdToken {
        val realmRoles = mapOf(
            "roles" to roles.map { it.name.lowercase() }
        )
        return OidcIdToken(
            "idToken", null, null, mapOf(
                "sub" to user.username,
                "given_name" to user.firstName,
                "family_name" to user.lastName,
                "email" to user.email,
                "iss" to "https://localhost:8080",
                "realm_access" to realmRoles,
            )
        )
    }

    private fun oAuth2AccessToken() = OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "accessToken", null, null)

    private fun clientRegistration(user: User): ClientRegistration? =
        ClientRegistration
            .withRegistrationId(user.firstName)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .clientId(user.firstName)
            .tokenUri("https://localhost:8080")
            .build()
}