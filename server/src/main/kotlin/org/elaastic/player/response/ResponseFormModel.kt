package org.elaastic.player.response

import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.activity.results.AttemptNum

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