/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.auth

import org.elaastic.auth.cas.ElaasticUrlLogoutSuccessHandler
import org.elaastic.auth.oauth.ElaasticOidcUser
import org.elaastic.user.User
import org.jasig.cas.client.validation.AssertionImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationToken
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser

class ElaasticLogoutSuccessHandlerTest {
    private val nativeLogout = ElaasticUrlLogoutSuccessHandler(
        "/", mapOf("ENT_1" to "https://cas.example"), "/logout?service=http://localhost:8080/"
    )

    @Test
    fun `CAS logout keeps provider redirect`() {
        val user = User("John", "Doe", "john", "password").also { it.casKey = "ENT_1" }
        val response = MockHttpServletResponse()
        ElaasticLogoutSuccessHandler(nativeLogout).onLogoutSuccess(
            MockHttpServletRequest(), response, UsernamePasswordAuthenticationToken(user, null)
        )
        assertEquals("https://cas.example/logout?service=http://localhost:8080/", response.redirectedUrl)
    }

    @Test
    fun `real CAS token keeps provider logout with OIDC enabled or disabled`() {
        val user = User("John", "Doe", "john", "password").also { it.casKey = "ENT_1" }
        val authentication = CasAuthenticationToken(
            "ENT_1", user, "ticket", user.authorities, user, AssertionImpl("john")
        )
        for (oidcLogout in listOf(null, mock(OidcClientInitiatedLogoutSuccessHandler::class.java))) {
            val response = MockHttpServletResponse()
            ElaasticLogoutSuccessHandler(nativeLogout, oidcLogout).onLogoutSuccess(
                MockHttpServletRequest(), response, authentication
            )
            assertEquals("https://cas.example/logout?service=http://localhost:8080/", response.redirectedUrl)
        }
    }

    @Test
    fun `local logout returns to homepage`() {
        val user = User("John", "Doe", "john", "password")
        val response = MockHttpServletResponse()
        ElaasticLogoutSuccessHandler(nativeLogout).onLogoutSuccess(
            MockHttpServletRequest(), response, UsernamePasswordAuthenticationToken(user, null)
        )
        assertEquals("/", response.redirectedUrl)
    }

    @Test
    fun `OIDC logout sends ID token to provider end session endpoint`() {
        val registration = ClientRegistration.withRegistrationId("keycloak")
            .clientId("elaastic")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:8080/login/oauth2/code/keycloak")
            .authorizationUri("https://iam.example/authorize")
            .tokenUri("https://iam.example/token")
            .providerConfigurationMetadata(mapOf("end_session_endpoint" to "https://iam.example/logout"))
            .build()
        val oidcLogout = OidcClientInitiatedLogoutSuccessHandler(InMemoryClientRegistrationRepository(registration))
        oidcLogout.setPostLogoutRedirectUri("http://localhost:8080/")
        val principal = ElaasticOidcUser(
            DefaultOidcUser(emptyList(), OidcIdToken("test-token", null, null, mapOf("sub" to "external-id"))),
            User("John", "Doe", "john", "password")
        )
        val response = MockHttpServletResponse()
        ElaasticLogoutSuccessHandler(nativeLogout, oidcLogout).onLogoutSuccess(
            MockHttpServletRequest(), response, OAuth2AuthenticationToken(principal, emptyList(), "keycloak")
        )
        assertTrue(response.redirectedUrl!!.startsWith("https://iam.example/logout?"))
        assertTrue(response.redirectedUrl!!.contains("id_token_hint=test-token"))
        assertTrue(response.redirectedUrl!!.contains("post_logout_redirect_uri="))
    }
}
