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
import org.elaastic.questions.assignment.sequence.interaction.specification.EvaluationSpecification
import org.elaastic.questions.player.components.steps.StepsModel
import org.elaastic.questions.player.components.steps.StepsModelFactory
import org.springframework.stereotype.Service

/**
 * Factory to build the [DashboardModel].
 *
 * @see DashboardModel
 */
@Service
object DashboardModelFactory {

    fun build(
        sequence: Sequence,
        previousSequence: Sequence?,
        nextSequence: Sequence?,
        attendees: List<LearnerAssignment>,
        responses: List<Response>,
        openedPane: String,
        evaluationCountByUser: Map<LearnerAssignment, Long>
    ): DashboardModel {

        val learnerStepsModel: StepsModel = StepsModelFactory.buildForTeacher(sequence)

        val sequenceMonitoringModel: SequenceMonitoringModel = SequenceMonitoringModel(
            sequence.executionContext,
            convertPhaseState(learnerStepsModel.responseSubmissionState),
            convertPhaseState(learnerStepsModel.evaluationState),
            convertPhaseState(learnerStepsModel.readState)
        )

        val learners: MutableList<LearnerMonitoringModel> = mutableListOf()

        attendees.forEach {
            learners.add(
                LearnerMonitoringModel(
                    it.id!!,
                    it.learner.getFullname(),
                    getAttendeeStateOnResponsePhase(it, responses, sequenceMonitoringModel.phase1State),
                    getAttendeeStateOnEvaluationPhase(
                        it,
                        sequence,
                        sequenceMonitoringModel.phase2State,
                        evaluationCountByUser
                    ),
                    getAttendeeStateOnReadPhase(
                        sequence,
                        sequenceMonitoringModel.phase3State,
                        it,
                        sequenceMonitoringModel.phase2State,
                        evaluationCountByUser
                    ),
                    sequenceMonitoringModel
                )
            )
        }

        sequenceMonitoringModel.setLearners(learners)

        return DashboardModel(
            sequence,
            openedPane,
            previousSequence?.id,
            nextSequence?.id,
            sequenceMonitoringModel
        )
    }

    /**
     * Get the LearnerStateOnPhase of the attendee on the Response phase.
     *
     * @param attendee the attendee
     * @param attendeeResponses the list of responses of the attendee
     * @param responsePhaseState the state of the response phase
     * @return the state of the attendee on the response phase
     * @see LearnerStateOnPhase
     */
    private fun getAttendeeStateOnResponsePhase(
        attendee: LearnerAssignment,
        attendeeResponses: List<Response>,
        responsePhaseState: DashboardPhaseState
    ): LearnerStateOnPhase {

        val hasResponseForPhase: Boolean = learnerHasAnswer(attendeeResponses, attendee)

        return if (responsePhaseState == DashboardPhaseState.NOT_STARTED) {
            LearnerStateOnPhase.WAITING
        } else if (hasResponseForPhase) {
            LearnerStateOnPhase.ACTIVITY_TERMINATED
        } else {
            LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
        }
    }

    /**
     * Check if among the responses of the sequence, one of them is from the
     * learner and is the first attempt.
     *
     * @param attendeeResponses the list of responses of the attendee
     * @param attendee the attendee
     * @return true if the learner has answered the question, false otherwise
     */
    fun learnerHasAnswer(attendeeResponses: List<Response>, attendee: LearnerAssignment) = attendeeResponses.count {
        it.attempt == 1
                && it.interaction.interactionType == InteractionType.ResponseSubmission
                && it.learner == attendee.learner
    } == 1

    /**
     * Get the LearnerStateOnPhase of the attendee on the Evaluation phase.
     *
     * A learner has finished the phase if he has evaluated exactly the number
     * of responses required.
     *
     * @param attendee the attendee
     * @param sequence the sequence
     * @param evaluationPhaseState the state of the evaluation phase
     * @param evaluationCountByUser the number of evaluations made by each user
     * @return the LearnerStateOnPhase base on the argument
     * @see EvaluationSpecification
     */
    private fun getAttendeeStateOnEvaluationPhase(
        attendee: LearnerAssignment,
        sequence: Sequence,
        evaluationPhaseState: DashboardPhaseState,
        evaluationCountByUser: Map<LearnerAssignment, Long>
    ): LearnerStateOnPhase {

        // To not use any Service in a Factory, we need to pass the evaluationCountByUser as an argument
        val nbEvaluationMade = evaluationCountByUser[attendee]

        val hasMadeAllEvaluationForPhase: Boolean =
            allEvaluationHaveBeenMade(sequence, nbEvaluationMade)

        return if (evaluationPhaseState == DashboardPhaseState.NOT_STARTED) {
            LearnerStateOnPhase.WAITING
        } else if (hasMadeAllEvaluationForPhase) {
            LearnerStateOnPhase.ACTIVITY_TERMINATED
        } else {
            LearnerStateOnPhase.ACTIVITY_NOT_TERMINATED
        }
    }

    /**
     * Check if all the evaluations have been made for the sequence.
     *
     * Compare the number of evaluations made by the user with the number of
     * evaluations to make specified in the sequence.
     *
     * @param sequence the sequence
     * @param nbEvaluationMade the number of evaluations made by the user
     * @return true if all the evaluations have been made, false otherwise
     * @see EvaluationSpecification
     */
    fun allEvaluationHaveBeenMade(
        sequence: Sequence,
        nbEvaluationMade: Long?
    ) = try {
        sequence.getEvaluationSpecification().responseToEvaluateCount.toLong() == nbEvaluationMade
    } catch (e: IllegalStateException) {
        false /* If the sequence isn't initialized an Exception his throw by the getEvaluationSpecification function */
    }

    /**
     * Get the LearnerStateOnPhase of the attendee on the Read phase. (eq. the
     * result phase)
     *
     * By default, the learner has not terminated the activity
     *
     * If the sequence is *Face-to-Face or blended and the read phase is in
     * progress, the learner is waiting.
     *
     * If the sequence is *Distance*, we need to check if the learner has
     * terminated the previous activity, the Evaluation phase. For that we
     * need to get the argument for the [getAttendeeStateOnEvaluationPhase]
     * function.
     *
     * @param sequence the sequence
     * @param readPhaseState the state of the read phase
     * @param attendee the attendee
     * @param evaluationPhaseState the state of the evaluation phase
     * @param evaluationCountByUser the number of evaluations made by each user
     * @return the state of the attendee on the read phase
     * @see LearnerStateOnPhase
     * @see getAttendeeStateOnEvaluationPhase
     */
    private fun getAttendeeStateOnReadPhase(
        sequence: Sequence,
        readPhaseState: DashboardPhaseState,
        attendee: LearnerAssignment,
        evaluationPhaseState: DashboardPhaseState,
        evaluationCountByUser: Map<LearnerAssignment, Long>
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
     * Since The [StepsModel] use different state phase than the
     * [SequenceMonitoringModel], we need to convert the state.
     *
     * @param state the StepsModel.PhaseState to convert
     * @return the converted state in DashboardPhaseState
     * @see [StepsModel.PhaseState]
     * @see DashboardPhaseState
     */
    private fun convertPhaseState(state: StepsModel.PhaseState): DashboardPhaseState {
        return when (state) {
            StepsModel.PhaseState.DISABLED -> DashboardPhaseState.NOT_STARTED
            StepsModel.PhaseState.ACTIVE -> DashboardPhaseState.IN_PROGRESS
            StepsModel.PhaseState.COMPLETED -> DashboardPhaseState.STOPPED
        }
    }

}