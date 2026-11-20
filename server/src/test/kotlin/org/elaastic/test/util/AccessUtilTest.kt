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

package org.elaastic.test.util

import org.elaastic.common.util.requireAccess
import org.elaastic.common.util.requireAccessThrowDenied
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.*
import org.springframework.security.access.AccessDeniedException as AccessDeniedExceptionSpring

class AccessUtilTest {

    @Test
    fun `test of requireAccess`() {
        assertDoesNotThrow {
            requireAccess(true) { "This should not throw an exception" }
        }
        assertThrows(IllegalAccessException::class.java) {
            requireAccess(false) { "This should throw an exception" }
        }
    }

    @Test
    fun `test of requireAccessThrowDenied`() {
        assertDoesNotThrow {
            requireAccessThrowDenied(true) { "This should not throw an exception" }
        }
        // I rename the import to avoid confusion between the two classes (one is from kotlin, the other from spring)
        assertThrows(AccessDeniedExceptionSpring::class.java) {
            requireAccessThrowDenied(false) { "This should throw an exception" }
        }
    }
}