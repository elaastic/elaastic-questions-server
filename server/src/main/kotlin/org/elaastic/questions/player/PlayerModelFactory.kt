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

import org.elaastic.common.web.MessageBuilder
import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.components.assignmentOverview.AssignmentOverviewModelFactory
import org.elaastic.questions.player.components.command.CommandModelFactory
import org.elaastic.questions.player.components.results.TeacherResultDashboardService
import org.elaastic.questions.player.components.sequenceInfo.SequenceInfoResolver
import org.elaastic.questions.player.components.statement.StatementInfo
import org.elaastic.questions.player.components.statement.StatementPanelModel
import org.elaastic.questions.player.components.steps.SequenceStatistics
import org.elaastic.questions.player.components.steps.StepsModelFactory

object PlayerModelFactory {

    /**
     * @param nbReportBySequence Map of sequence to (a pair of (total numbers of reports) and (number of reports to
     *    moderate))
     */
    fun buildForTeacher(
        user: User,
        sequence: Sequence,
        serverBaseUrl: String,
        nbRegisteredUsers: Int,
        sequenceToUserActiveInteraction: Map<Sequence, Interaction?>,
        messageBuilder: MessageBuilder,
        sequenceStatistics: SequenceStatistics,
        teacherResultDashboardService: TeacherResultDashboardService,
        nbReportBySequence: Map<Sequence, Pair<Int, Int>>,
    ): TeacherPlayerModel {
        val assignment = sequence.assignment ?: error("The sequence must have an assignment to be played")
        val showResults = sequence.state != State.beforeStart

        return TeacherPlayerModel(
            serverBaseUrl = serverBaseUrl,
            sequence = sequence,
            assignmentOverviewModel = AssignmentOverviewModelFactory.build(
                nbRegisteredUser = nbRegisteredUsers,
                assignment = assignment,
                sequenceToUserActiveInteraction = sequenceToUserActiveInteraction,
                selectedSequenceId = sequence.id,
                teacher = true,
                nbReportBySequence = nbReportBySequence,
            ),
            stepsModel = StepsModelFactory.buildForTeacher(sequence),
            sequenceStatistics = sequenceStatistics,
            commandModel = CommandModelFactory.build(user, sequence),
            sequenceInfoModel = SequenceInfoResolver.resolve(
                true,
                sequence,
                messageBuilder,
                nbReportBySequence[sequence] ?: (0 to 0)
            ),
            statementPanelModel = StatementPanelModel(
                hideStatement = false,
                panelClosed = sequence.state != State.beforeStart
            ),
            statement = StatementInfo(sequence.statement),
            showResults = showResults,
            resultsModel = if (showResults)
                teacherResultDashboardService.buildModel(sequence)
            else null,
            assignmentOverviewModelOneSequence = AssignmentOverviewModelFactory.buildOnSequence(
                teacher = true,
                assignment = assignment,
                nbRegisteredUser = nbRegisteredUsers,
                userActiveInteraction = sequenceToUserActiveInteraction[sequence],
                selectedSequence = sequence,
            )
        )
    }


    fun buildForLearner(
        sequence: Sequence,
        nbRegisteredUsers: Int,
        openedPane: String,
        sequenceToUserActiveInteraction: Map<Sequence, Interaction?>,
        messageBuilder: MessageBuilder,
        activeInteraction: Interaction?,
        learnerSequence: ILearnerSequence,
    ): LearnerPlayerModel = run {
        val assignment = sequence.assignment ?: error("The sequence must have an assignment to be played")

        LearnerPlayerModel(
            sequence = sequence,
            assignmentOverviewModel = AssignmentOverviewModelFactory.build(
                nbRegisteredUser = nbRegisteredUsers,
                assignment = assignment,
                sequenceToUserActiveInteraction = sequenceToUserActiveInteraction,
                selectedSequenceId = sequence.id,
                teacher = false,
            ),
            stepsModel = StepsModelFactory.buildForLearner(sequence, activeInteraction),
            sequenceInfoModel = SequenceInfoResolver.resolve(false, sequence, messageBuilder),
            statementPanelModel = StatementPanelModel(
                hideStatement = sequence.state == State.beforeStart,
                panelClosed = false
            ),
            statement = StatementInfo(sequence.statement),
            phaseList = learnerSequence.phaseList.filterNotNull()
        )
    }

}                                                        