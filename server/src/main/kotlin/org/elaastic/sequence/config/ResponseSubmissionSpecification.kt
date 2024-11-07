package org.elaastic.sequence.config

import org.elaastic.questions.assignment.sequence.interaction.InteractionType

/**
 * ResponseSubmissionSpecification is a data class that represents the
 * specification of the first phase of an interaction sequence.
 *
 * @property studentsProvideExplanation A boolean that indicates whether the
 *     students are required to provide an explanation for their response.
 * @property studentsProvideConfidenceDegree A boolean that indicates whether
 *     the students are required to provide a confidence degree for their
 *     response.
 * @see org.elaastic.questions.assignment.sequence.ConfidenceDegree
 */
data class ResponseSubmissionSpecification(
        var studentsProvideExplanation: Boolean,
        var studentsProvideConfidenceDegree: Boolean
) : InteractionSpecification {


    override fun getType(): InteractionType {
        return InteractionType.ResponseSubmission
    }

    fun setType(value: InteractionType) {
        assert(value == InteractionType.ResponseSubmission)
    }
}