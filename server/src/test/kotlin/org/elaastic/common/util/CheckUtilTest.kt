package org.elaastic.common.util

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CheckUtilTest {

    @Test
    fun `test alsoCheck with true condition`() {
        val list = listOf(1, 2, 3)
        val condition: (List<Int>) -> Boolean = { it.isNotEmpty() }
        assertTrue(condition(list))
        assertEquals(list, list.alsoCheck(condition) { "List is empty" })
        assertEquals(list, list.alsoCheck(condition))
    }

    @Test
    fun `test alsoCheck with false condition`() {
        val list = listOf(1, 2, 3)
        val condition: (List<Int>) -> Boolean = { it.isEmpty() }
        assertFalse(condition(list))
        assertThrows<IllegalStateException> {
            list.alsoCheck(condition) {
                "List is empty"
            }
        }
        assertThrows<IllegalStateException> {
            list.alsoCheck(condition)
        }
    }

    @Test
    fun `test alsoThrowIf with true condition`() {
        val list = listOf(1, 2, 3)
        val condition: (List<Int>) -> Boolean = { it.isNotEmpty() }
        assertTrue(condition(list))
        assertEquals(list, list.alsoThrowIfFalse(IllegalStateException::class.java, condition) { "List is empty" })
    }

    @Test
    fun `test alsoThrowIf with false condition and different Exception class`() {
        val list = listOf(1, 2, 3)
        val condition: (List<Int>) -> Boolean = { it.isEmpty() }
        assertFalse(condition(list))
        assertThrows<IllegalStateException> {
            list.alsoThrowIfFalse(IllegalStateException::class.java, condition) {
                "List is empty"
            }
        }
        assertThrows<NullPointerException> {
            list.alsoThrowIfFalse(NullPointerException::class.java, condition) {
                "List is empty"
            }
        }
    }
}