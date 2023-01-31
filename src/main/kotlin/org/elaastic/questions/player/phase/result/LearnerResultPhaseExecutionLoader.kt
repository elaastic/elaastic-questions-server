package org.elaastic.questions.player.phase.result

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.interaction.results.ResultsService
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.player.components.studentResults.LearnerResultsModelFactory
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.elaastic.questions.player.phase.LearnerPhaseExecutionLoader
import org.elaastic.questions.player.phase.LearnerPhase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.togglz.core.manager.FeatureManager

@Service("LearnerResultPhaseExecutionLoader")
class LearnerResultPhaseExecutionLoader(
    @Autowired val resultsService: ResultsService,
    @Autowired val responseService: ResponseService,
    @Autowired val featureManager: FeatureManager,
    @Autowired val messageBuilder: MessageBuilder,
) : LearnerPhaseExecutionLoader {

    override fun build(learnerPhase: LearnerPhase): LearnerPhaseExecution = run {
        val firstResponse = responseService.find(
            learnerPhase.learnerSequence.learner,
            learnerPhase.learnerSequence.sequence
        )

        val secondResponse = responseService.find(
            learnerPhase.learnerSequence.learner,
            learnerPhase.learnerSequence.sequence,
            2
        )

        // Get the "My results" data for the learner
        val myResultsModel = when (learnerPhase.learnerSequence.sequence.statement.questionType) {
            QuestionType.OpenEnded ->
                LearnerResultsModelFactory.buildOpenResult(
                    firstResponse,
                    secondResponse
                )
            QuestionType.ExclusiveChoice ->
                LearnerResultsModelFactory.buildExclusiveChoiceResult(
                    firstResponse,
                    secondResponse,
                    learnerPhase.learnerSequence.sequence.statement
                )
            QuestionType.MultipleChoice ->
                LearnerResultsModelFactory.buildMultipleChoiceResult(
                    firstResponse,
                    secondResponse,
                    learnerPhase.learnerSequence.sequence.statement
                )
        }

        LearnerResultPhaseExecution(
            responseSet = responseService.findAll(learnerPhase.learnerSequence.sequence, excludeFakes = false),
            userCanRefreshResults = resultsService.canUpdateResults(
                learnerPhase.learnerSequence.learner,
                learnerPhase.learnerSequence.sequence
            ),
            myResultsModel = myResultsModel,
            featureManager = featureManager,
            messageBuilder = messageBuilder,
        )
    }
}