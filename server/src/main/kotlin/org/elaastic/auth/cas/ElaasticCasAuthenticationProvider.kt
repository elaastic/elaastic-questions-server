/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.auth.cas

import org.elaastic.auth.UserLinkService
import org.jasig.cas.client.validation.TicketValidator
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.authentication.CasAuthenticationToken
import org.springframework.security.core.Authentication

class ElaasticCasAuthenticationProvider(
    val casKey: String,
    val casProvider: String,
    userLinkService: UserLinkService,
    serviceProperties: ServiceProperties,
    ticketValidator: TicketValidator,
) : CasAuthenticationProvider() {
    init {
        this.setServiceProperties(serviceProperties)
        this.ticketValidator = ticketValidator
        this.setAuthenticationUserDetailsService(
            CasAuthenticationUserDetailService(
                userLinkService,
                casKey,
                casProvider,
            )
        )
        this.key = casKey
    }

    override fun authenticate(authentication: Authentication?): Authentication? {
        if (authentication is CasTicketAuthenticationToken && authentication.casKey != casKey) {
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