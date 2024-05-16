package org.elaastic.questions.player.phase.result

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.player.components.evaluation.chatGptEvaluation.ChatGptEvaluationModel
import org.elaastic.questions.player.components.studentResults.LearnerResultsModel
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.togglz.core.manager.FeatureManager

class LearnerResultPhaseExecution(
    val responseSet: ResponseSet,
    val userCanRefreshResults: Boolean,
    val myResultsModel: LearnerResultsModel,
    val featureManager: FeatureManager,
    val messageBuilder: MessageBuilder,
    val myChatGptEvaluationModel: ChatGptEvaluationModel?,
) : LearnerPhaseExecution {
}