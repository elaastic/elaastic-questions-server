package org.elaastic.questions.test.directive



inline fun <T, R> T.tExpect(that:String? = null, block: (T) -> R): R  {
    return this.let(block)
}

inline fun <T, R> T.tThen(that:String? = null, block: (T) -> R): R  {
    return this.let(block)
}

inline fun <T, R> T.tWhen(that:String? = null, block: (T) -> R): R  {
    return this.let(block)
}

fun Any.tNoProblem(that:String? = null) {
    // Just do nothing
}

inline fun <T> tWhen(that:String? = null,  block: () -> T) : T {
    return block()
}

inline fun <T> tGiven(that:String? = null, block: () -> T) : T {
    return block()
}
