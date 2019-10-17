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
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.StatementService
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.results.AttemptNum
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
@Transactional
class ResponseService(
        @Autowired val responseRepository: ResponseRepository,
        @Autowired val learnerAssignmentService: LearnerAssignmentService,
        @Autowired val statementService: StatementService,
        @Autowired val userService: UserService,
        @Autowired val entityManager: EntityManager
) {

    fun findAll(sequence: Sequence): ResponseSet =
            findAll(sequence.getResponseSubmissionInteraction())

    fun findAll(interaction: Interaction): ResponseSet =
            ResponseSet(
                    responseRepository.findAllByInteraction(interaction)
            )

    fun hasResponseForUser(learner: User, sequence: Sequence, attempt: AttemptNum = 1) =
            responseRepository.countByLearnerAndInteractionAndAttempt(
                    learner = learner,
                    interaction = sequence.getResponseSubmissionInteraction(),
                    attempt = attempt
            ) > 0

    // TODO Need to fetch users with responses
    fun findAllChoiceResponse(interaction: Interaction, correct: Boolean, attempt: AttemptNum = 1) {
        if (correct)
            responseRepository.findAllByInteractionAndAttemptAndScoreOrderByScoreDesc(
                    interaction,
                    attempt
            )
        else responseRepository.findAllByInteractionAndAttemptAndScoreLessThanOrderByScoreDesc(
                interaction,
                attempt
        )
    }

    fun findAllOpenResponse(interaction: Interaction, attemptNum: AttemptNum = 1): List<Response> =
            responseRepository.findAllByInteractionAndAttemptOrderByMeanGradeDesc(interaction, attemptNum)


    fun updateMeanGradeAndEvaluationCount(response: Response): Response {
        val res = entityManager.createQuery("select avg(pg.grade) as meanGrade, count(pg.grade) as evaluationCount from PeerGrading pg where pg.response = :response and pg.grade <> -1")
                .setParameter("response", response)
                .singleResult as Array<Object>

        response.meanGrade = if (res[0] != null) {
            BigDecimal(res[0] as Double).setScale(2, RoundingMode.HALF_UP)
        } else null
        response.evaluationCount = if (res[1] != null) (res[1] as Long).toInt() else 0
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
        val attempt = if (sequence.executionIsFaceToFace()) 1 else 2
        val interaction = sequence.getResponseSubmissionInteraction()
        var score: BigDecimal? = null
        val learnerChoice = when (val choiceSpecification = statement.choiceSpecification) {
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
        return responseRepository.save(Response(
                learner = teacher,
                explanation = statement.expectedExplanation,
                confidenceDegree = confidenceDegree.ordinal,
                attempt = attempt,
                interaction = interaction,
                learnerChoice = learnerChoice,
                score = score,
                isAFake = true
        ))
    }


    /**
     * Build  responses from teacher fake explanations
     * @param sequence the sequence
     */
    fun buildResponsesBasedOnTeacherFakeExplanationsForASequence(sequence: Sequence,
                                                                 confidenceDegree: ConfidenceDegree = ConfidenceDegree.CONFIDENT): List<Response> {
        val res = mutableListOf<Response>()
        val statement = sequence.statement
        val explanations = statementService.findAllFakeExplanationsForStatement(statement)
        if (explanations.isNotEmpty()) {
            val attempt = if (sequence.executionIsFaceToFace()) 1 else 2
            val interaction = sequence.getResponseSubmissionInteraction()
            explanations.forEachIndexed { index, fakeExplanation ->
                val fakeLearner = entityManager.merge(userService.fakeUserList!![index % userService.fakeUserList!!.size])
                var score: BigDecimal? = null
                val learnerChoice = if (statement.hasChoices()) {
                    LearnerChoice(listOf(fakeExplanation.correspondingItem!!)).also {
                        score = Response.computeScore(learnerChoice = it, choiceSpecification = statement.choiceSpecification!!)
                    }
                } else null
                responseRepository.save(Response(
                        learner = fakeLearner,
                        explanation = fakeExplanation.content,
                        confidenceDegree = confidenceDegree.ordinal,
                        attempt = attempt,
                        interaction = interaction,
                        learnerChoice = learnerChoice,
                        score = score,
                        isAFake = true
                )).let {
                    res.add(it)
                }
            }
        }
        return res
    }

}
