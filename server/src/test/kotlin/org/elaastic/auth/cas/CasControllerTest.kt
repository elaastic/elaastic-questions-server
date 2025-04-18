package org.elaastic.auth.cas

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest

class CasControllerTest {

    @Test
    fun `test casRedirect`() {
        val localhost = "http://localhost"
        var remaining = "remaining/path?query=string"
        val casKey = "testKey"
        CasController().casRedirect(
            MockHttpServletRequest("GET", "/cas/$casKey/$remaining")
                .addDefaultHeader(),
            casKey
        ).let { result ->
            assertEquals("redirect:$localhost/$remaining", result)
        }

        remaining = "remaining/path"
        CasController().casRedirect(
            MockHttpServletRequest("GET", "/cas/$casKey/$remaining")
                .addDefaultHeader(),
            casKey
        ).let { result ->
            assertEquals("redirect:$localhost/$remaining", result)
        }
    }

    private fun MockHttpServletRequest.addDefaultHeader(): MockHttpServletRequest {
        addHeader("Host", "localhost")
        addHeader("X-Forwarded-Host", "localhost")
        addHeader("X-Forwarded-Prefix", "/elaastic-questions")
        return this
    }
}