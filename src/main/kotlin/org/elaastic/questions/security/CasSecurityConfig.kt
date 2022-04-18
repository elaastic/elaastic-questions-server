package org.elaastic.questions.security

import org.jasig.cas.client.validation.Cas30ServiceTicketValidator
import org.jasig.cas.client.validation.TicketValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter
import org.springframework.security.core.userdetails.UserDetailsService

@Configuration
class CasSecurityConfig(
    @Autowired val userDetailsService: UserDetailsService,
    @Value("\${cas.service.loginUrl}") val casServiceLoginUrl:String,
    @Value("\${cas.server.url}") val casServerUrl: String,
    @Value("\${cas.authenticationProvider.key}") val casAuthenticationProviderKey : String,
    @Value("\${elaastic.questions.url}") val elaasticServerUrl: String,
) {
    @Bean
    fun serviceProperties(): ServiceProperties? {
        val serviceProperties = ServiceProperties()
        serviceProperties.service = "$elaasticServerUrl$casServiceLoginUrl"
        serviceProperties.isSendRenew = false
        return serviceProperties
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun casAuthenticationFilter(
        authenticationManager: AuthenticationManager?,
        serviceProperties: ServiceProperties?
    ): CasAuthenticationFilter? {
        val filter = CasAuthenticationFilter()
        filter.setAuthenticationManager(authenticationManager)
        filter.setServiceProperties(serviceProperties)

//        Let's see if we need this to handle dynamic cas
//        filter.setAuthenticationFailureHandler { request, response, exception ->  ... }
        return filter
    }

    @Bean
    fun ticketValidator(): TicketValidator? {
        return Cas30ServiceTicketValidator(casServerUrl)
    }

    @Bean
    fun casAuthenticationProvider(
        ticketValidator: TicketValidator?,
        serviceProperties: ServiceProperties?
    ): CasAuthenticationProvider? {
        val provider = CasAuthenticationProvider()
        provider.setServiceProperties(serviceProperties)
        provider.setTicketValidator(ticketValidator)
        provider.setUserDetailsService(userDetailsService)
        provider.setKey(casAuthenticationProviderKey)
        return provider
    }

    @Bean
    fun casAuthenticationEntryPoint(serviceProperties: ServiceProperties?): CasAuthenticationEntryPoint? {
        val casAuthenticationEntryPoint = CasAuthenticationEntryPoint()
        casAuthenticationEntryPoint.loginUrl = "${casServerUrl}/login"
        casAuthenticationEntryPoint.serviceProperties = serviceProperties
        return casAuthenticationEntryPoint
    }
}