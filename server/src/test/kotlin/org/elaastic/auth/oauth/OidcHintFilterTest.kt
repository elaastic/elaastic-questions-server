package org.elaastic.auth.oauth

import org.elaastic.auth.oauth.OidcHintFilter.Companion.OIDC_HINT
import org.elaastic.auth.oauth.OidcHintFilter.Companion.TARGET_URL_SESSION_ATTR
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.junit.jupiter.EnabledIf
import javax.servlet.FilterChain

@EnabledIf(value = "#{environment.acceptsProfiles('oidc')}", loadContext = true)
class OidcHintFilterTest {

    /** To access non-public method in OidcHintFilter for testing purposes. */
    class TestableOidcHintFilter : OidcHintFilter() {
        fun testableShouldNotFilter(request: MockHttpServletRequest): Boolean {
            return super.shouldNotFilter(request)
        }

        fun testableDoFilterInternal(
            request: MockHttpServletRequest,
            response: MockHttpServletResponse,
            chain: FilterChain
        ) {
            super.doFilterInternal(request, response, chain)
        }
    }

    @Test
    fun `test shouldNotFilter`() {
        val oidcHintFilter = TestableOidcHintFilter()
        val request = MockHttpServletRequest()

        assertNull(request.getParameter(OIDC_HINT))
        assertTrue(oidcHintFilter.testableShouldNotFilter(request))

        request.addParameter(OIDC_HINT, "test")

        assertNotNull(request.getParameter(OIDC_HINT))
        assertFalse(oidcHintFilter.testableShouldNotFilter(request))
    }

    @Test
    fun `test doFilterInternal`() {
        val oidcHintFilter = TestableOidcHintFilter()
        val request = MockHttpServletRequest()
        request.addParameter(OIDC_HINT, "test")
        request.requestURI = "/test"
        request.addParameter("param1", "value1")
        request.addParameter("param2", "value2")

        val response = MockHttpServletResponse()

        oidcHintFilter.testableDoFilterInternal(request, response, MockFilterChain())

        // Check that the target URL is set correctly in the session
        assertEquals(
            "/test?param1=value1&param2=value2",
            request.session?.getAttribute(TARGET_URL_SESSION_ATTR)
        )

        // Check that the redirect URL is correct
        assertEquals("/oauth2/authorization/test", response.redirectedUrl)
    }

    @Test
    fun `test doFilterInternal without parameter`() {
        val oidcHintFilter = TestableOidcHintFilter()
        val request = MockHttpServletRequest()
        request.addParameter(OIDC_HINT, "test")
        val target = "/test"
        request.requestURI = target

        val response = MockHttpServletResponse()

        oidcHintFilter.testableDoFilterInternal(request, response, MockFilterChain())

        // Check that the target URL is set correctly in the session
        assertEquals(
            target,
            request.session?.getAttribute(TARGET_URL_SESSION_ATTR)
        )

        // Check that the redirect URL is correct
        assertEquals("/oauth2/authorization/test", response.redirectedUrl)
    }

    @Test
    fun `test doFilterInternal with authenticated user`() {
        val oidcHintFilter = TestableOidcHintFilter()
        val request = MockHttpServletRequest()
        request.addParameter(OIDC_HINT, "test")
        val targetURL = "/test"
        request.requestURI = targetURL

        val response = MockHttpServletResponse()

        // Set the SecurityContext with an STUB authenticated user
        val authentication = UsernamePasswordAuthenticationToken(
            "user",
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
        val originalContext = SecurityContextHolder.getContext()
        SecurityContextHolder.getContext().authentication = authentication

        oidcHintFilter.testableDoFilterInternal(request, response, MockFilterChain())

        // Check that the target URL is set correctly in the session
        assertEquals(targetURL, response.redirectedUrl)

        // Reset the SecurityContextHolder to its original state
        SecurityContextHolder.clearContext()
        SecurityContextHolder.setContext(originalContext)
    }
}