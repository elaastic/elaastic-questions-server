package org.elaastic.questions.security

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