package org.elaastic.auth.oauth

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.net.URLEncoder
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.text.Charsets.UTF_8

@Component
class OidcHintFilter : OncePerRequestFilter() {

    companion object {
        const val TARGET_URL_SESSION_ATTR = "OIDC_TARGET_URL"
        const val OIDC_HINT = "oidc_hint"
    }

    /** Filter only if the request has a parameter named "oidc_hint". */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.getParameter(OIDC_HINT) == null
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val realm = request.getParameter(OIDC_HINT)!!

        val requestUri = request.requestURI
        val cleanQuery = request.parameterMap
            .filterKeys { it != OIDC_HINT }
            .flatMap { (key, values) ->
                values.map { value ->
                    "${URLEncoder.encode(key, UTF_8.name())}=${URLEncoder.encode(value, UTF_8.name())}"
                }
            }
            .joinToString("&")
        val targetUrl = if (cleanQuery.isBlank()) requestUri else "$requestUri?$cleanQuery"

        val auth = SecurityContextHolder.getContext().authentication
        val isAuthenticated = auth?.isAuthenticated == true && auth !is AnonymousAuthenticationToken

        if (isAuthenticated) {
            response.sendRedirect(targetUrl)
        } else {
            request.getSession(true).setAttribute(TARGET_URL_SESSION_ATTR, targetUrl)
            response.sendRedirect("/oauth2/authorization/$realm")
        }
    }

}