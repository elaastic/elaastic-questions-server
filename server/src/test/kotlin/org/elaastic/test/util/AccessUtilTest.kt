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