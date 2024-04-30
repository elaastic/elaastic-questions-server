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
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.*
import org.elaastic.questions.assignment.sequence.peergrading.LikertPeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class ResponseServiceIntegrationTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val responseService: ResponseService,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val sequenceRepository: SequenceRepository,
    @Autowired val sequenceService: SequenceService,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val statementService: StatementService,
    @Autowired val userService: UserService,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
    @Autowired val peerGradingService: PeerGradingService,
) {

    @Test
    fun buildResponseBasedOnTeacherNullExpectedExplanationForASequenceOpenEndedBlended() {
        tGiven("given a sequence corresponding with an open ended question but with expected explanation") {
            val subject = integrationTestingService.getAnyTestSubject()
            val assignment = integrationTestingService.getAnyAssignment()
            subject.owner = assignment.owner

            val stmt1 = subjectService.addStatement(
                subject,
                Statement(
                    owner = subject.owner,
                    title = "q1",
                    content = "question 1",
                    expectedExplanation = null,
                    questionType = QuestionType.OpenEnded,
                    subject = subject
                )
            )
            assignmentService.addSequence(
                assignment = assignment,
                statement = stmt1
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
            val subject = integrationTestingService.getAnyTestSubject()
            val assignment = integrationTestingService.getAnyAssignment()
            subject.owner = assignment.owner

            val stmt1 = subjectService.addStatement(
                subject,
                Statement(
                    owner = assignment.owner,
                    title = "q1",
                    content = "question 1",
                    expectedExplanation = "",
                    questionType = QuestionType.OpenEnded,
                    subject = subject
                )
            )

            assignmentService.addSequence(
                assignment = assignment,
                statement = stmt1
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
            val subject = integrationTestingService.getAnyTestSubject()
            val assignment = integrationTestingService.getAnyAssignment()
            subject.owner = assignment.owner

            val stmt1 = subjectService.addStatement(
                subject,
                Statement(
                    owner = assignment.owner,
                    title = "q1",
                    content = "question 1",
                    expectedExplanation = "it is expected",
                    questionType = QuestionType.OpenEnded,
                    subject = subject
                )
            )

            assignmentService.addSequence(
                assignment = assignment,
                statement = stmt1
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
            assertThat(response.learner, equalTo(integrationTestingService.getAnyAssignment().owner))
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
            val subject = integrationTestingService.getAnyTestSubject()
            val assignment = integrationTestingService.getAnyAssignment()
            subject.owner = assignment.owner

            val stmt1 = subjectService.addStatement(
                subject,
                Statement(
                    owner = assignment.owner,
                    title = "q1",
                    content = "question 1",
                    expectedExplanation = "it is expected",
                    questionType = QuestionType.ExclusiveChoice,
                    choiceSpecification = ExclusiveChoiceSpecification(
                        nbCandidateItem = 3,
                        expectedChoice = ChoiceItem(2, 100f)
                    ),
                    subject = subject
                )
            )

            assignmentService.addSequence(
                assignment = assignment,
                statement = stmt1
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
            assertThat(response.learner, equalTo(integrationTestingService.getAnyAssignment().owner))
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
            val subject = integrationTestingService.getAnyTestSubject()
            val assignment = integrationTestingService.getAnyAssignment()
            subject.owner = assignment.owner

            val stmt1 = subjectService.addStatement(
                subject,
                Statement(
                    owner = assignment.owner,
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
                    ),
                    subject = subject
                )
            )

            assignmentService.addSequence(
                assignment = assignment,
                statement = stmt1
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
            assertThat(response.learner, equalTo(integrationTestingService.getAnyAssignment().owner))
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

        val subject = integrationTestingService.getAnyTestSubject()
        val assignment = integrationTestingService.getAnyAssignment()
        subject.owner = assignment.owner

        val stmt1 = subjectService.addStatement(
            subject,
            Statement(
                owner = assignment.owner,
                title = "q1",
                content = "question 1",
                expectedExplanation = "it is expected",
                questionType = QuestionType.OpenEnded,
                subject = subject
            )
        )

        assignmentService.addSequence(
            assignment = assignment,
            statement = stmt1
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
                assertThat(response.id, notNullValue())
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

        val subject = integrationTestingService.getAnyTestSubject()
        val assignment = integrationTestingService.getAnyAssignment()
        subject.owner = assignment.owner

        val stmt1 = subjectService.addStatement(
            subject,
            Statement(
                owner = assignment.owner,
                title = "q1",
                content = "question 1",
                expectedExplanation = "it is expected",
                questionType = QuestionType.ExclusiveChoice,
                choiceSpecification = ExclusiveChoiceSpecification(
                    nbCandidateItem = 3,
                    expectedChoice = ChoiceItem(2, 100f)
                ),
                subject = subject
            )
        )

        assignmentService.addSequence(
            assignment = assignment,
            statement = stmt1
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
                assertThat(response.id, notNullValue())
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

        val subject = integrationTestingService.getAnyTestSubject()
        val assignment = integrationTestingService.getAnyAssignment()
        subject.owner = assignment.owner

        val stmt1 = subjectService.addStatement(
            subject,
            Statement(
                owner = assignment.owner,
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
                ),
                subject = subject
            )
        )

        assignmentService.addSequence(
            assignment = assignment,
            statement = stmt1
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
                assertThat(response.id, notNullValue())
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
            statementService.save(
                Statement(
                    owner = integrationTestingService.getAnyAssignment().owner,
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
                assignmentService.addSequence(
                    assignment = integrationTestingService.getAnyAssignment(),
                    statement = it
                )
            }.let {
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
                LikertPeerGrading(
                    grade = BigDecimal(1),
                    grader = userService.fakeUserList!![0],
                    response = it!!,
                ),
                LikertPeerGrading(
                    grade = BigDecimal(2),
                    grader = userService.fakeUserList!![1],
                    response = it,
                ),
                LikertPeerGrading(
                    grade = BigDecimal(1),
                    grader = userService.fakeUserList!![2],
                    response = it,
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
            val statement = statementService.save(
                Statement(
                    owner = integrationTestingService.getAnyAssignment().owner,
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
            )
            assignmentService.addSequence(
                assignment = integrationTestingService.getAnyAssignment(),
                statement = statement
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

    @Test
    fun testResponseIsRecommendedByTeacher() {
        tGiven("given a sequence corresponding with an exclusive choice question") {
            val subject = integrationTestingService.getAnyTestSubject()
            val assignment = integrationTestingService.getAnyAssignment()
            subject.owner = assignment.owner

            val stmt1 = subjectService.addStatement(
                subject,
                Statement(
                    owner = subject.owner,
                    title = "q1",
                    content = "question 1",
                    expectedExplanation = "Some stuff",
                    questionType = QuestionType.ExclusiveChoice,
                    subject = subject
                )
            )
            assignmentService.addSequence(
                assignment = assignment,
                statement = stmt1
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
                teacher = sequence.owner,
            )
        }.tWhen("we mark this response as recommended by the teacher") { response ->
            responseService.addRecommendedByTeacher(integrationTestingService.getTestTeacher(), response!!)
        }.tThen { response ->
            assertThat(response.recommendedByTeacher, equalTo(true))
        }
    }

    @Test
    fun `a teacher can hide all the peerGrading in the assignement he own`() {
        val teacher = integrationTestingService.getTestTeacher()
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        val assignement = response.interaction.sequence.assignment!!

        tGiven("An assignement own by the teacher and a peerGrading of the response") {
            assignement.owner = teacher
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tThen("The teacher can hide the feedback") { peerGrading ->
            assertEquals(teacher, assignement.owner)
            assertTrue(peerGradingService.canHidePeerGrading(teacher, peerGrading), "The teacher can hide the feedback")
            assertEquals(0, peerGrading.response.draxoEvaluationHiddenCount, "The hidden count must be 0")
            peerGrading
        }.tWhen("the teacher hide the peerGrading") { peerGrading ->
            peerGradingService.markAsHidden(teacher, peerGrading)
            peerGrading
        }.tThen("the peerGrading is hidden") { peerGrading ->
            assertTrue(peerGrading.hiddenByTeacher, "The feedback is hidden")
            assertEquals(
                1,
                responseRepository.findById(response.id!!).get().draxoEvaluationHiddenCount,
                "The hidden count must be 1"
            )
        }
    }

    @Test
    fun `a student can't hide an peerGrading despite owning the response`() {
        val student = integrationTestingService.getTestStudent()
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()

        tGiven("An assignement own by the teacher and a peerGrading of the response") {
            response.learner = student
            LikertPeerGrading(
                grade = BigDecimal(2),
                annotation = "Annotation",
                grader = grader,
                response = response
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tThen("The student can't hide the feedback") { peerGrading ->
            assertFalse(
                peerGradingService.canHidePeerGrading(student, peerGrading),
                "The student can't hide a peergrading"
            )
            peerGrading
        }.tWhen("the student try hidding the peerGrading") { peerGrading ->
            peerGrading
        }.tThen("an excetion is thrown despite owning the response") { peerGrading ->
            assertThrows(
                IllegalAccessException::class.java
            ) { peerGradingService.markAsHidden(student, peerGrading) }
            assertEquals(student, peerGrading.response.learner, "The student own the response")
            assertEquals(0, peerGrading.response.draxoEvaluationHiddenCount, "The hidden count must not have changed")
        }
    }

    @Test
    fun `a student can only moderate the feedback for his anwser`() {
        val student = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()

        tGiven("Given an anwser given by a student") {
            response.learner = student

        }.tThen("Then the student can moderate the feedback of the answer") {
            assertTrue(responseService.canModerate(student, response), "A student can moderate his own response")

        }.tThen("Another studnet can't moderate the answer") {
            val anotherStudent = integrationTestingService.getNLearners(1).first()
            assertFalse(
                responseService.canModerate(anotherStudent, response),
                "Another student can't moderate the response"
            )
            assertNotEquals(student, anotherStudent, "The two students must be different")
        }
    }

    @Test
    fun `a teacher can unhide an hidden draxoPeerGrading`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        val teacher = response.interaction.sequence.owner

        tGiven("An assignement own by the teacher and a peerGrading of the response") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.save(it)
                    it
                }
        }.tWhen("the teacher hide the peerGrading") { peerGrading ->
            peerGradingService.markAsHidden(teacher, peerGrading)
            peerGrading
        }.tThen("the peerGrading is hidden") { peerGrading ->
            assertTrue(peerGrading.hiddenByTeacher, "The feedback is hidden")
            assertInstanceOf(DraxoPeerGrading::class.java, peerGrading, "The peerGrading is a DraxoPeerGrading")
            assertEquals(1, response.draxoEvaluationHiddenCount, "The hidden count must be 1")
            peerGrading
        }.tWhen("the teacher unhide the peerGrading") { peerGrading ->
            peerGradingService.markAsShow(teacher, peerGrading)
            peerGrading
        }.tThen("the peerGrading is unhidden") { peerGrading ->
            assertFalse(peerGrading.hiddenByTeacher, "The feedback is unhidden")
            assertInstanceOf(DraxoPeerGrading::class.java, peerGrading, "The peerGrading is a DraxoPeerGrading")
            assertEquals(0, peerGrading.response.draxoEvaluationHiddenCount, "The hidden count must be 0")
        }
    }

}