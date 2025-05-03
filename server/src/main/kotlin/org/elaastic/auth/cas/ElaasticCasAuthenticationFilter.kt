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

import org.springframework.core.log.LogMessage
import org.springframework.security.cas.web.CasAuthenticationFilter
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * This class is an adaptation of CasAuthenticationFilter needed to properly handle multiple CAS server as
 * authentication providers.
 * It introduces a CasTicketAuthenticationToken instead of UsernameAuthenticationToken. The purpose is to
 * avoir that DaoAuthenticationProvider tries to authenticate request with CAS ticket, and to add the casKey info
 * to the authentication so only the concerned CasAuthenticationProvider attempts to authenticate the request.
 *
 * Note : This cas does not support CAS Proxy Ticket.
 *
 * @author John Tranier
 */
class ElaasticCasAuthenticationFilter : CasAuthenticationFilter() {

    lateinit var casKey: String

    init {
        setAuthenticationFailureHandler(SimpleUrlAuthenticationFailureHandler())
    }

    @Throws(AuthenticationException::class, IOException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication? {

        val serviceTicketRequest = serviceTicketRequest(request, response)
        val username = if (serviceTicketRequest) CAS_STATEFUL_IDENTIFIER else CAS_STATELESS_IDENTIFIER
        var password = obtainArtifact(request)
        if (password == null) {
            this.logger.debug("Failed to obtain an artifact (cas ticket)")
            password = ""
        }
        val authRequest = CasTicketAuthenticationToken(
            casKey,
            username,
            password
        )
        authRequest.details = this.authenticationDetailsSource.buildDetails(request)
        return this.authenticationManager.authenticate(authRequest)
    }

    /**
     * Indicates if the request is elgible to process a service ticket. This method exists
     * for readability.
     * @param request
     * @param response
     * @return
     */
    private fun serviceTicketRequest(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        val result: Boolean = super.requiresAuthentication(request, response)
        this.logger.debug(LogMessage.format("serviceTicketRequest = %s", result))
        return result
    }
}