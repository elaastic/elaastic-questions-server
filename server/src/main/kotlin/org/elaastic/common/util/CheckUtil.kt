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

/*
 * This file contains utility functions for checking conditions and throwing exceptions.
 * It includes functions for checking conditions with custom messages and for checking
 * if an object is not null.
 */

/**
 * Throws an [IllegalStateException] if the condition is false.
 *
 * Instead of writing:
 * ```kotlin
 * listOf(-1, 1, 2)
 *   .also {
 *      check(it.isNotEmpty()) {
 *          "List is empty"
 *      }
 *   }
 * ```
 *
 * You can write:
 * ```kotlin
 * listOf(-1, 1, 2)
 *   .alsoCheck({ it.isNotEmpty() }) {
 *      "List is empty"
 *   }
 * ```
 *
 * @param condition the condition to check
 * @param message the message to use in the exception if the condition is false
 * @throws IllegalStateException if the condition is false
 */
inline fun <T> T.alsoCheck(condition: (T) -> Boolean, message: (T) -> Any): T {
    check(condition(this)) {
        message(this)
    }
    return this
}

/**
 * Throws an [IllegalStateException] if the condition is false.
 *
 * Instead of writing:
 * ```kotlin
 * listOf(-1, 1, 2)
 *   .also {
 *      check(it.isNotEmpty())
 *   }
 * ```
 *
 * You can write:
 * ```kotlin
 * listOf(-1, 1, 2)
 *   .alsoCheck { it.isNotEmpty() }
 * ```
 *
 * @param condition the condition to check
 * @throws IllegalStateException if the condition is false
 */
inline fun <T> T.alsoCheck(condition: (T) -> Boolean): T {
    check(condition(this))
    return this
}

/**
 * Throws an exception of type [exceptionClass] if the condition is false.
 *
 * Instead of writing:
 * ```kotlin
 * listOf(-1, 1, 2)
 *   .also {
 *      if (it.isEmpty()) {
 *          throw IllegalArgumentException("List is empty")
 *      }
 *   }
 * ```
 *
 * You can write:
 * ```kotlin
 * listOf(-1, 1, 2)
 *   .alsoThrowIf(IllegalArgumentException::class.java, { it.isNotEmpty() }) {
 *       "List is empty"
 *   }
 * ```
 *
 * @param exceptionClass The class of the exception to throw
 * @param condition The condition to check
 * @param message A lambda that provides the exception message
 * @return The receiver object
 * @throws Exception if the condition evaluates to true
 */
inline fun <E> E.alsoThrowIfFalse(exceptionClass: Class<out Exception>, condition: (E) -> Boolean, message: (E) -> String): E {
    if (!condition(this)) {
        throw exceptionClass.getConstructor(String::class.java).newInstance(message(this))
    }
    return this
}