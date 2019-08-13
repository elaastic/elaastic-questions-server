package org.elaastic.questions.test.directive



inline fun <T, R> T.tExpect(block: (T) -> R): R  {
    return this.let(block)
}

inline fun <T, R> T.tThen(block: (T) -> R): R  {
    return this.let(block)
}

inline fun <T, R> T.tWhen(block: (T) -> R): R  {
    return this.let(block)
}

inline fun Any.tNoProblem() {
    // Just do nothing
}

inline fun <T> tWhen( block: () -> T) : T {
    return block()
}

inline fun <T> tGiven( block: () -> T) : T {
    return block()
}