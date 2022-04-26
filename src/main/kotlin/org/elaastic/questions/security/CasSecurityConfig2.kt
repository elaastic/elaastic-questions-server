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
class CasSecurityConfig2(
    @Autowired val casUserDetailService: CasUserDetailService,
    @Value("\${elaastic.questions.url}") val elaasticServerUrl: String,
) {
    private val casKey = "ENT_2"
    private val serviceLoginUrl = "login/cas/2"
    private val casServerUrl = "https://localhost:8444/cas"

    @Bean
    @Throws(java.lang.Exception::class)
    fun casAuthenticationFilter2(
        authenticationManager: AuthenticationManager,
        @Qualifier("serviceProperties2") serviceProperties2: ServiceProperties
    ) = CasAuthenticationFilter().also {
        it.setFilterProcessesUrl("/$serviceLoginUrl")
        it.setAuthenticationManager(authenticationManager)
        it.setServiceProperties(serviceProperties2)
    }

    @Bean
    fun casAuthenticationProvider2(
        @Qualifier("serviceProperties2") serviceProperties2: ServiceProperties,
        @Qualifier("ticketValidator2") ticketValidator2: TicketValidator,
    ): CasAuthenticationProvider =
        CasAuthenticationProvider().also {
            it.setServiceProperties(serviceProperties2)
            it.setTicketValidator(ticketValidator2)
            it.setAuthenticationUserDetailsService(
                CasAuthenticationUserDetailService(
                    casUserDetailService,
                    casKey
                )
            )
            it.setKey(casKey)
        }

    @Bean
    fun serviceProperties2() =
        ServiceProperties().also {
            it.service = "${elaasticServerUrl}${serviceLoginUrl}" // TODO ***
            it.isSendRenew = false
        }

    @Bean
    fun ticketValidator2(): TicketValidator = // TODO Check if that works with a CAS v1 server
        Cas30ServiceTicketValidator(casServerUrl)


    @Bean
    fun casAuthenticationEntryPoint2(@Qualifier("serviceProperties2") serviceProperties2: ServiceProperties) = CasAuthenticationEntryPoint().also {
        it.loginUrl = "${casServerUrl}/login"
        it.serviceProperties = serviceProperties2
    }
}