package org.elaastic.questions.assignment.sequence.peergrading.draxo.option

enum class OptionType {
    POSITIVE,
    NEGATIVE,
    UNKNOWN;

    /**
     * Returns the CSS class use to display the DRAXO grid
     */
    fun getCSSClass() = when (this) {
        POSITIVE -> "positive"
        NEGATIVE -> "negative"
        UNKNOWN -> "unknown"
    }
}