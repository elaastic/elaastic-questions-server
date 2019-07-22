package org.elaastic.questions.assignment.sequence.interaction

/**
 * @author John Tranier
 */
data class InteractionResult(
        val attempt1Result: OneAttemptResult,
        val attempt2Result: OneAttemptResult? = null
) {

    init {
        require(
                attempt2Result == null || attempt1Result.size() == attempt2Result.size()
        )

    }

    fun toLegacyFormat(): Map<String, List<Float>> {
        if(attempt2Result == null) {
            return mapOf("1" to attempt1Result.values)
        }
        else return mapOf("1" to attempt1Result.values, "2" to attempt2Result.values)
    }
}