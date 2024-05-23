/*
 *
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
 *
 */

package org.elaastic.questions.player.components.dashboard

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.LearnerAssignment
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.player.components.steps.StepsModel
import org.elaastic.questions.player.components.steps.StepsModelFactory
import org.elaastic.questions.test.interpreter.command.Phase
import org.springframework.stereotype.Service

@Service
object DashboardModelFactory {

    private fun isResponsePhaseActive(learnerStepsModel: StepsModel): Boolean
        = learnerStepsModel.responseSubmissionState == StepsModel.PhaseState.ACTIVE

    private fun isEvaluationPhaseActive(learnerStepsModel: StepsModel): Boolean
        = learnerStepsModel.evaluationState == StepsModel.PhaseState.ACTIVE

    private fun isResponsePhasePlayed(learnerStepsModel: StepsModel): Boolean
        = this.isResponsePhaseActive(learnerStepsModel)
        || learnerStepsModel.responseSubmissionState == StepsModel.PhaseState.COMPLETED

    private fun isEvaluationPhasePlayed(learnerStepsModel: StepsModel): Boolean
        = this.isEvaluationPhaseActive(learnerStepsModel)
        || learnerStepsModel.evaluationState == StepsModel.PhaseState.COMPLETED

    fun build(sequence: Sequence,
              previousSequence: Sequence?,
              nextSequence: Sequence?,
              attendees: List<LearnerAssignment>,
              responses: List<Response>,
              openedPane: String): DashboardModel {

        val attendeesResponses: Map<Long, Pair<Response?, Response?>> = responses
            .groupBy { it.learner.id!! }
            .mapValues {
                val firstAttemptResponse = it.value.find { response ->
                    response.attempt == 1
                    && response.interaction.interactionType === InteractionType.ResponseSubmission
                }

                val secondAttemptResponse = it.value.find { response ->
                    response.attempt == 2
                    && response.interaction.interactionType === InteractionType.ResponseSubmission
                }

                Pair(firstAttemptResponse, secondAttemptResponse)
            }

        val learnerStepsModel: StepsModel
            = StepsModelFactory.buildForLearner(sequence, sequence.activeInteraction)

        val responsePhaseAttendees: Pair<List<LearnerAssignment>, List<LearnerAssignment>>
            = attendees
                .filter {
                    val submittedResponsesByLearner: Pair<Response?, Response?>?
                        = attendeesResponses[it.learner.id]

                    submittedResponsesByLearner?.first === null
                    || !this.isEvaluationPhasePlayed(learnerStepsModel)   //  ==> phase 1
                }
                .partition {
                    // FIRST LIST = IN COURSE
                    // SECOND LIST = FINISHED
                    attendeesResponses[it.learner.id]?.first === null
                }

        val responsePhaseAttendeesCount: Int
            = responsePhaseAttendees.first.size + responsePhaseAttendees.second.size

        val evaluationPhaseAttendees: Pair<List<LearnerAssignment>, List<LearnerAssignment>>
            = attendees
                .filter {
                    val submittedResponseByLearner: Pair<Response?, Response?>?
                        = attendeesResponses[it.learner.id]

                    submittedResponseByLearner?.first !== null
                    && this.isEvaluationPhasePlayed(learnerStepsModel)    //  ==> phase 2
                }
                .partition {
                    // FIRST LIST = IN COURSE
                    // SECOND LIST = FINISHED
                    attendeesResponses[it.learner.id]?.first !== null
                }

        val evaluationPhaseAttendeesCount: Int
            = evaluationPhaseAttendees.first.size + evaluationPhaseAttendees.second.size

        return DashboardModel(sequence,
                              attendees,
                              attendees.size,
                              attendeesResponses,
                              responsePhaseAttendees,
                              responsePhaseAttendeesCount,
                              evaluationPhaseAttendees,
                              evaluationPhaseAttendeesCount,
                              openedPane,
                              previousSequence?.id,
                              nextSequence?.id,
                              this.isResponsePhasePlayed(learnerStepsModel),
                              this.isEvaluationPhasePlayed(learnerStepsModel),
                              this.isResponsePhaseActive(learnerStepsModel),
                              this.isEvaluationPhaseActive(learnerStepsModel))
    }

}