package org.elaastic.sequence.phase.result

import org.elaastic.questions.player.components.evaluation.chatgpt.ChatGptEvaluationModel
import org.elaastic.questions.player.components.results.ResultsModel
import org.elaastic.questions.player.components.studentResults.LearnerResultsModel
import org.elaastic.sequence.phase.PhaseViewModel

class LearnerResultPhaseViewModel(
    val resultsArePublished: Boolean,
    val myResultsModel: LearnerResultsModel,
    val sequenceResultsModel: ResultsModel,
    val myChatGptEvaluationModel: ChatGptEvaluationModel?,
) : PhaseViewModel