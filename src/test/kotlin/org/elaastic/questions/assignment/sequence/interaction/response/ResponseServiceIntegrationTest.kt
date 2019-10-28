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

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.*
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest
@Transactional
internal class ResponseServiceIntegrationTest(
        @Autowired val testingService: TestingService,
        @Autowired val responseService: ResponseService,
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val sequenceService: SequenceService,
        @Autowired val assignmentService: AssignmentService,
        @Autowired val statementService: StatementService,
        @Autowired val userService: UserService,
        @Autowired val peerGradingRepository: PeerGradingRepository,
        @Autowired val entityManager: EntityManager
) {

    @Test
    fun buildResponseBasedOnTeacherNullExpectedExplanationForASequenceOpenEndedBlended() {
        tGiven("given a sequence corresponding with an open ended question but with expected explanation") {
            assignmentService.addSequence(
                    assignment = testingService.getAnyAssignment(),
                    statement = Statement(
                            owner = testingService.getAnyAssignment().owner,
                            title = "q1",
                            content = "question 1",
                            expectedExplanation = null,
                            questionType = QuestionType.OpenEnded
                    )
            ).let {
                sequenceService.initializeInteractionsForSequence(
                        it,
                        true,
                        3,
                        ExecutionContext.Blended
                ).let { sequence ->
                    sequence.executionContext = ExecutionContext.Blended
                    sequenceRepository.save(sequence)
                }
            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponseBasedOnTeacherExpectedExplanationForASequence(
                    sequence = sequence,
                    teacher = sequence.owner
            )
        }.tThen { response ->
            assertThat(response, nullValue())
        }
    }

    @Test
    fun buildResponseBasedOnTeacherEmptyExpectedExplanationForASequenceOpenEndedBlended() {
        tGiven("given a sequence corresponding with an open ended question but with expected explanation") {
            assignmentService.addSequence(
                    assignment = testingService.getAnyAssignment(),
                    statement = Statement(
                            owner = testingService.getAnyAssignment().owner,
                            title = "q1",
                            content = "question 1",
                            expectedExplanation = "",
                            questionType = QuestionType.OpenEnded
                    )
            ).let {
                sequenceService.initializeInteractionsForSequence(
                        it,
                        true,
                        3,
                        ExecutionContext.Blended
                ).let { sequence ->
                    sequence.executionContext = ExecutionContext.Blended
                    sequenceRepository.save(sequence)
                }
            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponseBasedOnTeacherExpectedExplanationForASequence(
                    sequence = sequence,
                    teacher = sequence.owner
            )
        }.tThen { response ->
            assertThat(response, nullValue())
        }
    }

    @Test
    fun buildResponseBasedOnTeacherExpectedExplanationForASequenceOpenEndedBlended() {
        tGiven("given a sequence corresponding with an open ended question but with expected explanation") {
            assignmentService.addSequence(
                    assignment = testingService.getAnyAssignment(),
                    statement = Statement(
                            owner = testingService.getAnyAssignment().owner,
                            title = "q1",
                            content = "question 1",
                            expectedExplanation = "it is expected",
                            questionType = QuestionType.OpenEnded
                    )
            ).let {
                sequenceService.initializeInteractionsForSequence(
                        it,
                        true,
                        3,
                        ExecutionContext.Blended
                ).let { sequence ->
                    sequence.executionContext = ExecutionContext.Blended
                    sequenceRepository.save(sequence)
                }
            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponseBasedOnTeacherExpectedExplanationForASequence(
                    sequence = sequence,
                    teacher = sequence.owner
            )
        }.tThen { response ->
            assertThat(response!!.id, notNullValue())
            assertThat(response.learner, equalTo(testingService.getAnyAssignment().owner))
            assertThat(response.score, nullValue())
            assertThat(response.confidenceDegree, equalTo(ConfidenceDegree.CONFIDENT))
            assertThat(response.explanation, equalTo(response.interaction.sequence.statement.expectedExplanation))
            assertThat(response.attempt, equalTo(2))
            assertTrue(response.fake)
        }
    }

    @Test
    fun buildResponseBasedOnTeacherExpectedExplanationForASequenceExclusiveChoiceBlended() {
        tGiven("given a sequence corresponding with an open ended question but with expected explanation") {
            assignmentService.addSequence(
                    assignment = testingService.getAnyAssignment(),
                    statement = Statement(
                            owner = testingService.getAnyAssignment().owner,
                            title = "q1",
                            content = "question 1",
                            expectedExplanation = "it is expected",
                            questionType = QuestionType.ExclusiveChoice,
                            choiceSpecification = ExclusiveChoiceSpecification(
                                    nbCandidateItem = 3,
                                    expectedChoice = ChoiceItem(2, 100f)
                            )
                    )
            ).let {
                sequenceService.initializeInteractionsForSequence(
                        it,
                        true,
                        3,
                        ExecutionContext.Blended
                ).let { sequence ->
                    sequence.executionContext = ExecutionContext.Blended
                    sequenceRepository.save(sequence)
                }
            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponseBasedOnTeacherExpectedExplanationForASequence(
                    sequence = sequence,
                    teacher = sequence.owner
            )
        }.tThen { response ->
            assertThat(response!!.id, notNullValue())
            assertThat(response.learner, equalTo(testingService.getAnyAssignment().owner))
            assertThat(response.score, equalTo(BigDecimal(100)))
            assertThat(response.confidenceDegree, equalTo(ConfidenceDegree.CONFIDENT))
            assertThat(response.explanation, equalTo(response.interaction.sequence.statement.expectedExplanation))
            assertThat(response.attempt, equalTo(2))
            assertTrue(response.fake)
            assertThat(response.learnerChoice, equalTo(LearnerChoice(listOf(2))))
        }
    }

    @Test
    fun buildResponseBasedOnTeacherExpectedExplanationForASequenceMultipleChoiceFaceToFace() {
        tGiven("given a sequence corresponding with an open ended question but with expected explanation") {
            assignmentService.addSequence(
                    assignment = testingService.getAnyAssignment(),
                    statement = Statement(
                            owner = testingService.getAnyAssignment().owner,
                            title = "q1",
                            content = "question 1",
                            expectedExplanation = "it is expected",
                            questionType = QuestionType.MultipleChoice,
                            choiceSpecification = MultipleChoiceSpecification(
                                    nbCandidateItem = 4,
                                    expectedChoiceList = listOf(
                                            ChoiceItem(4, 50f),
                                            ChoiceItem(2, 50f)
                                    )
                            )
                    )
            ).let {
                sequenceService.initializeInteractionsForSequence(
                        it,
                        true,
                        3,
                        ExecutionContext.FaceToFace
                ).let { sequence ->
                    sequence.executionContext = ExecutionContext.FaceToFace
                    sequenceRepository.save(sequence)
                }
            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponseBasedOnTeacherExpectedExplanationForASequence(
                    sequence = sequence,
                    teacher = sequence.owner
            )
        }.tThen { response ->
            assertThat(response!!.id, notNullValue())
            assertThat(response.learner, equalTo(testingService.getAnyAssignment().owner))
            assertThat(response.score, equalTo(BigDecimal(100)))
            assertThat(response.confidenceDegree, equalTo(ConfidenceDegree.CONFIDENT))
            assertThat(response.explanation, equalTo(response.interaction.sequence.statement.expectedExplanation))
            assertThat(response.attempt, equalTo(1))
            assertTrue(response.fake)
            assertThat(response.learnerChoice, equalTo(LearnerChoice(listOf(4, 2))))
        }
    }

    @Test
    fun buildResponseBasedOnTeacherFakeExplanationsForASequenceOpenEndedBlended() {
        //"given a sequence corresponding with an open ended question but with fake explanations"
        val fakeExplanations = listOf(
                FakeExplanationData(
                        null,
                        content = "first fake"
                ),
                FakeExplanationData(
                        null,
                        content = "second fake"
                )
        )
        assignmentService.addSequence(
                assignment = testingService.getAnyAssignment(),
                statement = Statement(
                        owner = testingService.getAnyAssignment().owner,
                        title = "q1",
                        content = "question 1",
                        expectedExplanation = "it is expected",
                        questionType = QuestionType.OpenEnded
                )
        ).let {
            statementService.updateFakeExplanationList(it.statement, fakeExplanations)
            sequenceService.initializeInteractionsForSequence(
                    it,
                    true,
                    3,
                    ExecutionContext.Blended
            ).let { sequence ->
                sequence.executionContext = ExecutionContext.Blended
                sequenceRepository.save(sequence)

            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponsesBasedOnTeacherFakeExplanationsForASequence(
                    sequence = sequence
            )
        }.tThen { responseList ->
            assertThat(responseList.size, equalTo(2))
            responseList.forEachIndexed { index, response ->
                assertThat(response!!.id, notNullValue())
                assertThat(response.learner, equalTo(userService.fakeUserList!![index]))
                assertThat(response.score, nullValue())
                assertThat(response.confidenceDegree, equalTo(ConfidenceDegree.CONFIDENT))
                assertThat(response.explanation, equalTo(fakeExplanations[index].content))
                assertThat(response.attempt, equalTo(2))
                assertTrue(response.fake)
            }
        }
    }

    @Test
    fun buildResponseBasedOnTeacherFakeExplanationsForASequenceExclusiveChoiceBlended() {
        //"given a sequence corresponding with an open ended question but with fake explanations"
        val fakeExplanations = listOf(
                FakeExplanationData(
                        1,
                        content = "first fake"
                ),
                FakeExplanationData(
                        2,
                        content = "second fake"
                ),
                FakeExplanationData(
                        3,
                        content = "third fake"
                )
        )
        assignmentService.addSequence(
                assignment = testingService.getAnyAssignment(),
                statement = Statement(
                        owner = testingService.getAnyAssignment().owner,
                        title = "q1",
                        content = "question 1",
                        expectedExplanation = "it is expected",
                        questionType = QuestionType.ExclusiveChoice,
                        choiceSpecification = ExclusiveChoiceSpecification(
                                nbCandidateItem = 3,
                                expectedChoice = ChoiceItem(2, 100f)
                        )
                )
        ).let {
            statementService.updateFakeExplanationList(it.statement, fakeExplanations)
            sequenceService.initializeInteractionsForSequence(
                    it,
                    true,
                    3,
                    ExecutionContext.Blended
            ).let { sequence ->
                sequence.executionContext = ExecutionContext.Blended
                sequenceRepository.save(sequence)

            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponsesBasedOnTeacherFakeExplanationsForASequence(
                    sequence = sequence
            )
        }.tThen { responseList ->
            assertThat(responseList.size, equalTo(3))
            responseList.forEachIndexed { index, response ->
                assertThat(response!!.id, notNullValue())
                assertThat(response.learner, equalTo(userService.fakeUserList!![index]))
                assertThat(response.score, equalTo(if (index == 1) BigDecimal(100) else BigDecimal.ZERO))
                assertThat(response.confidenceDegree, equalTo(ConfidenceDegree.CONFIDENT))
                assertThat(response.explanation, equalTo(fakeExplanations[index].content))
                assertThat(response.attempt, equalTo(2))
                assertTrue(response.fake)
            }
        }
    }

    @Test
    fun buildResponseBasedOnTeacherFakeExplanationsForASequenceMultipleChoiceFaceToFace() {
        //"given a sequence corresponding with an open ended question but with fake explanations"
        val fakeExplanations = listOf(
                FakeExplanationData(
                        1,
                        content = "first fake"
                ),
                FakeExplanationData(
                        2,
                        content = "second fake"
                ),
                FakeExplanationData(
                        3,
                        content = "third fake"
                )
        )
        assignmentService.addSequence(
                assignment = testingService.getAnyAssignment(),
                statement = Statement(
                        owner = testingService.getAnyAssignment().owner,
                        title = "q1",
                        content = "question 1",
                        expectedExplanation = "it is expected",
                        questionType = QuestionType.MultipleChoice,
                        choiceSpecification = MultipleChoiceSpecification(
                                nbCandidateItem = 4,
                                expectedChoiceList = listOf(
                                        ChoiceItem(4, 50f),
                                        ChoiceItem(2, 50f)
                                )
                        )
                )
        ).let {
            statementService.updateFakeExplanationList(it.statement, fakeExplanations)
            sequenceService.initializeInteractionsForSequence(
                    it,
                    true,
                    3,
                    ExecutionContext.FaceToFace
            ).let { sequence ->
                sequence.executionContext = ExecutionContext.FaceToFace
                sequenceRepository.save(sequence)
            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponsesBasedOnTeacherFakeExplanationsForASequence(
                    sequence = sequence
            )
        }.tThen { responseList ->
            assertThat(responseList.size, equalTo(3))
            responseList.forEachIndexed { index, response ->
                assertThat(response!!.id, notNullValue())
                assertThat(response.learner, equalTo(userService.fakeUserList!![index]))
                assertThat(response.score, equalTo(if (index == 1) BigDecimal(50) else BigDecimal.ZERO))
                assertThat(response.confidenceDegree, equalTo(ConfidenceDegree.CONFIDENT))
                assertThat(response.explanation, equalTo(fakeExplanations[index].content))
                assertThat(response.attempt, equalTo(1))
                assertTrue(response.fake)
            }
        }
    }

    @Test
    fun testUpgradeMeanGradeAndEvaluationCount() {
        tGiven("given a sequence corresponding with an open ended question but with expected explanation") {
            assignmentService.addSequence(
                    assignment = testingService.getAnyAssignment(),
                    statement = Statement(
                            owner = testingService.getAnyAssignment().owner,
                            title = "q1",
                            content = "question 1",
                            expectedExplanation = "it is expected",
                            questionType = QuestionType.MultipleChoice,
                            choiceSpecification = MultipleChoiceSpecification(
                                    nbCandidateItem = 4,
                                    expectedChoiceList = listOf(
                                            ChoiceItem(4, 50f),
                                            ChoiceItem(2, 50f)
                                    )
                            )
                    )
            ).let {
                sequenceService.initializeInteractionsForSequence(
                        it,
                        true,
                        3,
                        ExecutionContext.FaceToFace
                ).let { sequence ->
                    sequence.executionContext = ExecutionContext.FaceToFace
                    sequenceRepository.save(sequence)
                }
            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponseBasedOnTeacherExpectedExplanationForASequence(
                    sequence = sequence,
                    teacher = sequence.owner
            )
        }.tWhen("we build some peer grading") {
            listOf(
                    PeerGrading(
                            grade = BigDecimal(1),
                            grader = userService.fakeUserList!![0],
                            response = it!!,
                            annotation = null
                    ),
                    PeerGrading(
                            grade = BigDecimal(2),
                            grader = userService.fakeUserList!![1],
                            response = it,
                            annotation = null
                    ),
                    PeerGrading(
                            grade = BigDecimal(1),
                            grader = userService.fakeUserList!![2],
                            response = it,
                            annotation = null
                    )
            ).map { peerGrading ->
                peerGradingRepository.save(peerGrading)
            }.tWhen("update mean grade and evaluation count") { peerGradingList ->
                responseService.updateMeanGradeAndEvaluationCount(response = peerGradingList[0].response)
            }.tThen { response ->
                assertThat(response.evaluationCount, equalTo(3))
                assertThat(response.meanGrade!!, equalTo(BigDecimal("1.33")))
            }
        }
    }

    @Test
    fun testUpgradeMeanGradeAndEvaluationCountWhenNoPeerGrading() {
        tGiven("given a sequence corresponding with an open ended question but with expected explanation") {
            assignmentService.addSequence(
                    assignment = testingService.getAnyAssignment(),
                    statement = Statement(
                            owner = testingService.getAnyAssignment().owner,
                            title = "q1",
                            content = "question 1",
                            expectedExplanation = "it is expected",
                            questionType = QuestionType.MultipleChoice,
                            choiceSpecification = MultipleChoiceSpecification(
                                    nbCandidateItem = 4,
                                    expectedChoiceList = listOf(
                                            ChoiceItem(4, 50f),
                                            ChoiceItem(2, 50f)
                                    )
                            )
                    )
            ).let {
                sequenceService.initializeInteractionsForSequence(
                        it,
                        true,
                        3,
                        ExecutionContext.FaceToFace
                ).let { sequence ->
                    sequence.executionContext = ExecutionContext.FaceToFace
                    sequenceRepository.save(sequence)
                }
            }
        }.tWhen("we build a response based on expected explanations") { sequence ->
            responseService.buildResponseBasedOnTeacherExpectedExplanationForASequence(
                    sequence = sequence,
                    teacher = sequence.owner
            )
        }.tWhen("update mean grade and evaluation count") { response ->
            responseService.updateMeanGradeAndEvaluationCount(response = response!!)
        }.tThen { response ->
            assertThat(response.evaluationCount, equalTo(0))
            assertThat(response.meanGrade, nullValue())
        }
    }
}

