package org.elaastic.questions.test.directive

/**
 * @author John Tranier
 */

inline fun <T, R> T.tExpect(block: (T) -> R): R  {
    return this.let(block)
}

inline fun <T, R> T.tThen(block: (T) -> R): R  {
    return this.let(block)
}

inline fun <T, R> T.tWhen(block: (T) -> R): R  {
    return this.let(block)
}

fun Any.tNoProblem() {
    // Just do nothing
}