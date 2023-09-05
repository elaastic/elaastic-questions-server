package org.elaastic.questions.player.phase.result

import org.elaastic.questions.player.components.chatgptEvaluation.ChatgptEvaluationModel
import org.elaastic.questions.player.components.results.ResultsModel
import org.elaastic.questions.player.components.studentResults.LearnerResultsModel
import org.elaastic.questions.player.phase.PhaseViewModel

class LearnerResultPhaseViewModel(
    val resultsArePublished: Boolean,
    val myResultsModel: LearnerResultsModel,
    val sequenceResultsModel: ResultsModel,
    val myChatgptEvaluationModel: ChatgptEvaluationModel
) : PhaseViewModel