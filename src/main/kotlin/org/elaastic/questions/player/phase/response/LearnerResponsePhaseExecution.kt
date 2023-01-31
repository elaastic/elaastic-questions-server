package org.elaastic.questions.player.phase.response

import org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
import org.elaastic.questions.player.phase.LearnerPhaseExecution

class LearnerResponsePhaseExecution(
    val responseSubmitted: Boolean,
    responseSubmissionSpecification: ResponseSubmissionSpecification
) : LearnerPhaseExecution {
}