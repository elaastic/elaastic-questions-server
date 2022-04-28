/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.elaastic.questions.player

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.player.components.assignmentOverview.AssignmentOverviewModel
import org.elaastic.questions.player.components.command.CommandModel
import org.elaastic.questions.player.components.evaluationPhase.EvaluationPhaseModel
import org.elaastic.questions.player.components.studentResults.LearnerResultsModel
import org.elaastic.questions.player.components.responsePhase.ResponsePhaseModel
import org.elaastic.questions.player.components.results.ResultsModel
import org.elaastic.questions.player.components.sequenceInfo.SequenceInfoModel
import org.elaastic.questions.player.components.statement.StatementInfo
import org.elaastic.questions.player.components.statement.StatementPanelModel
import org.elaastic.questions.player.components.steps.SequenceStatistics
import org.elaastic.questions.player.components.steps.StepsModel

open class PlayerModel(
        val assignment: Assignment,
        val sequence: Sequence,
        val userRole: UserRole,
        val assignmentOverviewModel: AssignmentOverviewModel,
        val stepsModel: StepsModel,
        val sequenceInfoModel: SequenceInfoModel,                       
        val statementPanelModel: StatementPanelModel,
        val statement: StatementInfo,
        val showResponsePhase: Boolean,
        val showEvaluationPhase: Boolean,
        val showResults: Boolean,
        val resultsModel: ResultsModel?,
        val learnerResultsModel: LearnerResultsModel?
)

class TeacherPlayerModel(
    assignment: Assignment,
    val serverBaseUrl: String = "Hello World",
    sequence: Sequence,
    assignmentOverviewModel: AssignmentOverviewModel,
    stepsModel: StepsModel,
    val sequenceStatistics: SequenceStatistics,
    val commandModel: CommandModel,
    sequenceInfoModel: SequenceInfoModel,
    statementPanelModel: StatementPanelModel,
    statement: StatementInfo,
    showResults: Boolean,
    resultsModel: ResultsModel?
) : PlayerModel(
        assignment = assignment,
        sequence = sequence,
        userRole = UserRole.Teacher, // TODO Check if we need it
        assignmentOverviewModel = assignmentOverviewModel,
        stepsModel = stepsModel,
        sequenceInfoModel = sequenceInfoModel,
        statementPanelModel = statementPanelModel, // TODO Perhaps we merge this one with the following
        statement = statement,
        showResponsePhase = false,
        showEvaluationPhase = false,
        showResults = showResults,
        resultsModel = resultsModel,
        learnerResultsModel = null
)

class LearnerPlayerModel(
    assignment: Assignment,
    sequence: Sequence,
    assignmentOverviewModel: AssignmentOverviewModel,
    stepsModel: StepsModel,
    sequenceInfoModel: SequenceInfoModel,
    statementPanelModel: StatementPanelModel,
    statement: StatementInfo,
    showResponsePhase: Boolean,
    val responsePhaseModel: ResponsePhaseModel?,
    showEvaluationPhase: Boolean,
    val evaluationPhaseModel: EvaluationPhaseModel?,
    showResults: Boolean,
    resultsModel: ResultsModel?,
    learnerResultsModel: LearnerResultsModel?
) : PlayerModel(
        assignment = assignment,
        sequence = sequence,
        userRole = UserRole.Learner, // TODO Check if we need it
        assignmentOverviewModel = assignmentOverviewModel,
        stepsModel = stepsModel,
        sequenceInfoModel = sequenceInfoModel,
        statementPanelModel = statementPanelModel, // TODO Perhaps we merge this one with the following
        statement = statement,
        showResponsePhase = showResponsePhase,
        showEvaluationPhase = showEvaluationPhase,
        showResults = showResults,
        resultsModel = resultsModel,
        learnerResultsModel = learnerResultsModel
)

enum class UserRole {
    Teacher, Learner
}