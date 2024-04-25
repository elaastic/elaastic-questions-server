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

    fun findAllEvaluation(user: User, sequence: Sequence): List<PeerGrading> =
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
            .setParameter("grader", user)
            .setParameter("interaction", sequence.getResponseSubmissionInteraction())
            .resultList as List<PeerGrading>


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
     * Mark peer grading as hidden by teacher.
     *
     * @param teacher the teacher who hide the peer grading.
     * @param peerGrading the peer grading to hide
     */
    fun markAsHidden(teacher: User, peerGrading: PeerGrading) {
        if (teacher != peerGrading.response.interaction.sequence.owner) {
            throw IllegalAccessException("Only the teacher who own the sequence can hide a peer grading")
        }
        reportCandidateService.markAsHidden(peerGrading, peerGradingRepository)
    }

    /**
     * Mark peer grading as removed by teacher.
     *
     * @param teacher the teacher who remove the peer grading.
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
     * @param reporter the learner who own the response.
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
        if (reporter != peerGrading.response.learner) {
            throw IllegalAccessException("Only the learner who own the response can report a peer grading")
        }
        if (peerGrading is DraxoPeerGrading && peerGrading.getDraxoEvaluation().getExplanation() == null) {
            throw IllegalStateException("You can't report something that doesn't exist. The evaluation doesn't have any comment")
        }
        if (reportReasons.contains(ReportReason.OTHER.name) && reportComment.isNullOrBlank()) {
            throw IllegalArgumentException("You must provide a comment when you report a peer grading for the reason OTHER")
        }
        reportCandidateService.updateReport(peerGrading, reportReasons, reportComment, peerGradingRepository)
    }

    /**
     * Find a Draxo peer grading by its id.
     * @param id the id of the peer grading.
     * @return the Draxo peer grading.
     * @throws IllegalArgumentException if no Draxo peer grading is found with the given id.
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
     * @throws IllegalArgumentException if the learner is not the owner of the response.
     */
    fun updateUtilityGrade(learner: User, peerGrading: PeerGrading, utilityGrade: UtilityGrade) {
        if (learner != peerGrading.response.learner) {
            throw IllegalAccessException("Only the learner who own the response can update the utility grade of a peer grading")
        }
        reportCandidateService.updateGrade(peerGrading, utilityGrade, peerGradingRepository)
    }

    /**
     * Show a peer grading that was hidden by the teacher.
     */
    fun markAsShow(teacher: User, peerGrading: PeerGrading) {
        if (teacher != peerGrading.response.interaction.sequence.owner) {
            throw IllegalAccessException("Only the teacher who own the sequence can show a peer grading")
        }
        reportCandidateService.markAsShown(peerGrading, peerGradingRepository)
    }
}