package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

/**
 * Enum representing the status of a ChatGptEvaluation.
 *
 * @property UNKNOWN The status is unknown.
 * @property PENDING The evaluation is pending.
 * @property DONE The evaluation is done.
 * @property ERROR An error occurred during the evaluation.
 */
enum class ChatGptEvaluationStatus {
    /** The status is unknown. */
    UNKNOWN,

    /** The evaluation is pending. */
    PENDING,

    /** The evaluation is done. */
    DONE,

    /** An error occurred during the evaluation. */
    ERROR
}