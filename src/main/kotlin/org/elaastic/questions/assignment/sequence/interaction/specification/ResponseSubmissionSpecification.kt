package org.elaastic.questions.assignment.sequence.interaction.specification

import org.elaastic.questions.assignment.sequence.interaction.InteractionType


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