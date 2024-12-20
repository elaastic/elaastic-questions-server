package org.elaastic.activity.response

import org.elaastic.activity.results.AttemptNum
import org.elaastic.sequence.ExecutionContext
import org.elaastic.assignment.LearnerAssignmentService
import org.elaastic.material.instructional.question.ExclusiveChoiceSpecification
import org.elaastic.material.instructional.question.MultipleChoiceSpecification
import org.elaastic.material.instructional.question.legacy.LearnerChoice
import org.elaastic.activity.evaluation.peergrading.ResponseRecommendationService
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.material.instructional.statement.StatementService
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.user.User
import org.elaastic.user.UserService
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
     * It will immediately throw an exception if it does not exists (while
     * getReferenceById will delay the query and so the eventual exception)
     */
    fun findById(id: Long): Response = responseRepository.findById(id).orElseThrow {
        EntityNotFoundException("There is no response with id='$id'")
    }

    /**
     * Return the response with the given id. It will return the last attempt
     * if it's exist
     *
     * @param id the id of the response to find
     * @return the response with the given id, or the last attempt if it exists
     */
    fun findByIdLastAttempt(id: Long): Response {
        val response = findById(id)
        val secondAttempt = responseRepository.findByInteractionAndAttemptAndLearner(
            response.interaction,
            2,
            response.learner
        )
        return secondAttempt ?: response
    }

    fun findAll(sequence: Sequence, excludeFakes: Boolean = true): ResponseSet =
        findAll(sequence.getResponseSubmissionInteraction(), excludeFakes)

    fun findAll(interaction: Interaction, excludeFakes: Boolean = true): ResponseSet =
        ResponseSet(
            if (excludeFakes)
                responseRepository.findAllByInteractionAndFakeIsFalseOrderByMeanGradeDesc(interaction)
            else responseRepository.findAllByInteractionOrderByMeanGradeDesc(interaction)
        )

    fun findRecommendedByTeacherResponses(sequence: Sequence): List<Response> =
        responseRepository.findAllByInteractionAndRecommendedByTeacherIsTrue(
            sequence.getResponseSubmissionInteraction()
        )

    fun count(sequence: Sequence, attempt: AttemptNum) =
        count(sequence.getResponseSubmissionInteraction(), attempt)

    /**
     * @param interaction
     * @param attempt
     * @return the number of response made in an [Interaction] and it's not [Response.fake] with the given [Response.attempt]
     */
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
                sequence.getResponseSubmissionInteraction().peerEvaluationMapping?.getRecommandation(
                    userResponse.id!!
                )?.let { responseRepository.getAllByIdIn(it) }
            } ?: listOf()

        } else recommendationService.findAllResponsesOrderedByEvaluationCount(
            evaluator = user,
            interaction = sequence.getResponseSubmissionInteraction(),
            attemptNum = attempt,
            limit = sequence.getEvaluationSpecification().responseToEvaluateCount
        )

    fun findNextResponseToGrade(
        sequence: Sequence,
        user: User,
        attempt: AttemptNum,
        excludedIds: List<Long>
    ) =
        if (sequence.executionIsFaceToFace()) {
            val recommendedResponses = responseRepository.findByInteractionAndAttemptAndLearner(
                sequence.getResponseSubmissionInteraction(),
                1,
                user
            )?.let { userResponse ->
                sequence.getResponseSubmissionInteraction().peerEvaluationMapping?.getRecommandation(
                    userResponse.id!!
                )?.let { responseRepository.getAllByIdIn(it) }
            } ?: listOf()
            recommendedResponses.firstOrNull { it.id !in excludedIds }
        } else recommendationService.findAllResponsesOrderedByEvaluationCount(
            evaluator = user,
            interaction = sequence.getResponseSubmissionInteraction(),
            attemptNum = attempt,
            excludedIds = excludedIds,
            limit = 1
        ).firstOrNull()

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
     * Update the mean grade and nb of evaluations for every responses bound
     * the provided interaction
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
            entityManager.createQuery("select count(pg.id) as evaluationCount, sum(CASE WHEN pg.type = 'DRAXO' THEN 1 ELSE 0 END) from PeerGrading pg where pg.response = :response")
                .setParameter("response", response)
                .singleResult as Array<Any?>

        response.evaluationCount = res[0]?.let { (it as Long).toInt() } ?: 0
        response.draxoEvaluationCount = res[1]?.let { (it as Long).toInt() } ?: 0

        val meangrade = entityManager.createQuery(
            "SELECT AVG(pg.grade) " +
                    "FROM PeerGrading pg " +
                    "WHERE pg.response = :response AND pg.hiddenByTeacher = false AND pg.removedByTeacher = false"
        )
            .setParameter("response", response)
            .singleResult as Double?
        response.meanGrade = meangrade?.let { BigDecimal(it).setScale(2, RoundingMode.HALF_UP) }

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
     *
     * @param sequence the sequence
     * @param teacher the teacher
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
     * Build responses from teacher fake explanations
     *
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
     *
     * @param response the response to hide
     * @return the response
     */
    fun hideResponse(user: User, response: Response): Response {
        // Only a teacher can hide a response
        require(user.isTeacher()) {
            "Only a teacher can hide a response"
        }

        if (!response.hiddenByTeacher) {
            // A response hidden must not be recommended
            if (response.recommendedByTeacher) {
                response.recommendedByTeacher = false
            }
            response.hiddenByTeacher = true
            return responseRepository.save(response)
        }
        return response
    }

    /**
     * Mark a response as NOT hidden by a teacher
     *
     * @param response the previously hidden response to show again
     * @return the response
     */
    fun unhideResponse(user: User, response: Response): Response {
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
     * Mark a response as recommended by a teacher
     *
     * @param response the response to add as recommended
     * @return the response
     */
    fun addRecommendedByTeacher(user: User, response: Response): Response {
        // Only a teacher can add a response as favourite
        require(user.isTeacher()) {
            "Only a teacher can unhide a response"
        }

        if (!response.recommendedByTeacher && !response.hiddenByTeacher) {
            response.recommendedByTeacher = true
            return responseRepository.save(response)
        }
        return response
    }

    /**
     * Mark a response as NOT recommended by a teacher
     *
     * @param response the previously recommended response
     * @return the response
     */
    fun removeRecommendedByTeacher(user: User, response: Response): Response {
        // Only a teacher can remove a response as favourite
        require(user.isTeacher()) {
            "Only a teacher can unhide a response"
        }

        if (response.recommendedByTeacher) {
            response.recommendedByTeacher = false
            return responseRepository.save(response)
        }
        return response
    }

    /**
     * Return true if the user can hide the peer grading of a response An user
     * can hide the peer grading if he is the owner of the assigment
     *
     * @param teacher the user who want to hide the peer grading
     * @param response the response where the peer grading to hide is
     * @return true if the user can hide the peer grading
     */
    fun canHidePeerGrading(teacher: User, response: Response): Boolean {
        return response.interaction.sequence.assignment!!.owner == teacher
    }


    /**
     * Return true if the user can moderate the feedback of the response An
     * user can moderate the feedback if he is the owner of the sequence
     *
     * @param user the user who want to moderate the feedback
     * @param response the response to moderate
     */
    fun canReactOnFeedbackOfResponse(user: User, response: Response): Boolean {
        return response.learner == user
    }

    /**
     * Return all the responses of the sequence that are not fake for the given
     * attempt
     *
     * The teacher created fake Response to simulate a learner's response
     *
     * @param attempt the attempt of the sequence
     * @param sequence the sequence
     * @return All the responses of the sequence that are not fake for the
     *    given attempt
     * @see Response.fake
     */
    fun findAllByAttemptNotFake(attempt: Int, sequence: Sequence): List<Response> {
        return responseRepository.findAllByInteractionAndAttempt(sequence.getResponseSubmissionInteraction(), attempt)
            .filter { !it.fake }
    }

    /**
     * Return all the fake responses of the sequence
     *
     * The attempt for a fake response depends on the execution context of the
     * sequence
     *
     * @param sequence the sequence
     * @return All the fake responses of the sequence
     * @see Response.fake
     */
    fun findAllFakeResponses(sequence: Sequence): List<Response> {
        return when (sequence.executionContext) {
            ExecutionContext.FaceToFace -> responseRepository.findAllByAttemptAndInteractionAndFakeIsTrue(
                1,
                sequence.getResponseSubmissionInteraction()
            )

            else -> responseRepository.findAllByAttemptAndInteractionAndFakeIsTrue(
                2,
                sequence.getResponseSubmissionInteraction()
            )
        }
    }

    /**
     * Return the same response but with the given attempt
     *
     * @param response the response to find
     * @param attempt the attempt of the response to find
     */
    fun findResponseByResponseAndAttempt(response: Response, attempt: Int): Response? {
        return if (attempt == response.attempt) {
            response
        } else {
            responseRepository.findByInteractionAndAttemptAndLearner(response.interaction, attempt, response.learner)
        }
    }
}