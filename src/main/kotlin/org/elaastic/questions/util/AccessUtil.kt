package org.elaastic.questions.util

import org.springframework.security.access.AccessDeniedException
import kotlin.jvm.Throws

/**
 * Throws an IllegalAccessException with the result of calling lazyMessage if the condition is false
 * @param condition the condition to check
 * @param lazyMessage the message to use in the exception if the condition is false
 * @throws IllegalAccessException if the condition is false
 */
@Throws(IllegalAccessException::class)
inline fun requireAccess(condition: Boolean, lazyMessage: () -> Any) {
    if (!condition) {
        val message = lazyMessage()
        throw IllegalAccessException(message.toString())
    }
}

/**
 * Throws an [AccessDeniedException] if the condition is false.
 */
@Throws(AccessDeniedException::class)
inline fun requireAccessThrowDenied(condition: Boolean, lazyMessage: () -> Any) {
    if (!condition) {
        val message = lazyMessage()
        throw AccessDeniedException(message.toString())
    }
}