package org.elaastic.sequence.phase.response

import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.sequence.phase.LearnerPhaseExecution

class LearnerResponsePhaseExecution(
    val responseSubmitted: Boolean,
    responseSubmissionSpecification: ResponseSubmissionSpecification
) : LearnerPhaseExecution {
}