package org.elaastic.sequence.phase.result

import org.elaastic.player.evaluation.chatgpt.ChatGptEvaluationModel
import org.elaastic.player.results.ResultsModel
import org.elaastic.questions.player.components.studentResults.LearnerResultsModel
import org.elaastic.sequence.phase.PhaseViewModel

class LearnerResultPhaseViewModel(
    val resultsArePublished: Boolean,
    val myResultsModel: LearnerResultsModel,
    val sequenceResultsModel: ResultsModel,
    val myChatGptEvaluationModel: ChatGptEvaluationModel?,
) : PhaseViewModel