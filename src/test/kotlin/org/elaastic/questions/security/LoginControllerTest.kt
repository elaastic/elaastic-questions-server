package org.elaastic.questions.security

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.net.URLEncoder

class LoginControllerTest {

    @Test
    fun buildCasUrlWithService() {
        val serverUrl = "https://www.ecollege31.fr/cas"
        val serviceUrl = "https://localhost/login/cas/ecollege31"
        val expected = "https://www.ecollege31.fr/cas/login?service=https%3A%2F%2Flocalhost%2Flogin%2Fcas%2Fecollege31"
        val actual = LoginController.buildCasUrlWithService(serverUrl, serviceUrl)
        assertEquals(expected, actual)
    }
}