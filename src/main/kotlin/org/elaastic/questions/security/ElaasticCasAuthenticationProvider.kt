package org.elaastic.questions.security

import org.elaastic.questions.directory.cas.CasAuthenticationUserDetailService
import org.elaastic.questions.directory.cas.CasUserDetailService
import org.jasig.cas.client.validation.TicketValidator
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAuthenticationProvider

class ElaasticCasAuthenticationProvider(
    casKey: String,
    casUserDetailService: CasUserDetailService,
    serviceProperties: ServiceProperties,
    ticketValidator: TicketValidator,
) : CasAuthenticationProvider() {
    init {
        this.setServiceProperties(serviceProperties)
        this.ticketValidator = ticketValidator
        this.setAuthenticationUserDetailsService(
            CasAuthenticationUserDetailService(
                casUserDetailService,
                casKey
            )
        )
        this.key = casKey
    }
}