package org.elaastic.questions.player.phase.result

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.questions.player.components.studentResults.LearnerResultsModel
import org.elaastic.questions.player.phase.LearnerPhaseExecution

class LearnerResultPhaseExecution(
    val responseSet: ResponseSet,
    val userCanRefreshResults: Boolean,
    val myResultsModel: LearnerResultsModel
) : LearnerPhaseExecution {
}