package org.elaastic.auth.oauth

import org.elaastic.auth.UserLink
import org.elaastic.auth.UserLinkRepository
import org.elaastic.test.IntegrationTestingService
import org.elaastic.user.RoleService
import org.elaastic.user.User
import org.elaastic.user.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
class ElaasticOidcUserServiceIntegrationTest(
    @Autowired val elaasticOidcUserService: ElaasticOidcUserService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userLinkRepository: UserLinkRepository,
    @Autowired val roleService: RoleService,
    @Autowired val userRepository: UserRepository,
) {

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
        val userCountBefore = userRepository.count()
        val userLinkCountBefore = userLinkRepository.count()


        // When we load the user
        val elaasticOidcUser = elaasticOidcUserService.loadUser(getUserRequest(user))

        // Then the user is created
        assertEquals(userCountBefore + 1, userRepository.count()) { "One user should be created" }
        assertEquals(userLinkCountBefore + 1, userLinkRepository.count()) { "On userLink should be created" }
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
            elaasticOidcUser.idToken.tokenValue,
            elaasticOidcUser.name
        ).let {
            assertNotNull(it)
            it!!
        }
        assertEquals(createdUser, createdUserLink.user) { "User should be the same" }
        assertEquals(
            elaasticOidcUser.idToken.tokenValue,
            createdUserLink.providerId
        ) { "Provider id should be the same" }
        assertEquals(elaasticOidcUser.name, createdUserLink.providerUserId) { "Provider user id should be the same" }
    }

    @Test
    fun `test loadUser with existing User`() {
        // Given a user
        val user = integrationTestingService.getAnyUser()
        val userRequest = getUserRequest(user)
        val userLink = UserLink(
            providerId = userRequest.idToken.tokenValue,
            providerUserId = user.username,
            user = user
        ).let(userLinkRepository::save)

        val userCountBefore = userRepository.count()
        val userLinkCountBefore = userLinkRepository.count()

        // When we load the user
        val elaasticOidcUser = elaasticOidcUserService.loadUser(userRequest)

        // Then the user is not created
        assertEquals(userCountBefore, userRepository.count()) { "No new user should be created" }
        assertEquals(userLinkCountBefore, userLinkRepository.count()) { "No new userLink should be created" }
        assertInstanceOf(ElaasticOidcUser::class.java, elaasticOidcUser)
        elaasticOidcUser as ElaasticOidcUser
        assertEquals(user.email, elaasticOidcUser.email) { "Email should be the same" }
        assertEquals(user.firstName, elaasticOidcUser.givenName) { "First name should be the same" }
        assertEquals(user.lastName, elaasticOidcUser.familyName) { "Last name should be the same" }
        assertEquals(user, elaasticOidcUser.elaasticUser) { "User should be the same" }
    }

    /** Create a UserRequest for the given user */
    private fun getUserRequest(user: User): OidcUserRequest {
        return OidcUserRequest(
            ClientRegistration
                .withRegistrationId(user.firstName)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientId(user.firstName)
                .tokenUri("https://localhost:8080")
                .build(),
            OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "accessToken", null, null),
            OidcIdToken(
                "idToken", null, null, mapOf(
                    "sub" to user.username,
                    "given_name" to user.firstName,
                    "family_name" to user.lastName,
                    "email" to user.email,
                    "iss" to "https://localhost:8080",
                )
            ),
            emptyMap()
        )
    }
}