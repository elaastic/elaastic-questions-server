package org.elaastic.auth.oauth

import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "elaastic.oidc.enabled=false",
        "spring.security.oauth2.client.provider.test-keycloak.issuer-uri=http://127.0.0.1:1/unreachable",
    ]
)
@AutoConfigureMockMvc
@ActiveProfiles("oidc")
class OidcDisabledWebSecurityIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val context: ApplicationContext,
) {
    @Test
    fun `disabled OIDC starts without contacting the issuer and hides the login button`() {
        assertThat(context.getBeansOfType(ClientRegistrationRepository::class.java)).isEmpty()
        assertThat(context.getBeansOfType(ElaasticOidcUserService::class.java)).isEmpty()
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk)
            .andExpect(model().attribute("elaasticOidcEnabled", false))
            .andExpect(content().string(not(containsString("/oauth2/authorization/keycloak"))))
    }

    @Test
    fun `disabled OIDC cannot start a login by URL or hint and local logout still works`() {
        for (path in listOf("/oauth2/authorization/keycloak", "/login/oauth2/code/keycloak")) {
            mockMvc.perform(get(path))
                .andExpect(status().is3xxRedirection)
                .andExpect(redirectedUrlPattern("**/login"))
        }
        mockMvc.perform(get("/home").param("oidc_hint", "keycloak"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrlPattern("**/login"))
        mockMvc.perform(get("/logout"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/"))
    }
}
