package org.elaastic.questions.util

inline fun requireAccess(condition: Boolean, lazyMessage: () -> Any) {
    if (!condition) {
        val message = lazyMessage()
        throw IllegalAccessException(message.toString())
    }
}