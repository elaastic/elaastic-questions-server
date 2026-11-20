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

import org.elaastic.test.IntegrationTestingService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("oidc")
@Transactional
class OidcWebSecurityIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val integrationTestingService: IntegrationTestingService,
) {
    @Test
    fun `OIDC login page exposes provider button`() {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("elaasticOidcEnabled", true))
    }

    @Test
    fun `OIDC authorization endpoint starts authorization code flow`() {
        mockMvc.perform(get("/oauth2/authorization/keycloak"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string("Location", org.hamcrest.Matchers.startsWith("http://localhost/authorize?")))
    }

    @Test
    fun `home resolves Elaastic user from OIDC principal`() {
        val user = integrationTestingService.getTestTeacher()
        val token = OidcIdToken("test-token", null, null, mapOf("sub" to "external-id"))
        val principal = ElaasticOidcUser(DefaultOidcUser(emptyList(), token), user)
        mockMvc.perform(get("/home").with(oidcLogin().oidcUser(principal)))
            .andExpect(status().isOk)
            .andExpect(forwardedUrl("/course/index"))
    }

    @Test
    fun `role error page does not authenticate a rejected user`() {
        mockMvc.perform(get(ERROR_ROLE_URL))
            .andExpect(status().isOk)
            .andExpect(view().name("error/oidc_role"))
        mockMvc.perform(get("/home"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrlPattern("**/login"))
    }

    @Test
    fun `logout remains available without authentication`() {
        mockMvc.perform(get("/logout"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/"))
    }
}
