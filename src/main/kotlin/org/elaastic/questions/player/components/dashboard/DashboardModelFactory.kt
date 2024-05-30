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

import org.elaastic.questions.assignment.LearnerAssignment
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.player.components.steps.StepsModel
import org.elaastic.questions.player.components.steps.StepsModelFactory
import org.springframework.stereotype.Service

@Service
object DashboardModelFactory {

    fun build(
        sequence: Sequence,
        previousSequence: Sequence?,
        nextSequence: Sequence?,
        attendees: List<LearnerAssignment>,
        responses: List<Response>,
        openedPane: String,
        evaluationCountByUser: Map<LearnerAssignment, Int>
    ): DashboardModel {

        val learnerStepsModel: StepsModel = StepsModelFactory.buildForTeacher(sequence)

        val learnersMonitoringModel: LearnersMonitoringModel = LearnersMonitoringModel(
            sequence.executionContext,
            convertPhaseState(learnerStepsModel.responseSubmissionState),
            convertPhaseState(learnerStepsModel.evaluationState),
            convertPhaseState(learnerStepsModel.readState)
        )

        val learners: MutableList<LearnerMonitoringModel> = mutableListOf()

        /*
        for (attendee: LearnerAssignment in attendees) {
            learners.add(LearnerMonitoringModel(attendee.id,
                                                attendee.learner.getFullname(),
                                                ))
        }
        */

        //  TODO:      learnersMonitoringModel.setLearners()

        return DashboardModel(
            sequence,
            openedPane,
            previousSequence?.id,
            nextSequence?.id,
            learnersMonitoringModel
        )
    }

    private fun getAttendeeStateOnResponsePhase(
        attendee: LearnerAssignment,
        attendeeResponses: List<Response>,
        responsePhaseState: DashboardPhaseState
    ): LearnerStateOnPhase {

        val hasResponseForPhase: Boolean = attendeeResponses.count {
            it.attempt == 1 && it.interaction.interactionType == InteractionType.ResponseSubmission
        } == 1

        return if (responsePhaseState == DashboardPhaseState.NOT_STARTED) {
            LearnerStateOnPhase.WAITING
        } else if (!hasResponseForPhase) {
            LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
        } else {
            LearnerStateOnPhase.ACTIVITY_TERMINATED
        }
    }

    /** @return the LearnerStateOnPhase base on the argument */
    private fun getAttendeeStateOnEvaluationPhase(
        attendee: LearnerAssignment,
        sequence: Sequence,
        evaluationPhaseState: DashboardPhaseState,
        evaluationCountByUser: Map<LearnerAssignment, Int>
    ): LearnerStateOnPhase {

        val nbEvaluationMade = evaluationCountByUser[attendee]

        val hasMadeAllEvaluationForPhase: Boolean =
            sequence.activeInteraction?.interactionType == InteractionType.Evaluation
                    && sequence.getEvaluationSpecification().responseToEvaluateCount == nbEvaluationMade

        return if (evaluationPhaseState == DashboardPhaseState.NOT_STARTED) {
            LearnerStateOnPhase.WAITING
        } else if (hasMadeAllEvaluationForPhase) {
            LearnerStateOnPhase.ACTIVITY_TERMINATED
        } else {
            LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
        }
    }

    private fun getAttendeeStateOnReadPhase(
        sequence: Sequence,
        readPhaseState: DashboardPhaseState,
        attendee: LearnerAssignment,
        evaluationPhaseState: DashboardPhaseState,
        evaluationCountByUser: Map<LearnerAssignment, Int>
    ): LearnerStateOnPhase {

        return if ((sequence.executionIsFaceToFace() || sequence.executionIsBlended())) {
            if (readPhaseState == DashboardPhaseState.IN_PROGRESS) {
                LearnerStateOnPhase.WAITING
            } else {
                LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
            }
        } else if (getAttendeeStateOnEvaluationPhase(
                attendee,
                sequence,
                evaluationPhaseState,
                evaluationCountByUser
            ) == LearnerStateOnPhase.ACTIVITY_TERMINATED
        ) {
            LearnerStateOnPhase.WAITING
        } else {
            LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
        }
    }

    /**
     * Since The StepsModel use different state phase than the
     * LearnersMonitoringModel we need to convert it
     */
    private fun convertPhaseState(state: StepsModel.PhaseState): DashboardPhaseState {
        return when (state) {
            StepsModel.PhaseState.DISABLED -> DashboardPhaseState.NOT_STARTED
            StepsModel.PhaseState.ACTIVE -> DashboardPhaseState.IN_PROGRESS
            StepsModel.PhaseState.COMPLETED -> DashboardPhaseState.COMPLETED
        }
    }

}