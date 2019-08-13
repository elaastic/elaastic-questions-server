package org.elaastic.questions.assignment.sequence.interaction


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
        return if (attempt2Result == null) {
            mapOf("1" to attempt1Result.values)
        } else mapOf("1" to attempt1Result.values, "2" to attempt2Result.values)
    }
}