package org.elaastic.auth.cas

import org.jasig.cas.client.validation.TicketValidator
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.authentication.CasAuthenticationToken
import org.springframework.security.core.Authentication

class ElaasticCasAuthenticationProvider(
    val casKey: String,
    val casProvider: String,
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
                casKey,
                casProvider,
            )
        )
        this.key = casKey
    }

    override fun authenticate(authentication: Authentication?): Authentication? {
        if(authentication is CasTicketAuthenticationToken && authentication.casKey != casKey) {
            return null // Not concerned ; this authentication is from another CAS server
        }

        return super.authenticate(authentication)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return (CasTicketAuthenticationToken::class.java.isAssignableFrom(authentication)
                || CasAuthenticationToken::class.java.isAssignableFrom(authentication)
                || CasAssertionAuthenticationToken::class.java.isAssignableFrom(authentication))
    }
}