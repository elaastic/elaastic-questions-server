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

import org.elaastic.common.util.requireAccess
import org.elaastic.moderation.ReportCandidateService
import org.elaastic.moderation.ReportReason
import org.elaastic.moderation.UtilityGrade
import org.elaastic.questions.assignment.LearnerAssignment
import org.elaastic.questions.assignment.LearnerAssignmentService
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.response.Response
import org.elaastic.sequence.interaction.response.ResponseRepository
import org.elaastic.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
import org.elaastic.user.User
import org.elaastic.common.util.requireAccess
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.stereotype.Service
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.persistence.Tuple
import javax.transaction.Transactional

@Service
@Transactional
class PeerGradingService(
    @Autowired val reportCandidateService: ReportCandidateService,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val responseService: ResponseService,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val learnerAssignmentService: LearnerAssignmentService,
    @Autowired val entityManager: EntityManager,
) {

    fun createOrUpdateLikert(grader: User, response: Response, grade: BigDecimal): LikertPeerGrading {
        require(learnerAssignmentService.isGraderRegisteredOnAssignment(grader, response)) {
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
        """.trimIndent(), PeerGrading::class.java
        )
            .setParameter("grader", grader)
            .setParameter("interaction", sequence.getResponseSubmissionInteraction())
            .resultList as List<PeerGrading>

    /**
     * Count the number of evaluations made by a list of users on a sequence.
     *
     * If a user has not made any evaluation, 0 is returned. If the sequence is
     * not initialized, 0 is returned for all users.
     *
     * To avoid N+1 Select, we didn't use the method
     * [countEvaluationsMadeByUser].
     *
     * @param users the list of users who performed the evaluations.
     * @param sequence the sequence.
     */
    @EntityGraph("PeerGrading.with_grader_and_response", type = EntityGraph.EntityGraphType.LOAD)
    fun countEvaluationsMadeByUsers(users: List<LearnerAssignment>, sequence: Sequence): Map<LearnerAssignment, Long> {
        val graderWithEvaluationCount: MutableMap<LearnerAssignment, Long> =
            emptyMap<LearnerAssignment, Long>().toMutableMap()

        return try {
            val queryResult = entityManager.createQuery(
                """
            SELECT pg.grader, count(pg)
            FROM PeerGrading pg
            WHERE pg.response IN (
                FROM Response resp
                WHERE resp.interaction = :interaction
            )
            GROUP BY pg.grader
        """.trimIndent(), Tuple::class.java
            )
                .setParameter("interaction", sequence.getResponseSubmissionInteraction())
                .resultList as List<Tuple>

            // For each given student, we associate it with the number of evaluations he made
            // If the student isn't found in the query result, we associate it with 0
            for (learner in users) {
                val tupleResult = queryResult.find { it[0] == learner.learner }
                graderWithEvaluationCount += if (tupleResult == null) {
                    learner to 0
                } else {
                    learner to tupleResult[1] as Long
                }
            }
            graderWithEvaluationCount
        } catch (_: IllegalStateException) {
            /* If the sequence isn't initialized an Exception his throw by the getEvaluationSpecification function
               If the sequence isn't initialized, that means the users haven't made any evaluation */
            users.associateWith { 0 } // No evaluation made by any user
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

    /**
     * Find all the evaluations made on a sequence at a specific attempt. We
     * retrieve all the responses of the sequence at the given attempt, and
     * then we retrieve all the peer grading that have been made on these
     * responses.
     *
     * @param sequence the sequence.
     * @param attempt the attempt.
     * @return the list of peer grading.
     */
    fun findAllByAttempt(sequence: Sequence, attempt: Int): List<PeerGrading> =
        peerGradingRepository.findAllByResponseIn(
            responseRepository.findAllByInteractionAndAttempt(
                sequence.getResponseSubmissionInteraction(),
                attempt
            )
        )

    /**
     * Find all the evaluations made on a sequence. We retrieve all the
     * responses of the sequence, and then we retrieve all the peer grading
     * that have been made on these responses.
     *
     * @param sequence the sequence.
     * @return the list of peer grading.
     */
    fun findAll(sequence: Sequence): List<PeerGrading> =
        peerGradingRepository.findAllByResponseIn(
            responseRepository.findAllByInteraction(sequence.getResponseSubmissionInteraction())
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
        if (peerGrading is DraxoPeerGrading) peerGrading.response.draxoEvaluationHiddenCount++
        responseService.updateMeanGradeAndEvaluationCount(peerGrading.response)
    }

    /**
     * Mark peer grading as removed by teacher.
     *
     * @param teacher the teacher who removes the peer grading.
     * @param peerGrading the peer grading to remove.
     */
    fun markAsRemoved(teacher: User, peerGrading: PeerGrading) {
        requireAccess(teacher == peerGrading.response.interaction.sequence.owner) {
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
     * Update the utility grade of a peer grading.
     *
     * @param learner the learner who owns the response.
     * @param peerGrading the draxo peer grading to update.
     * @param utilityGrade the utility grade.
     * @throws IllegalArgumentException if the learner is not the owner of the
     *    response.
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
     * @param teacher the user who wants to hide the peer grading
     * @param peerGrading the peer grading to hide
     * @return true if the user can hide the peer grading
     */
    fun canHidePeerGrading(teacher: User, peerGrading: PeerGrading): Boolean {
        return responseService.canHidePeerGrading(teacher, peerGrading.response)
    }

    /**
     * Return all the DRAXO evaluation another user made to the learner
     * response of the sequence.
     *
     * @param user the user
     * @param sequence the sequence
     * @return the list of evaluation
     */
    fun findAllEvaluationMadeForLearner(user: User, sequence: Sequence): List<PeerGrading> {
        return entityManager.createQuery(
            """
            SELECT pg
            FROM PeerGrading pg
            WHERE pg.response IN (
                FROM Response resp
                WHERE resp.interaction = :interaction 
                      AND resp.learner = :learner
            )
        """.trimIndent(), PeerGrading::class.java
        )
            .setParameter("interaction", sequence.getResponseSubmissionInteraction())
            .setParameter("learner", user)
            .resultList as List<PeerGrading>
    }

    /**
     * Return a map with the learner and a boolean indicating if the learner
     * answered the sequence.
     *
     * @param registeredUsers the list of learners
     * @param sequence the sequence
     * @return the map with the learner and a boolean indicating if the learner
     *    answered the sequence
     */
    fun learnerToIfTheyAnswer(
        registeredUsers: List<LearnerAssignment>,
        sequence: Sequence
    ): Map<LearnerAssignment, Boolean> {
        val learnersWhoAnswered = entityManager.createQuery(
            """
            SELECT response.learner, response
            FROM Response response
            JOIN User user ON response.learner = user
            WHERE response.interaction = :interaction
            """.trimIndent(), Tuple::class.java
        )
            .setParameter("interaction", sequence.getResponseSubmissionInteraction())
            .resultList as List<Tuple>

        return registeredUsers.associateWith {
            learnersWhoAnswered.any { tuple -> tuple[0] == it.learner }
        }
    }

    /**
     * Remove the report of a peer grading.
     *
     * @param user the user who wants to remove the report
     * @param peerGrading the peer grading to update
     * @throws IllegalArgumentException if the user is not the owner of the response
     */
    fun removeReport(
        user: User,
        peerGrading: PeerGrading
    ) {
        requireAccess(user == peerGrading.response.interaction.owner) {
            "Only the teacher who own the sequence can remove a report"
        }

        peerGrading.reportReasons = null
        peerGrading.reportComment = null

        peerGradingRepository.save(peerGrading)
    }

    fun removeReport(user: User, id: Long) {
        val peerGrading = peerGradingRepository.findById(id).orElseThrow()
        removeReport(user, peerGrading)
    }

    fun markAsRestored(user: User, peerGrading: PeerGrading) {
        requireAccess(user == peerGrading.response.interaction.owner) {
            "Only the teacher who own the sequence can restore a peer grading"
        }
        reportCandidateService.markAsRestored(peerGrading, peerGradingRepository)
    }
}