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

package org.elaastic.questions.assignment.sequence.peergrading

import org.elaastic.questions.assignment.LearnerAssignmentService
import org.elaastic.questions.assignment.sequence.ReportReason
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.report.ReportCandidateService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
import org.elaastic.questions.directory.User
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.util.requireAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class PeerGradingService(
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val responseService: ResponseService,
    @Autowired val learnerAssignmentService: LearnerAssignmentService,
    @Autowired val reportCandidateService: ReportCandidateService,
    @Autowired val entityManager: EntityManager
) {

    fun createOrUpdateLikert(grader: User, response: Response, grade: BigDecimal): LikertPeerGrading {
        require(isGraderRegisteredOnAssignment(grader, response)) {
            "You must be registered on the assignment to provide evaluations"
        }

        val peerGrade = peerGradingRepository.findByGraderAndResponse(grader, response)
            ?: LikertPeerGrading(grader = grader, response = response, grade = grade)

        require(peerGrade is LikertPeerGrading) {
            "It already exist a peer grading for this response & this grader but it is not a LIKERT evaluation"
        }

        peerGrade.grade = grade
        val savedPeerGrade = peerGradingRepository.save(peerGrade)
        responseService.updateMeanGradeAndEvaluationCount(response)

        return savedPeerGrade
    }

    fun createOrUpdateDraxo(
        grader: User,
        response: Response,
        evaluation: DraxoEvaluation,
        lastSequencePeerGrading: Boolean
    ): DraxoPeerGrading {
        require(isGraderRegisteredOnAssignment(grader, response)) {
            "You must be registered on the assignment to provide evaluations"
        }

        val peerGrade = peerGradingRepository.findByGraderAndResponse(grader, response)
            ?: DraxoPeerGrading(grader, response, evaluation, lastSequencePeerGrading)

        require(peerGrade is DraxoPeerGrading) {
            "It already exist a peer grading for this response & this grader but it is not a DRAXO evaluation"
        }

        peerGrade.updateFrom(evaluation)

        val savedPeerGrade = peerGradingRepository.save(peerGrade)
        responseService.updateMeanGradeAndEvaluationCount(response)

        return savedPeerGrade
    }

    fun findAllDraxo(response: Response): List<DraxoPeerGrading> =
        peerGradingRepository.findAllByResponseAndType(response, PeerGradingType.DRAXO)

    fun isGraderRegisteredOnAssignment(grader: User, response: Response) =
        learnerAssignmentService.isRegistered(
            grader,
            response.interaction.sequence.assignment ?: error("The response is not bound to an assignment")
        )

    fun userHasPerformedEvaluation(user: User, sequence: Sequence) =
        entityManager.createQuery(
            """
                SELECT COUNT(*) 
                FROM PeerGrading pg 
                WHERE pg.grader = :grader 
                    AND pg.response IN (
                        FROM Response resp 
                        WHERE resp.interaction = :interaction
                    )
            """.trimIndent()
        )
            .setParameter("grader", user)
            .setParameter("interaction", sequence.getResponseSubmissionInteraction())
            .singleResult as Long > 0

    /**
     * Find all the evaluations made by a user on a sequence.
     *
     * @param grader the user who performed the evaluations.
     * @param sequence the sequence.
     * @return the list of evaluations.
     */
    fun findAllEvaluation(grader: User, sequence: Sequence): List<PeerGrading> =
        entityManager.createQuery(
            """
            SELECT pg
            FROM PeerGrading  pg
            WHERE pg.grader = :grader
                AND pg.response IN (
                    FROM Response resp
                    WHERE resp.interaction = :interaction
                )
        """.trimIndent()
        )
            .setParameter("grader", grader)
            .setParameter("interaction", sequence.getResponseSubmissionInteraction())
            .resultList as List<PeerGrading>

    /**
     * Count the number of evaluations made by a user on a sequence.
     *
     * If the user has not made any evaluation, 0 is returned.
     *
     * @param grader the user who performed the evaluations.
     * @param sequence the sequence.
     * @return the number of evaluations.
     */
    fun countEvaluationsMadeByUser(grader: User, sequence: Sequence): Long {
        return try {
            entityManager.createQuery(
                """
            SELECT count(*)
            FROM PeerGrading  pg
            WHERE pg.grader = :grader
                AND pg.response IN (
                    FROM Response resp
                    WHERE resp.interaction = :interaction
                )
        """.trimIndent()
            )
                .setParameter("grader", grader)
                .setParameter("interaction", sequence.getResponseSubmissionInteraction())
                .singleResult as Long
        } catch (e: IllegalStateException) {
            /* If the sequence isn't initialized an Exception his throw by the getEvaluationSpecification function
               If the sequence isn't initialized, that means the user has not made any evaluation */
            0
        }
    }

    fun countEvaluations(sequence: Sequence) =
        countEvaluations(sequence.getResponseSubmissionInteraction())

    fun countEvaluations(interaction: Interaction) =
        (entityManager.createQuery(
            """
                SELECT COUNT(DISTINCT pg.grader) 
                FROM PeerGrading pg
                WHERE pg.response IN (FROM  Response resp where resp.interaction = :interaction) AND pg.lastSequencePeerGrading IS TRUE 
            """.trimIndent()
        )
            .setParameter("interaction", interaction)
            .singleResult as Long).toInt()

    fun findAll(sequence: Sequence): List<PeerGrading> =
        peerGradingRepository.findAllByResponseIn(
            responseRepository.findAllByInteractionAndAttempt(
                sequence.getResponseSubmissionInteraction(),
                1
            )
        )

    /**
     * Mark peer grading as hidden by teacher. If the user can't hide the peer
     * grading, an exception is thrown
     *
     * @param teacher the teacher who hides the peer grading.
     * @param peerGrading the peer grading to hide
     */
    fun markAsHidden(teacher: User, peerGrading: PeerGrading) {
        if (canHidePeerGrading(teacher, peerGrading).not()) {
            throw IllegalAccessException("Only the teacher who own the sequence can hide a peer grading")
        }
        reportCandidateService.markAsHidden(peerGrading, peerGradingRepository)
        peerGrading.response.draxoEvaluationHiddenCount++
        responseService.updateMeanGradeAndEvaluationCount(peerGrading.response)
    }

    /**
     * Mark peer grading as removed by teacher.
     *
     * @param teacher the teacher who removes the peer grading.
     * @param peerGrading the peer grading to remove.
     */
    fun markAsRemoved(teacher: User, peerGrading: PeerGrading) {
        require(teacher == peerGrading.response.interaction.sequence.owner) {
            "Only the teacher who own the sequence can remove a peer grading"
        }
        reportCandidateService.markAsRemoved(peerGrading, peerGradingRepository)
    }

    /**
     * Update the report of a peer grading.
     *
     * @param reporter the learner who owns the response.
     * @param peerGrading the peer grading to update.
     * @param reportReasons the list of report.
     * @param reportComment the comment of the report.
     */
    fun updateReport(
        reporter: User,
        peerGrading: PeerGrading,
        reportReasons: List<String>,
        reportComment: String? = null
    ) {
        requireAccess(reporter == peerGrading.response.learner) {
            "Only the learner who own the response can report a peer grading"
        }
        check(!(peerGrading is DraxoPeerGrading && peerGrading.getDraxoEvaluation().getExplanation() == null)) {
            "You can't report something that doesn't exist. The evaluation doesn't have any comment"
        }
        require(!(reportReasons.contains(ReportReason.OTHER.name) && reportComment.isNullOrBlank())) {
            "You must provide a comment when you report a peer grading for the reason OTHER"
        }

        reportCandidateService.updateReport(peerGrading, reportReasons, reportComment, peerGradingRepository)
    }

    /**
     * Find a Draxo peer grading by its id.
     *
     * @param id the id of the peer grading.
     * @return the Draxo peer grading.
     * @throws IllegalArgumentException if no Draxo peer grading is found with
     *     the given id.
     */
    fun getDraxoPeerGrading(id: Long): DraxoPeerGrading =
        peerGradingRepository.findByIdAndType(id, PeerGradingType.DRAXO)
            ?: error("No Draxo peer grading found with id $id")

    /**
     * Update the utility grade of a peer grading.
     *
     * @param learner the learner who own the response.
     * @param peerGrading the draxo peer grading to update.
     * @param utilityGrade the utility grade.
     * @throws IllegalArgumentException if the learner is not the owner of the
     *     response.
     */
    fun updateUtilityGrade(learner: User, peerGrading: PeerGrading, utilityGrade: UtilityGrade) {
        requireAccess(learner == peerGrading.response.learner) {
            "Only the learner who own the response can update the utility grade of a peer grading"
        }
        reportCandidateService.updateGrade(peerGrading, utilityGrade, peerGradingRepository)
    }

    /** Show a peer grading that was hidden by the teacher. */
    fun markAsShow(teacher: User, peerGrading: PeerGrading) {
        requireAccess(canHidePeerGrading(teacher, peerGrading)) {
            "Only the teacher who own the sequence can show a peer grading"
        }

        reportCandidateService.markAsShown(peerGrading, peerGradingRepository)
        peerGrading.response.draxoEvaluationHiddenCount--
        responseService.updateMeanGradeAndEvaluationCount(peerGrading.response)
    }

    /**
     * Return true if the user can hide the peer grading An user can hide the
     * peer grading if he is the owner of the assigment
     *
     * @param teacher the user who want to hide the peer grading
     * @param peerGrading the peer grading to hide
     * @return true if the user can hide the peer grading
     */
    fun canHidePeerGrading(teacher: User, peerGrading: PeerGrading): Boolean {
        return responseService.canHidePeerGrading(teacher, peerGrading.response)
    }

    /**
     * Return all the evaluation another user made to the learner response of
     * the sequence
     *
     * @param user the user
     * @param sequence the sequence
     * @param attempt the attempt
     * @return the list of evaluation
     */
    fun findAllEvaluationMadeForLearner(user: User, sequence: Sequence, attempt: Int = 1): List<DraxoPeerGrading> {
        val peerGrading = emptyList<DraxoPeerGrading>()
        val peerGradingFind = responseRepository.findByInteractionAndAttemptAndLearner(
            sequence.getResponseSubmissionInteraction(),
            attempt,
            user
        )?.let {
            peerGradingRepository.findAllByResponseAndType(
                it,
                PeerGradingType.DRAXO
            )
        }
        return peerGradingFind ?: peerGrading
    }
}