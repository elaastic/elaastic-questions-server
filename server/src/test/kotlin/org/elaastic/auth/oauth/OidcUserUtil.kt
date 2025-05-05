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

import org.elaastic.user.Role.RoleId
import org.elaastic.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser

fun createUserRequest(user: User, role: String): OidcUserRequest = createUserRequest(user, listOf(role))

fun createUserRequest(user: User, role: RoleId): OidcUserRequest =
    createUserRequest(user, listOf(findKeycloakRoleFrom(role)))

/**
 * Create a UserRequest for the given user and role
 *
 * To see how the role is added, see `oidcIdToken(User, List<Role.RoleId>)`
 *
 * @see createOidcIdToken
 */
fun createUserRequest(user: User, roles: List<String>): OidcUserRequest = OidcUserRequest(
    buildClientRegistration(user),
    createOAuth2AccessToken(),
    createOidcIdToken(user, roles),
    emptyMap()
)

private fun createOidcIdToken(user: User, roles: List<String>): OidcIdToken {
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
                "roles" to roles.map(::findKeycloakRoleFrom)
            ),
        )
    )
}

/** Get the Keycloak role from the given RoleId */
fun findKeycloakRoleFrom(role: RoleId): String {
    return keycloakToElaasticRole.entries.find { it.value == role }?.key
        ?: throw RoleException("Role $role not found")
}

/**
 * Get the Keycloak role from the given role name.
 *
 * If the role name is not found, it will return the role name itself.
 */
fun findKeycloakRoleFrom(role: String): String {
    return keycloakToElaasticRole.entries.find { it.value.roleName == role }?.key
        ?: role
}

private fun createOAuth2AccessToken() = OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "accessToken", null, null)

private fun buildClientRegistration(user: User): ClientRegistration? =
    ClientRegistration
        .withRegistrationId(user.firstName)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .clientId(user.firstName)
        .tokenUri("https://localhost:8080")
        .build()

private fun createOidcUserInfo(user: User): OidcUserInfo =
    OidcUserInfo.builder()
        .name(user.firstName)
        .familyName(user.lastName)
        .email(user.email)
        .build()

fun createOidcUser(user: User, roles: List<String>): OidcUser {
    val authorities: List<GrantedAuthority> = emptyList()
    return DefaultOidcUser(
        authorities,
        createOidcIdToken(user, roles),
        createOidcUserInfo(user),
    )
}