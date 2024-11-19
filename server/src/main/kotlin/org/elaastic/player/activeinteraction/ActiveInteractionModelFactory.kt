package org.elaastic.player.activeinteraction

import org.elaastic.player.results.ResultsModel
import org.elaastic.player.results.TeacherResultDashboardService
import org.elaastic.player.statement.StatementInfo
import org.elaastic.player.statement.StatementPanelModel
import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
object ActiveInteractionModelFactory {

    @Autowired
    lateinit var teacherResultDashboardService: TeacherResultDashboardService

    fun buildForTeacher(
        sequence: Sequence,
    ): TeacherActiveInteractionModel {
        val showResults = !sequence.isNotStarted()

        return TeacherActiveInteractionModel(
            statementPanelModel = StatementPanelModel(
                hideStatement = false,
                panelClosed = showResults
            ),
            statementInfo = StatementInfo(sequence.statement),
            showResults = showResults,
            resultsModel = getResultsModel(sequence),
        )
    }

    fun buildForLearner(
        learnerSequence: ILearnerSequence,
    ): LearnerActiveInteractionModel = LearnerActiveInteractionModel(
        statementPanelModel = StatementPanelModel(
            hideStatement = learnerSequence.sequence.state == State.beforeStart,
            panelClosed = false
        ),
        statementInfo = StatementInfo(learnerSequence.sequence.statement),
        phaseList = learnerSequence.phaseList.filterNotNull(),
    )


    private fun getResultsModel(
        sequence: Sequence
    ): ResultsModel? {
        var resultsModel: ResultsModel? = null

        if (!sequence.isNotStarted()) {
            resultsModel = teacherResultDashboardService.buildModel(sequence)
        }

        return resultsModel
    }
}