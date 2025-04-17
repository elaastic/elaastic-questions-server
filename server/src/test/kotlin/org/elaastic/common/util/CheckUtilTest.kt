/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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