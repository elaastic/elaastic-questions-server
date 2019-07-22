package org.elaastic.questions.assignment.sequence.interaction

/**
 * @author John Tranier
 */
data class OneAttemptResult(var values: List<Float>) {

    fun size(): Int = values.size

    override fun equals(other: Any?): Boolean {
        if(other !is OneAttemptResult) return false
        return values.toFloatArray().contentEquals(other.values.toFloatArray())
    }

    override fun hashCode(): Int {
        return values.toFloatArray().contentHashCode()
    }
}
