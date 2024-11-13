package org.elaastic.auth.local

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class LoginControllerTest {

    @Test
    fun testbuildCasUrlWithService() {
        // given a server URL and a service URL
        val serverUrl = "https://www.ecollege31.fr/cas"
        val serviceUrl = "https://localhost/login/cas/ecollege31"
        // when building the CAS login URL with the service parameter
        val expected = "https://www.ecollege31.fr/cas/login?service=https%3A%2F%2Flocalhost%2Flogin%2Fcas%2Fecollege31"
        val actual = LoginController.buildCasUrlWithService(serverUrl, serviceUrl)
        // Then the CAS login URL with the service parameter should be built as expected
        assertEquals(expected, actual)

        val serverUrl2 = "https://www.ecollege31.fr/cas/"
        val actual2 = LoginController.buildCasUrlWithService(serverUrl2, serviceUrl)
        assertEquals(expected, actual2)
    }
}