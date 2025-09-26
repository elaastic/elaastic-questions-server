package org.elaastic.auth.oauth

import org.elaastic.auth.oauth.OidcHintFilter.Companion.TARGET_URL_SESSION_ATTR
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.savedrequest.SavedRequest
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(prefix = "elaastic.openid", name = ["enabled"], havingValue = "true")
class OidcLoginSuccessHandler : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val targetUrl = request.session.getAttribute(TARGET_URL_SESSION_ATTR) as? String
            ?: (request.session.getAttribute("SPRING_SECURITY_SAVED_REQUEST") as? SavedRequest)?.redirectUrl
            ?: "/home"

        request.session.removeAttribute(TARGET_URL_SESSION_ATTR)

        response.sendRedirect(targetUrl)
    }
}
