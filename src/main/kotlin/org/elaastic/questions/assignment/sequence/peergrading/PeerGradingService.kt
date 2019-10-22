package org.elaastic.questions.assignment.sequence.peergrading

import org.elaastic.questions.assignment.LearnerAssignmentService
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class PeerGradingService(
        @Autowired val peerGradingRepository: PeerGradingRepository,
        @Autowired val learnerAssignmentService: LearnerAssignmentService,
        @Autowired val entityManager: EntityManager
) {

    // TODO (+) we should add a unicity constraint on <user, reponseId> so that we can use INSERT INTO / ON DUPLICATE KEY UPDATE syntax
    fun createOrUpdate(grader: User, response: Response, grade: BigDecimal): PeerGrading {
        require(
                learnerAssignmentService.isRegistered(
                        grader,
                        response.interaction.sequence.assignment ?: error("The response is not bound to an assignment")
                )
        ) { "You must be registered on the assignment to provide evaluations" }

        val peerGrade = peerGradingRepository.findByGraderAndResponse(grader, response)
                ?: PeerGrading(grader = grader, response = response, grade = grade)

        peerGrade.grade = grade
        peerGradingRepository.save(peerGrade)
        return peerGrade
    }

    fun userHasPerformedEvaluation(user: User, sequence: Sequence) =
            entityManager.createQuery("""
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


    fun countEvaluations(sequence: Sequence) =
            countEvaluations(sequence.getResponseSubmissionInteraction())

    fun countEvaluations(interaction: Interaction) =
            (entityManager.createQuery("""
                SELECT COUNT(DISTINCT pg.grader) 
                FROM PeerGrading pg
                WHERE pg.response IN (FROM  Response resp where resp.interaction = :interaction)
            """.trimIndent())
                    .setParameter("interaction", interaction)
                    .singleResult as Long).toInt()
}