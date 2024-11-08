package org.elaastic.sequence.phase.result

import org.elaastic.activity.response.ResponseSet
import org.elaastic.common.web.MessageBuilder
import org.elaastic.player.evaluation.chatgpt.ChatGptEvaluationModel
import org.elaastic.player.results.learner.LearnerResultsModel
import org.elaastic.sequence.phase.LearnerPhaseExecution
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