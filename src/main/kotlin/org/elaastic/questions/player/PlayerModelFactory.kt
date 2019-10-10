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

import org.elaastic.questions.assignment.sequence.LearnerSequence
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.components.assignmentOverview.AssignmentOverviewModelFactory
import org.elaastic.questions.player.components.command.CommandModelFactory
import org.elaastic.questions.player.components.responseForm.ResponseFormModelFactory
import org.elaastic.questions.player.components.results.ResultsModelFactory
import org.elaastic.questions.player.components.sequenceInfo.SequenceInfoResolver
import org.elaastic.questions.player.components.statement.StatementInfo
import org.elaastic.questions.player.components.statement.StatementPanelModel
import org.elaastic.questions.player.components.steps.SequenceStatistics
import org.elaastic.questions.player.components.steps.StepsModelFactory

object PlayerModelFactory {

    fun build(user: User,
              sequence: Sequence,
              teacher: Boolean,
              nbRegisteredUsers: Int,
              sequenceToUserActiveInteraction: Map<Sequence, Interaction?>,
              messageBuilder: MessageBuilder,
              getLearnerSequence: () -> LearnerSequence,
              hasResponseForUser: () -> Boolean,
              findAllResponses: () -> ResponseSet): PlayerModel =
            run {
                val assignment = sequence.assignment ?: error("The sequence must have an assignment to be played")
                val showResponseForm = getShowResponseForm(teacher, sequence, getLearnerSequence)
                val showResults =
                        if (teacher)
                            sequence.state != State.beforeStart
                        else sequence.resultsArePublished && getLearnerSequence().activeInteraction?.isRead() == true

                PlayerModel(
                        assignment = assignment,
                        sequence = sequence,
                        userRole = if (teacher) UserRole.Teacher else UserRole.Learner,
                        assignmentOverviewModel = AssignmentOverviewModelFactory.build(
                                nbRegisteredUser = nbRegisteredUsers,
                                assignmentTitle = assignment.title,
                                sequences = assignment.sequences,
                                sequenceToUserActiveInteraction = sequenceToUserActiveInteraction,
                                selectedSequenceId = sequence.id,
                                teacher = teacher
                        ),
                        stepsModel =
                        if (teacher)
                            StepsModelFactory.buildForTeacher(sequence)
                        else StepsModelFactory.buildForLearner(
                                sequence,
                                getLearnerSequence().activeInteraction
                        ),
                        sequenceStatistics = SequenceStatistics(1,2,3), // TODO Compute statistics
                        commandModel =
                        if (teacher)
                            CommandModelFactory.build(user, sequence)
                        else null,
                        sequenceInfoModel = SequenceInfoResolver.resolve(teacher, sequence, messageBuilder),
                        statementPanelModel = StatementPanelModel(
                                hideStatement = !teacher && sequence.state == State.beforeStart,
                                panelClosed = teacher && sequence.state != State.beforeStart
                        ),
                        statement = StatementInfo(sequence.statement),
                        showResponseForm = showResponseForm,
                        responseFormModel =
                        if (showResponseForm)
                            ResponseFormModelFactory.build(
                                    responseSubmitted = hasResponseForUser(),
                                    attempt = 1,
                                    sequence = sequence,
                                    userActiveInteraction = getLearnerSequence().activeInteraction
                            )
                        else null,
                        showResults = showResults,
                        resultsModel =
                        if (showResults)
                            ResultsModelFactory.build(
                                    sequence,
                                    findAllResponses()
                            )
                        else null

                )
            }

    private fun getShowResponseForm(teacher: Boolean,
                                    sequence: Sequence,
                                    getLearnerSequence: () -> LearnerSequence): Boolean =
            !teacher &&
                    sequence.state == State.show &&
                    getLearnerSequence().activeInteraction.let {
                        it != null && it.isResponseSubmission()
                    }

}