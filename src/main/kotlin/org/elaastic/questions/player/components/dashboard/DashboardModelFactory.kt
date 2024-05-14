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
import org.springframework.stereotype.Service

@Service
object DashboardModelFactory {

    fun build(sequence: Sequence,
              previousSequence: Sequence?,
              nextSequence: Sequence?,
              attendees: List<LearnerAssignment>,
              responses: List<Response>,
              openedPane: String): DashboardModel {

        val attendeesResponses: Map<Long, List<Response>> = responses.groupBy { it.learner.id!! }

        val responsePhaseAttendees: List<LearnerAssignment> = attendees.filter {
            val learnerStepsModel: StepsModel
                = StepsModelFactory.buildForLearner(sequence, sequence.activeInteraction)

            val submittedResponsesByLearner: Int = attendeesResponses[it.learner.id]?.count { response ->
                response.interaction.interactionType == InteractionType.ResponseSubmission
            } ?: 0

            submittedResponsesByLearner == 0
                || (submittedResponsesByLearner == 1
                && learnerStepsModel.evaluationState == StepsModel.PhaseState.DISABLED)   //  ==> phase 1
        }

        val evaluationPhaseAttendees: List<LearnerAssignment> = attendees.filter {
            val learnerStepsModel: StepsModel
                = StepsModelFactory.buildForLearner(sequence, sequence.activeInteraction)

            val submittedResponseByLearner: Int = attendeesResponses[it.learner.id]?.count { response ->
                response.interaction.interactionType == InteractionType.ResponseSubmission
            } ?: 0

            submittedResponseByLearner >= 1
                && learnerStepsModel.evaluationState != StepsModel.PhaseState.DISABLED    //  ==> phase 2
        }

        return DashboardModel(sequence,
                              attendees,
                              attendees.size,
                              attendeesResponses,
                              responsePhaseAttendees,
                              evaluationPhaseAttendees,
                              openedPane,
                              previousSequence?.id,
                              nextSequence?.id)
    }

}