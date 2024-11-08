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

package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.assignment.LearnerAssignmentService
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingType
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DraxoPeerGradingService(
    @Autowired val draxoPeerGradingRepository: DraxoPeerGradingRepository,
    @Autowired val learnerAssignmentService: LearnerAssignmentService,
    @Autowired val responseService: ResponseService,
    @Autowired val responseRepository: ResponseRepository,
) {

    fun createOrUpdateDraxo(
        grader: User,
        response: Response,
        evaluation: DraxoEvaluation,
        lastSequencePeerGrading: Boolean
    ): DraxoPeerGrading {
        require(learnerAssignmentService.isGraderRegisteredOnAssignment(grader, response)) {
            "You must be registered on the assignment to provide evaluations"
        }

        val peerGrade = draxoPeerGradingRepository.findByGraderAndResponse(grader, response)
            ?: DraxoPeerGrading(grader, response, evaluation, lastSequencePeerGrading)

        require(peerGrade is DraxoPeerGrading) {
            "It already exist a peer grading for this response & this grader but it is not a DRAXO evaluation"
        }

        peerGrade.updateFrom(evaluation)

        val savedPeerGrade = draxoPeerGradingRepository.save(peerGrade)
        responseService.updateMeanGradeAndEvaluationCount(response)

        return savedPeerGrade
    }

    fun findAllDraxo(responses: List<Response>): List<DraxoPeerGrading> =
        draxoPeerGradingRepository
            .findAllByResponseIn(responses)
            .filter { !it.removedByTeacher }

    fun findAllDraxo(sequence: Sequence): List<DraxoPeerGrading> =
        findAllDraxo(
            responseRepository.findAllByInteraction(sequence.getResponseSubmissionInteraction())
        )

    fun findAllDraxo(response: Response): List<DraxoPeerGrading> =
        draxoPeerGradingRepository
            .findAllByResponseAndType(response, PeerGradingType.DRAXO)
            .filter { !it.removedByTeacher }

    /**
     * Find a Draxo peer grading by its id.
     *
     * @param id the id of the peer grading.
     * @return the Draxo peer grading.
     * @throws IllegalArgumentException if no Draxo peer grading is found with
     *    the given id.
     */
    fun getDraxoPeerGrading(id: Long): DraxoPeerGrading =
        draxoPeerGradingRepository.findByIdAndType(id, PeerGradingType.DRAXO)
            ?: error("No Draxo peer grading found with id $id")

    /**
     * Return the list of all the DRAXO peer grading that have been reported
     * and are not hidden by the teacher.
     *
     * @param sequence the sequence
     * @return the list of DRAXO peer grading
     */
    fun findAllDraxoPeerGradingReported(sequence: Sequence, removed: Boolean = false): List<DraxoPeerGrading> =
        draxoPeerGradingRepository.findAllReported(
            sequence.getResponseSubmissionInteraction(),
            removed = removed
        )

    /**
     * @param sequence the sequence
     * @return the number of DRAXO peer grading that have been reported and are
     *    not hidden by the teacher
     */
    fun countAllDraxoPeerGradingReported(sequence: Sequence, removed: Boolean): Int =
        draxoPeerGradingRepository.countAllReported(
            sequence.getResponseSubmissionInteraction(),
            removed
        )

    fun countAllReportedNotHiddenForGrader(interaction: Interaction, grader: User): Int =
        draxoPeerGradingRepository.countAllReportedNotRemovedForGrader(
            interaction,
            grader
        )
}