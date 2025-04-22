package org.elaastic.auth.oauth

import org.elaastic.auth.oauth.OidcHintFilter.Companion.TARGET_URL_SESSION_ATTR
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class OidcLoginSuccessHandler : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val targetUrl = request.session.getAttribute(TARGET_URL_SESSION_ATTR) as? String ?: "/"

        request.session.removeAttribute(TARGET_URL_SESSION_ATTR)

        response.sendRedirect(targetUrl)
    }
}
