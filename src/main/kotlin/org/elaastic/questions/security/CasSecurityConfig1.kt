package org.elaastic.questions.security

import org.elaastic.questions.directory.cas.CasAuthenticationUserDetailService
import org.elaastic.questions.directory.cas.CasUserDetailService
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator
import org.jasig.cas.client.validation.TicketValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter

@Configuration
class CasSecurityConfig1(
    @Autowired val casUserDetailService: CasUserDetailService,
    @Value("\${elaastic.questions.url}") val elaasticServerUrl: String,
) {
    private val casKey = "ENT_1"
    private val serviceLoginUrl = "login/cas/1"
    private val casServerUrl = "https://localhost:8443/cas"

    @Bean
    @Throws(java.lang.Exception::class)
    fun casAuthenticationFilter1(
        authenticationManager: AuthenticationManager,
        @Qualifier("serviceProperties1") serviceProperties1: ServiceProperties
    ) = CasAuthenticationFilter().also {
        it.setFilterProcessesUrl("/${serviceLoginUrl}")
        it.setAuthenticationManager(authenticationManager)
        it.setServiceProperties(serviceProperties1)
    }

    @Bean
    fun casAuthenticationProvider1(
        @Qualifier("serviceProperties1") serviceProperties1: ServiceProperties,
        @Qualifier("ticketValidator1") ticketValidator1: TicketValidator,
    ): CasAuthenticationProvider =
        CasAuthenticationProvider().also {
            it.setServiceProperties(serviceProperties1)
            it.setTicketValidator(ticketValidator1)
            it.setAuthenticationUserDetailsService(
                CasAuthenticationUserDetailService(
                    casUserDetailService,
                    casKey
                )
            )
            it.setKey(casKey)
        }

    @Bean
    fun serviceProperties1() =
        ServiceProperties().also {
            it.service = "${elaasticServerUrl}${serviceLoginUrl}" // TODO ***
            it.isSendRenew = false
        }

    @Bean
    fun ticketValidator1(): TicketValidator = // TODO Check if that works with a CAS v1 server
        Cas30ServiceTicketValidator(casServerUrl)


    @Bean
    fun casAuthenticationEntryPoint1(@Qualifier("serviceProperties1") serviceProperties1: ServiceProperties) =
        CasAuthenticationEntryPoint().also {
        it.loginUrl = "${casServerUrl}/login"
        it.serviceProperties = serviceProperties1
    }
}