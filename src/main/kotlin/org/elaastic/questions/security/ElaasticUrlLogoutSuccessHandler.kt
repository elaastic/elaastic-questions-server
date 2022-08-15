package org.elaastic.questions.security

import org.elaastic.questions.directory.User
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
typealias CasKey = String
typealias Url = String

/**
 * This LogoutSuccessHandler manage logouts from the elaastic formLogin and the configured CAS servers as well
 * If the session is not bound to a CAS session, it will redirect to formLogoutSuccessUrl
 * IF the session is bound to a CAS session, it will redirect to the CAS server logout URL
 */
class ElaasticUrlLogoutSuccessHandler(
    formLogoutSuccessUrl: String,
    private val casKeyToServerUrl: Map<CasKey, Url>,
    private val casLogoutSuccessUrl: String,
) : SimpleUrlLogoutSuccessHandler(), LogoutSuccessHandler {

    init {
        this.defaultTargetUrl = formLogoutSuccessUrl
    }

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ): String {
        val user = authentication.principal as User

        return if (user.casKey != null) {
            (casKeyToServerUrl[user.casKey]
                ?: throw IllegalStateException("No logout URL configured for the CAS server [${user.casKey}]")) +
                        casLogoutSuccessUrl
        } else super.determineTargetUrl(request, response, authentication)
    }
}