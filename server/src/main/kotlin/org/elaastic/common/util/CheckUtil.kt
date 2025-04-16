package org.elaastic.common.util

import kotlin.contracts.contract

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