/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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