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
import javax.persistence.EntityNotFoundException
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

    fun getReferenceById(id: Long) = responseRepository.getReferenceById(id)

    /**
     * It will immediately throw an exception if it does not exists (while getReferenceById will delay the query
     * and so the eventual exception)
     */
    fun findById(id: Long) = responseRepository.findById(id).orElseThrow {
        EntityNotFoundException("There is no response with id='$id'")
    }

    fun findAll(sequence: Sequence, excludeFakes: Boolean = true): ResponseSet =
        findAll(sequence.getResponseSubmissionInteraction(), excludeFakes)

    fun findAll(interaction: Interaction, excludeFakes: Boolean = true): ResponseSet =
        ResponseSet(
            if (excludeFakes)
                responseRepository.findAllByInteractionAndFakeIsFalseOrderByMeanGradeDesc(interaction)
            else responseRepository.findAllByInteractionOrderByMeanGradeDesc(interaction)
        )

    fun find3BestRankedResponses(sequence: Sequence): Triple<Response?, Response?, Response?> =
        responseRepository.findTop3ByInteractionAndScoreAndFakeIsFalseAndHiddenByTeacherIsFalseOrderByMeanGradeDesc(
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
            } ?: listOf()

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
        // TODO Attempt to deactivate this global stat update in favor of micro update a each peer grading deposit ; this code is kept in comment the time to check everything is working properly
//        entityManager.createNativeQuery(
//            """
//            UPDATE choice_interaction_response response
//            INNER JOIN (
//                    SELECT pg.response_id as rid,
//                           avg(pg.grade) as meanGrade,
//                           count(pg.id) as evaluationCount,
//                           sum(IF(pg.`type` = 'DRAXO', 1, 0)) as draxoEvaluationCount
//                    FROM peer_grading pg
//                    GROUP BY pg.response_id
//                ) data ON rid = response.id
//            SET
//                mean_grade = data.meanGrade,
//                evaluation_count = data.evaluationCount,
//                draxo_evaluation_count = data.draxoEvaluationCount
//            WHERE response.interaction_id = :interactionId
//                AND response.attempt = :attempt
//        """.trimIndent()
//        )
//            .setParameter("interactionId", sequence.getResponseSubmissionInteraction().id)
//            .setParameter("attempt", sequence.whichAttemptEvaluate())
//            .executeUpdate()
    }

    fun updateMeanGradeAndEvaluationCount(response: Response): Response {
        // TODO Update the stats in one single query
        val res =
            entityManager.createQuery("select avg(pg.grade) as meanGrade, count(pg.id) as evaluationCount, sum(CASE WHEN pg.type = 'DRAXO' THEN 1 ELSE 0 END) from PeerGrading pg where pg.response = :response")
                .setParameter("response", response)
                .singleResult as Array<Any?>

        response.meanGrade = res[0]?.let { BigDecimal(it as Double).setScale(2, RoundingMode.HALF_UP) }
        response.evaluationCount = res[1]?.let { (it as Long).toInt() } ?: 0
        response.draxoEvaluationCount  = res[2]?.let { (it as Long).toInt() } ?: 0

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

    /**
     * Mark a response as hidden by a teacher
     * @param response the response to hide
     * @return the response
     */
    fun hideResponse(user: User, response: Response) : Response {
        // Only a teacher can hide a response
        require(user.isTeacher()) {
            "Only a teacher can hide a response"
        }

        if (!response.hiddenByTeacher) {
            // A response hidden must not be favourite
            if (response.favourite) {
                response.favourite = false
            }
            response.hiddenByTeacher = true
            return responseRepository.save(response)
        }
        return response
    }

    /**
     * Mark a response as NOT hidden by a teacher
     * @param response the previously hidden response to show again
     * @return the response
     */
    fun unhideResponse(user: User, response: Response) : Response {
        // Only a teacher can unhide a response
        require(user.isTeacher()) {
            "Only a teacher can unhide a response"
        }

        if (response.hiddenByTeacher) {
            response.hiddenByTeacher = false
            return responseRepository.save(response)
        }
        return response
    }

    /**
     * Mark a response as favourite by a teacher
     * @param response the response to add as favourite
     * @return the response
     */
    fun addFavourite(user: User, response: Response) : Response {
        // Only a teacher can add a response as favourite
        require(user.isTeacher()) {
            "Only a teacher can unhide a response"
        }

        if (!response.favourite && !response.hiddenByTeacher) {
            response.favourite = true
            return responseRepository.save(response)
        }
        return response
    }

    /**
     * Mark a response as NOT favourite by a teacher
     * @param response the previously favourite response
     * @return the response
     */
    fun removeFavourite(user: User, response: Response) : Response {
        // Only a teacher can remove a response as favourite
        require(user.isTeacher()) {
            "Only a teacher can unhide a response"
        }

        if (response.favourite) {
            response.favourite = false
            return responseRepository.save(response)
        }
        return response
    }
}
