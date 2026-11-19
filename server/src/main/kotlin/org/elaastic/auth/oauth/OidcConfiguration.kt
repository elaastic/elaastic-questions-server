package org.elaastic.auth.oauth

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesRegistrationAdapter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository

/** Load the OAuth2 client infrastructure only when OIDC is explicitly enabled. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "elaastic.oidc", name = ["enabled"], havingValue = "true")
@EnableConfigurationProperties(OAuth2ClientProperties::class)
class OidcConfiguration {
    @Bean
    fun clientRegistrationRepository(properties: OAuth2ClientProperties): ClientRegistrationRepository =
        InMemoryClientRegistrationRepository(
            OAuth2ClientPropertiesRegistrationAdapter.getClientRegistrations(properties).values.toList()
        )
}
