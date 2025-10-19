package org.elaastic.auth.oauth

import org.elaastic.auth.oauth.OidcHintFilter.Companion.TARGET_URL_SESSION_ATTR
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.web.savedrequest.SimpleSavedRequest
import org.springframework.test.context.junit.jupiter.EnabledIf

@EnabledIf(value = "#{environment.acceptsProfiles('oidc')}", loadContext = true)
class OidcLoginSuccessHandlerTest {

    @Test
    fun `onAuthenticationSuccess with session attribute`() {
        val oidcLoginSuccessHandler = OidcLoginSuccessHandler()
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val authentication = UsernamePasswordAuthenticationToken("user", "password")

        // Set the target URL in the session
        val target = "/target"
        request.session?.setAttribute(TARGET_URL_SESSION_ATTR, target)

        oidcLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication)

        // Check that the target URL is removed from the session
        assertNull(request.session?.getAttribute(TARGET_URL_SESSION_ATTR))

        // Check that the response redirects to the target URL
        assertEquals(302, response.status)
        assertEquals(target, response.redirectedUrl)
    }

    @Test
    fun `onAuthenticationSuccess without custom session attribute but with spring security's one`() {
        val oidcLoginSuccessHandler = OidcLoginSuccessHandler()
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val authentication = UsernamePasswordAuthenticationToken("user", "password")
        // Set the target URL in the session
        val target = "/target"
        request.session?.setAttribute("SPRING_SECURITY_SAVED_REQUEST", SimpleSavedRequest(target))

        assertNull(request.session?.getAttribute(TARGET_URL_SESSION_ATTR))
        assertNotNull(request.session?.getAttribute("SPRING_SECURITY_SAVED_REQUEST"))

        oidcLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication)

        assertNull(request.session?.getAttribute(TARGET_URL_SESSION_ATTR))

        // Check that the response redirects to the target URL
        assertEquals(302, response.status)
        assertEquals(target, response.redirectedUrl)
    }

    @Test
    fun `onAuthenticationSuccess without session attribute`() {
        val oidcLoginSuccessHandler = OidcLoginSuccessHandler()
        val request = MockHttpServletRequest()
        val response = MockHttpServletResponse()
        val authentication = UsernamePasswordAuthenticationToken("user", "password")

        // Check that the target URL does not exist in the session
        assertNull(request.session?.getAttribute(TARGET_URL_SESSION_ATTR))
        assertNull(request.session?.getAttribute("SPRING_SECURITY_SAVED_REQUEST"))

        oidcLoginSuccessHandler.onAuthenticationSuccess(request, response, authentication)

        assertNull(request.session?.getAttribute(TARGET_URL_SESSION_ATTR))

        // Check that the response redirects to the target URL
        assertEquals(302, response.status)
        assertEquals("/home", response.redirectedUrl)
    }
}