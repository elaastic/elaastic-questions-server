package org.elaastic.questions.security

import org.elaastic.questions.api.practice.subject.RestPracticeSubjectController
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
internal class WebSecurityIntegrationTest(
    @Autowired val mockMvc: MockMvc,

    @LocalServerPort
    val port: Int,

    @Autowired
    val restTemplate: TestRestTemplate,

    @Autowired
    val messageSource: MessageSource,
) {

    @Test
    fun `one can access login page anonymously`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("login"))
    }

    @Test
    @Throws(Exception::class)
    fun `test successful authentication`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())  // Add the CSRF token
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "fsil")
                .param("password", "1234")
        )
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/home"))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun `test authentication failure`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())  // Add the CSRF token
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "testuser")
                .param("password", "wrongpassword")
        )
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/login?error"))
    }

    @Test
    fun `When a user access a protected URL without being authenticated, it should be redirected to the login form`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/subject")
                .with(SecurityMockMvcRequestPostProcessors.csrf())  // Add the CSRF token
        )
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection)
            .andExpect(MockMvcResultMatchers.redirectedUrlPattern("**/login"))
    }

    @Test
    fun `An authenticated user can browse the secured web pages`() {
        val loginUrl = "http://localhost:$port/login"
        val privateUrl = "http://localhost:$port/subject"

        // Authentication
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginPageResponse = restTemplate.getForEntity(
            loginUrl,
            String::class.java
        )
        val csrfToken: String? = extractCsrfToken(loginPageResponse.body!!)
        loginHeaders[HttpHeaders.COOKIE] = loginPageResponse.headers.getFirst(HttpHeaders.SET_COOKIE)
        loginHeaders[HttpHeaders.AUTHORIZATION] = "Bearer $csrfToken"
        val loginRequest = HttpEntity("_csrf=$csrfToken&username=fsil&password=1234", loginHeaders)
        val loginResponse = restTemplate.exchange(
            loginUrl, HttpMethod.POST, loginRequest,
            String::class.java
        )

        assertEquals(HttpStatus.FOUND, loginResponse.statusCode)

        // Get the authentication cookie
        val sessionCookie = loginResponse.headers.getFirst(HttpHeaders.SET_COOKIE)


        // Access the private page
        val privateHeaders = HttpHeaders()
        privateHeaders.set(HttpHeaders.COOKIE, sessionCookie)
        val privateRequest = HttpEntity<String>(privateHeaders)
        val privateResponse: ResponseEntity<String> = restTemplate.exchange(
            privateUrl,
            HttpMethod.GET,
            privateRequest,
            String::class.java
        )

        assertEquals(HttpStatus.OK, privateResponse.statusCode)

        val pageTitle = Jsoup.parse(privateResponse.body!!).title()
        assertTrue(
            pageTitle.contains(
                messageSource.getMessage(
                    "subject.my.list.label",
                    null,
                    LocaleContextHolder.getLocale()
                ),
            )
        )
    }

    @Test
    fun `An authenticated user cannot access the REST API`() {
        val loginUrl = "http://localhost:$port/login"
        val apiUrl = "http://localhost:$port"+RestPracticeSubjectController.PRACTICE_API_URL

        // Authentication
        val loginHeaders = HttpHeaders()
        loginHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val loginPageResponse = restTemplate.getForEntity(
            loginUrl,
            String::class.java
        )
        val csrfToken: String? = extractCsrfToken(loginPageResponse.body!!)
        loginHeaders[HttpHeaders.COOKIE] = loginPageResponse.headers.getFirst(HttpHeaders.SET_COOKIE)
        loginHeaders[HttpHeaders.AUTHORIZATION] = "Bearer $csrfToken"
        val loginRequest = HttpEntity("_csrf=$csrfToken&username=fsil&password=1234", loginHeaders)
        val loginResponse = restTemplate.exchange(
            loginUrl, HttpMethod.POST, loginRequest,
            String::class.java
        )

        assertEquals(HttpStatus.FOUND, loginResponse.statusCode)

        // Get the authentication cookie
        val sessionCookie = loginResponse.headers.getFirst(HttpHeaders.SET_COOKIE)


        // Access the REST API
        val privateHeaders = HttpHeaders()
        privateHeaders.set(HttpHeaders.COOKIE, sessionCookie)
        val privateRequest = HttpEntity<String>(privateHeaders)
        val privateResponse: ResponseEntity<String> = restTemplate.exchange(
            apiUrl,
            HttpMethod.GET,
            privateRequest,
            String::class.java
        )

        assertEquals(HttpStatus.UNAUTHORIZED, privateResponse.statusCode)
    }

    private fun extractCsrfToken(responseBody: String): String? {
        val doc = Jsoup.parse(responseBody)
        val csrfInputElement = doc.selectFirst("input[name=_csrf]")
        return csrfInputElement?.attr("value")
    }
}