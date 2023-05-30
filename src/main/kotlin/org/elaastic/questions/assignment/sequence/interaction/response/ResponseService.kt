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

package org.elaastic.questions.assignment.sequence.interaction.response

import org.elaastic.questions.assignment.LearnerAssignmentService
import org.elaastic.questions.assignment.ia.ResponseRecommendationService
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class ResponseService(
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val learnerAssignmentService: LearnerAssignmentService,
    @Autowired val entityManager: EntityManager,
    @Autowired val recommendationService: ResponseRecommendationService,
    @Autowired val statementService: StatementService,
    @Autowired val userService: UserService
) {

    fun getOne(id: Long) = responseRepository.getOne(id)

    fun findAll(sequence: Sequence, excludeFakes: Boolean = true): ResponseSet =
        findAll(sequence.getResponseSubmissionInteraction(), excludeFakes)

    fun findAll(interaction: Interaction, excludeFakes: Boolean = true): ResponseSet =
        ResponseSet(
            if (excludeFakes)
                responseRepository.findAllByInteractionAndFakeIsFalseOrderByMeanGradeDesc(interaction)
            else responseRepository.findAllByInteractionOrderByMeanGradeDesc(interaction)
        )

    fun find3BestRankedResponses(sequence: Sequence): Triple<Response?, Response?, Response?> =
        responseRepository.findAllByInteractionAndScoreAndFakeIsFalseOrderByMeanGradeDesc(
            sequence.getResponseSubmissionInteraction()
        ).let { return Triple(it.getOrNull(0), it.getOrNull(1), it.getOrNull(2)) }

    fun count(sequence: Sequence, attempt: AttemptNum) =
        count(sequence.getResponseSubmissionInteraction(), attempt)

    fun count(interaction: Interaction, attempt: AttemptNum) =
        responseRepository.countByInteractionAndAttemptAndFakeIsFalse(interaction, attempt)

    fun findAllRecommandedResponsesForUser(sequence: Sequence, user: User, attempt: AttemptNum): List<Response> =
        if (sequence.executionIsFaceToFace()) {
            // TODO (+) We should index the recommended explanations by userId to that we don't need to get the userResponse to find its recommendations
            responseRepository.findByInteractionAndAttemptAndLearner(
                sequence.getResponseSubmissionInteraction(),
                1,
                user
            )?.let { userResponse ->
                sequence.getResponseSubmissionInteraction().explanationRecommendationMapping?.getRecommandation(
                    userResponse.id!!
                )?.let { responseRepository.getAllByIdIn(it) }
            } ?: listOf<Response>()

        } else recommendationService.findAllResponsesOrderedByEvaluationCount(
            interaction = sequence.getResponseSubmissionInteraction(),
            attemptNum = attempt,
            limit = sequence.getEvaluationSpecification().responseToEvaluateCount
        )

    fun hasResponseForUser(learner: User, sequence: Sequence, attempt: AttemptNum = 1) =
        responseRepository.countByLearnerAndInteractionAndAttempt(
            learner = learner,
            interaction = sequence.getResponseSubmissionInteraction(),
            attempt = attempt
        ) > 0

    fun find(learner: User, sequence: Sequence, attempt: AttemptNum = 1) =
        responseRepository.findByInteractionAndAttemptAndLearner(
            sequence.getResponseSubmissionInteraction(),
            attempt,
            learner
        )


    /**
     *  Update the mean grade and nb of evaluations for every responses
     *  bound the provided interaction
     */
    fun updateGradings(sequence: Sequence) {
        entityManager.createNativeQuery(
            """
            UPDATE choice_interaction_response response
            INNER JOIN (
                    SELECT pg.response_id as rid,
                           avg(pg.grade) as meanGrade,
                           count(pg.grade) as evaluationCount
                    FROM peer_grading pg WHERE pg.grade <> -1
                    GROUP BY pg.response_id
                ) data ON rid = response.id
            SET
                mean_grade = data.meanGrade,
                evaluation_count = data.evaluationCount
            WHERE response.interaction_id = :interactionId
                AND response.attempt = :attempt
        """.trimIndent()
        )
            .setParameter("interactionId", sequence.getResponseSubmissionInteraction().id)
            .setParameter("attempt", sequence.whichAttemptEvaluate())
            .executeUpdate()
    }

    fun updateMeanGradeAndEvaluationCount(response: Response): Response {
        val res =
            entityManager.createQuery("select avg(pg.grade) as meanGrade, count(pg.grade) as evaluationCount from PeerGrading pg where pg.response = :response and pg.grade <> -1")
                .setParameter("response", response)
                .singleResult as Array<Object?>

        response.meanGrade = res[0]?.let { BigDecimal(it as Double).setScale(2, RoundingMode.HALF_UP) }
        response.evaluationCount = res[1]?.let { (it as Long).toInt() } ?: 0

        return responseRepository.save(response)
    }

    fun save(userActiveInteraction: Interaction, response: Response): Response {
        require(
            learnerAssignmentService.isRegistered(
                response.learner,
                response.interaction.sequence.assignment!!
            )
        ) { "You must be registered on the assignment to submit a response" }
        require(run {
            userActiveInteraction.isResponseSubmission() &&
                    userActiveInteraction.state == State.show &&
                    response.attempt == 1
        } ||
                run {
                    userActiveInteraction.isEvaluation() &&
                            userActiveInteraction.state == State.show &&
                            response.attempt == 2
                }

        ) { "The interaction cannot receive response" }

        responseRepository.save(response)
        return response
    }

    /**
     * Build response from teacher expected explanation
     * @param teacher the teacher
     * @param sequence the sequence
     */
    fun buildResponseBasedOnTeacherExpectedExplanationForASequence(
        sequence: Sequence,
        teacher: User,
        confidenceDegree: ConfidenceDegree = ConfidenceDegree.CONFIDENT
    ): Response? {
        val statement = sequence.statement
        if (statement.expectedExplanation.isNullOrBlank()) {
            return null
        }
        val attempt = sequence.whichAttemptEvaluate()
        val interaction = sequence.getResponseSubmissionInteraction()
        var score: BigDecimal? = null
        val learnerChoice = statement.choiceSpecification.let { choiceSpecification ->
            when (choiceSpecification) {
                is ExclusiveChoiceSpecification -> {
                    LearnerChoice(listOf(choiceSpecification.expectedChoice.index)).also {
                        score = Response.computeScore(it, choiceSpecification)
                    }
                }

                is MultipleChoiceSpecification -> {
                    LearnerChoice(choiceSpecification.expectedChoiceList.map { it.index }).also {
                        score = Response.computeScore(it, choiceSpecification)
                    }
                }

                else -> {
                    null
                }
            }
        }
        return responseRepository.save(
            Response(
                learner = teacher,
                explanation = statement.expectedExplanation,
                confidenceDegree = confidenceDegree,
                attempt = attempt,
                interaction = interaction,
                learnerChoice = learnerChoice,
                score = score,
                fake = true,
                statement = statement
            )
        )
    }


    /**
     * Build  responses from teacher fake explanations
     * @param sequence the sequence
     */
    fun buildResponsesBasedOnTeacherFakeExplanationsForASequence(
        sequence: Sequence,
        confidenceDegree: ConfidenceDegree = ConfidenceDegree.CONFIDENT
    ): List<Response> {
        val res = mutableListOf<Response>()
        val statement = sequence.statement
        val explanations = statementService.findAllFakeExplanationsForStatement(statement)
        if (explanations.isNotEmpty()) {
            val attempt = sequence.whichAttemptEvaluate()
            val interaction = sequence.getResponseSubmissionInteraction()
            explanations.forEachIndexed { index, fakeExplanation ->
                val fakeLearner =
                    entityManager.merge(userService.fakeUserList!![index % userService.fakeUserList!!.size])
                var score: BigDecimal? = null
                val learnerChoice = if (statement.hasChoices()) {
                    LearnerChoice(listOf(fakeExplanation.correspondingItem!!)).also {
                        score = Response.computeScore(
                            learnerChoice = it,
                            choiceSpecification = statement.choiceSpecification!!
                        )
                    }
                } else null
                responseRepository.save(
                    Response(
                        learner = fakeLearner,
                        explanation = fakeExplanation.content,
                        confidenceDegree = confidenceDegree,
                        attempt = attempt,
                        interaction = interaction,
                        learnerChoice = learnerChoice,
                        score = score,
                        fake = true,
                        statement = statement
                    )
                ).let {
                    res.add(it)
                }
            }
        }
        return res
    }

}
