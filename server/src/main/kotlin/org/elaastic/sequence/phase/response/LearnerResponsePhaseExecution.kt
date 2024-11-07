package org.elaastic.sequence.phase.response

import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.sequence.phase.LearnerPhaseExecution

class LearnerResponsePhaseExecution(
    val responseSubmitted: Boolean,
    responseSubmissionSpecification: ResponseSubmissionSpecification
) : LearnerPhaseExecution {
}