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

package org.elaastic.auth.oauth

import org.assertj.core.api.Assertions.assertThat
import org.elaastic.auth.UserLinkService
import org.elaastic.security.WebSecurityConfig
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.runner.WebApplicationContextRunner
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.FilterChainProxy
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockFilterChain
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

class OidcConfigurationTest {
    private val contextRunner = WebApplicationContextRunner()
        .withUserConfiguration(
            MvcConfiguration::class.java,
            WebSecurityConfig::class.java,
            OidcConfiguration::class.java,
            ElaasticOidcUserService::class.java,
            OidcLoginSuccessHandler::class.java,
        )
        .withBean(UserLinkService::class.java, { mock(UserLinkService::class.java) })
        .withBean(UserDetailsService::class.java, { mock(UserDetailsService::class.java) })
        .withBean(PasswordEncoder::class.java, { BCryptPasswordEncoder() })
        .withPropertyValues("elaastic.questions.url=http://localhost:8080/")

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    class MvcConfiguration

    private val providerSettings = arrayOf(
        "spring.security.oauth2.client.registration.keycloak.client-id=elaastic",
        "spring.security.oauth2.client.registration.keycloak.client-secret=secret",
        "spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code",
        "spring.security.oauth2.client.registration.keycloak.scope=openid",
        "spring.security.oauth2.client.registration.keycloak.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}",
        "spring.security.oauth2.client.provider.keycloak.authorization-uri=https://iam.example/authorize",
        "spring.security.oauth2.client.provider.keycloak.token-uri=https://iam.example/token",
        "spring.security.oauth2.client.provider.keycloak.jwk-set-uri=https://iam.example/jwks",
        "spring.security.oauth2.client.provider.keycloak.user-info-uri=https://iam.example/userinfo",
        "spring.security.oauth2.client.provider.keycloak.user-name-attribute=sub",
    )

    @Test
    fun `OIDC is disabled by default and needs no provider configuration`() {
        contextRunner.run { context ->
            assertThat(context).hasNotFailed()
            assertThat(context).doesNotHaveBean(ClientRegistrationRepository::class.java)
            assertThat(context).doesNotHaveBean(ElaasticOidcUserService::class.java)
            assertThat(context).doesNotHaveBean(OidcLoginSuccessHandler::class.java)
            val security = context.getBean(FilterChainProxy::class.java)
            assertThat(request(security, "/oauth2/authorization/keycloak").redirectedUrl)
                .isEqualTo("http://localhost/login")
            assertThat(request(security, "/login/oauth2/code/keycloak").redirectedUrl)
                .isEqualTo("http://localhost/login")
            assertThat(request(security, "/home", "oidc_hint=keycloak").redirectedUrl)
                .isEqualTo("http://localhost/login")
            assertThat(request(security, "/logout").redirectedUrl).isEqualTo("/")
        }
    }

    @Test
    fun `explicit disable ignores an unreachable issuer even with the oidc profile`() {
        contextRunner
            .withInitializer { it.environment.setActiveProfiles("oidc") }
            .withPropertyValues(*providerSettings)
            .withPropertyValues(
                "elaastic.oidc.enabled=false",
                "spring.security.oauth2.client.provider.keycloak.issuer-uri=http://127.0.0.1:1/unreachable",
            ).run { context ->
                assertThat(context).hasNotFailed()
                assertThat(context).doesNotHaveBean(ClientRegistrationRepository::class.java)
                assertThat(context).doesNotHaveBean(ElaasticOidcUserService::class.java)
            }
    }

    @Test
    fun `OIDC can be enabled without a Spring profile`() {
        contextRunner.withPropertyValues("elaastic.oidc.enabled=true", *providerSettings).run { context ->
            assertThat(context).hasNotFailed()
            assertThat(context).hasSingleBean(ClientRegistrationRepository::class.java)
            assertThat(context).hasSingleBean(ElaasticOidcUserService::class.java)
            val security = context.getBean(FilterChainProxy::class.java)
            assertThat(request(security, "/oauth2/authorization/keycloak").redirectedUrl)
                .startsWith("https://iam.example/authorize?")
            assertThat(request(security, "/home", "oidc_hint=keycloak").redirectedUrl)
                .isEqualTo("/oauth2/authorization/keycloak")
            assertThat(request(security, "/logout").redirectedUrl).isEqualTo("/")
        }
    }

    @Test
    fun `enabled OIDC fails at startup without provider configuration`() {
        contextRunner.withPropertyValues("elaastic.oidc.enabled=true").run { context ->
            assertThat(context).hasFailed()
        }
    }

    private fun request(security: FilterChainProxy, path: String, query: String? = null): MockHttpServletResponse {
        val request = MockHttpServletRequest("GET", path).apply {
            servletPath = path
            if (query != null) {
                queryString = query
                addParameter("oidc_hint", "keycloak")
            }
        }
        return MockHttpServletResponse().also { security.doFilter(request, it, MockFilterChain()) }
    }
}
