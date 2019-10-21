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

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.elaastic.questions.controller.MessageBuilder
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.components.assignmentOverview.AssignmentOverviewModelFactory
import org.elaastic.questions.player.components.command.CommandModelFactory
import org.elaastic.questions.player.components.evaluationPhase.EvaluationPhaseModelFactory
import org.elaastic.questions.player.components.evaluationPhase.ResponseData
import org.elaastic.questions.player.components.responsePhase.ResponsePhaseModelFactory
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
              getActiveInteractionForLearner: () -> Interaction?,
              hasResponseForUser: (attemptNum: AttemptNum) -> Boolean,
              findAllResponses: () -> ResponseSet,
              findAllRecommandedResponsesForUser: () -> List<Response>,
              userHasPerformedEvaluation: () -> Boolean,
              getFirstAttemptResponse: () -> Response?): PlayerModel =
            run {
                val assignment = sequence.assignment ?: error("The sequence must have an assignment to be played")
                val showResponsePhase = getShowResponsePhase(teacher, sequence, getActiveInteractionForLearner)
                val showEvaluationPhase = getShowEvaluationPhase(teacher, sequence, getActiveInteractionForLearner)
                val showResults =
                        if (teacher)
                            sequence.state != State.beforeStart
                        else sequence.resultsArePublished && getActiveInteractionForLearner()?.isRead() == true

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
                                getActiveInteractionForLearner()
                        ),
                        sequenceStatistics = SequenceStatistics(1, 2, 3), // TODO Compute statistics
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
                        showResponsePhase = showResponsePhase,
                        responsePhaseModel =
                        if (showResponsePhase)
                            ResponsePhaseModelFactory.build(
                                    responseSubmitted = hasResponseForUser(1),
                                    sequence = sequence,
                                    userActiveInteraction = getActiveInteractionForLearner()
                            )
                        else null,
                        showEvaluationPhase = showEvaluationPhase,
                        evaluationPhaseModel =
                        if (showEvaluationPhase)
                            run {
                                val userHasPerformedEvaluation = userHasPerformedEvaluation()
                                val secondAttemptAlreadySubmitted = hasResponseForUser(2)
                                val responsesToGrade = if (!userHasPerformedEvaluation)
                                    findAllRecommandedResponsesForUser().map { ResponseData(it) }
                                else listOf()
                                EvaluationPhaseModelFactory.build(
                                        userHasCompletedPhase2 = (responsesToGrade.isEmpty() && secondAttemptAlreadySubmitted),
                                        userHasPerformedEvaluation = userHasPerformedEvaluation,
                                        secondAttemptAlreadySubmitted = secondAttemptAlreadySubmitted,
                                        responsesToGrade = responsesToGrade,
                                        sequence = sequence,
                                        userActiveInteraction = getActiveInteractionForLearner(),
                                        firstAttemptResponse = getFirstAttemptResponse()
                                )
                            }
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

    // TODO Perhaps we could implement the logic to get the current LearnerPhase
    private fun getShowResponsePhase(teacher: Boolean,
                                     sequence: Sequence,
                                     getActiveInteractionForLearner: () -> Interaction?): Boolean =
            !teacher &&
                    sequence.state == State.show &&
                    getActiveInteractionForLearner()?.isResponseSubmission() == true

    private fun getShowEvaluationPhase(teacher: Boolean,
                                       sequence: Sequence,
                                       getActiveInteractionForLearner: () -> Interaction?): Boolean =
            !teacher &&
                    sequence.state == State.show &&
                    getActiveInteractionForLearner()?.isEvaluation() == true
}