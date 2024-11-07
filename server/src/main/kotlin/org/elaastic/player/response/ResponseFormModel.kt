package org.elaastic.player.response

import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.sequence.interaction.results.AttemptNum

data class ResponseFormModel(
    val interactionId: Long,
    val attempt: AttemptNum,
    val hasChoices: Boolean,
    val multipleChoice: Boolean,
    val nbItem: Int? = null,
    val firstAttemptChoices: Array<Int> = arrayOf(),
    val firstAttemptExplanation: String? = null,
    val firstAttemptConfidenceDegree: ConfidenceDegree? = null,
    val responseSubmissionSpecification: ResponseSubmissionSpecification,
    val ConfidenceDegreeValues: Array<ConfidenceDegree> = ConfidenceDegree.values()
)