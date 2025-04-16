package org.elaastic.auth.oauth

import org.elaastic.user.Role.RoleId
import org.elaastic.user.User
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.oidc.OidcIdToken

fun getUserRequest(user: User, role: String): OidcUserRequest = getUserRequest(user, listOf(role))

fun getUserRequest(user: User, role: RoleId): OidcUserRequest =
    getUserRequest(user, listOf(getKeycloakRoleFrom(role)))

/**
 * Create a UserRequest for the given user and role
 *
 * To see how the role is added, see `oidcIdToken(User, List<Role.RoleId>)`
 *
 * @see oidcIdToken
 */
fun getUserRequest(user: User, roles: List<String>): OidcUserRequest = OidcUserRequest(
    clientRegistration(user),
    oAuth2AccessToken(),
    oidcIdToken(user, roles),
    emptyMap()
)

private fun oidcIdToken(user: User, roles: List<String>): OidcIdToken {
    return OidcIdToken(
        "idToken",
        null,
        null,
        mapOf(
            "sub" to user.username,
            "given_name" to user.firstName,
            "family_name" to user.lastName,
            "email" to user.email,
            "iss" to "https://localhost:8080",
            "realm_access" to mapOf(
                "roles" to roles.map(::getKeycloakRoleFrom)
            ),
        )
    )
}

/** Get the Keycloak role from the given RoleId */
fun getKeycloakRoleFrom(role: RoleId): String {
    return keycloakToElaasticRole.entries.find { it.value == role }?.key
        ?: throw RoleException("Role $role not found")
}

/**
 * Get the Keycloak role from the given role name.
 *
 * If the role name is not found, it will return the role name itself.
 */
fun getKeycloakRoleFrom(role: String): String {
    return keycloakToElaasticRole.entries.find { it.value.roleName == role }?.key
        ?: role
}

private fun oAuth2AccessToken() = OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "accessToken", null, null)

private fun clientRegistration(user: User): ClientRegistration? =
    ClientRegistration
        .withRegistrationId(user.firstName)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .clientId(user.firstName)
        .tokenUri("https://localhost:8080")
        .build()