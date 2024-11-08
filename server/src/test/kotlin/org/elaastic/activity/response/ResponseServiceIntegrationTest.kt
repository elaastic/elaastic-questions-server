package org.elaastic.activity.response

import org.elaastic.activity.evaluation.peergrading.LikertPeerGrading
import org.elaastic.activity.evaluation.peergrading.PeerGradingRepository
import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoEvaluation
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoPeerGrading
import org.elaastic.activity.evaluation.peergrading.draxo.criteria.Criteria
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionId
import org.elaastic.assignment.AssignmentService
import org.elaastic.sequence.ExecutionContext
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.choice.MultipleChoiceSpecification
import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.sequence.FakeExplanationData
import org.elaastic.sequence.SequenceRepository
import org.elaastic.sequence.SequenceService
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.test.FunctionalTestingService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.elaastic.questions.test.interpreter.command.Phase
import org.elaastic.user.UserService
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.math.RoundingMode
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
    @Autowired val functionalTestingService: FunctionalTestingService,
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
            MatcherAssert.assertThat(response, Matchers.nullValue())
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
            MatcherAssert.assertThat(response, Matchers.nullValue())
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
            MatcherAssert.assertThat(response!!.id, Matchers.notNullValue())
            MatcherAssert.assertThat(
                response.learner,
                Matchers.equalTo(integrationTestingService.getAnyAssignment().owner)
            )
            MatcherAssert.assertThat(response.score, Matchers.nullValue())
            MatcherAssert.assertThat(response.confidenceDegree, Matchers.equalTo(ConfidenceDegree.CONFIDENT))
            MatcherAssert.assertThat(
                response.explanation,
                Matchers.equalTo(response.interaction.sequence.statement.expectedExplanation)
            )
            MatcherAssert.assertThat(response.attempt, Matchers.equalTo(2))
            Assertions.assertTrue(response.fake)
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
            MatcherAssert.assertThat(response!!.id, Matchers.notNullValue())
            MatcherAssert.assertThat(
                response.learner,
                Matchers.equalTo(integrationTestingService.getAnyAssignment().owner)
            )
            MatcherAssert.assertThat(response.score, Matchers.equalTo(BigDecimal(100)))
            MatcherAssert.assertThat(response.confidenceDegree, Matchers.equalTo(ConfidenceDegree.CONFIDENT))
            MatcherAssert.assertThat(
                response.explanation,
                Matchers.equalTo(response.interaction.sequence.statement.expectedExplanation)
            )
            MatcherAssert.assertThat(response.attempt, Matchers.equalTo(2))
            Assertions.assertTrue(response.fake)
            MatcherAssert.assertThat(response.learnerChoice, Matchers.equalTo(LearnerChoice(listOf(2))))
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
            MatcherAssert.assertThat(response!!.id, Matchers.notNullValue())
            MatcherAssert.assertThat(
                response.learner,
                Matchers.equalTo(integrationTestingService.getAnyAssignment().owner)
            )
            MatcherAssert.assertThat(response.score, Matchers.equalTo(BigDecimal(100)))
            MatcherAssert.assertThat(response.confidenceDegree, Matchers.equalTo(ConfidenceDegree.CONFIDENT))
            MatcherAssert.assertThat(
                response.explanation,
                Matchers.equalTo(response.interaction.sequence.statement.expectedExplanation)
            )
            MatcherAssert.assertThat(response.attempt, Matchers.equalTo(1))
            Assertions.assertTrue(response.fake)
            MatcherAssert.assertThat(response.learnerChoice, Matchers.equalTo(LearnerChoice(listOf(4, 2))))
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
            MatcherAssert.assertThat(responseList.size, Matchers.equalTo(2))
            responseList.forEachIndexed { index, response ->
                MatcherAssert.assertThat(response.id, Matchers.notNullValue())
                MatcherAssert.assertThat(response.learner, Matchers.equalTo(userService.fakeUserList!![index]))
                MatcherAssert.assertThat(response.score, Matchers.nullValue())
                MatcherAssert.assertThat(response.confidenceDegree, Matchers.equalTo(ConfidenceDegree.CONFIDENT))
                MatcherAssert.assertThat(response.explanation, Matchers.equalTo(fakeExplanations[index].content))
                MatcherAssert.assertThat(response.attempt, Matchers.equalTo(2))
                Assertions.assertTrue(response.fake)
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
            MatcherAssert.assertThat(responseList.size, Matchers.equalTo(3))
            responseList.forEachIndexed { index, response ->
                MatcherAssert.assertThat(response.id, Matchers.notNullValue())
                MatcherAssert.assertThat(response.learner, Matchers.equalTo(userService.fakeUserList!![index]))
                MatcherAssert.assertThat(
                    response.score,
                    Matchers.equalTo(if (index == 1) BigDecimal(100) else BigDecimal.ZERO)
                )
                MatcherAssert.assertThat(response.confidenceDegree, Matchers.equalTo(ConfidenceDegree.CONFIDENT))
                MatcherAssert.assertThat(response.explanation, Matchers.equalTo(fakeExplanations[index].content))
                MatcherAssert.assertThat(response.attempt, Matchers.equalTo(2))
                Assertions.assertTrue(response.fake)
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
            MatcherAssert.assertThat(responseList.size, Matchers.equalTo(3))
            responseList.forEachIndexed { index, response ->
                MatcherAssert.assertThat(response.id, Matchers.notNullValue())
                MatcherAssert.assertThat(response.learner, Matchers.equalTo(userService.fakeUserList!![index]))
                MatcherAssert.assertThat(
                    response.score,
                    Matchers.equalTo(if (index == 1) BigDecimal(50) else BigDecimal.ZERO)
                )
                MatcherAssert.assertThat(response.confidenceDegree, Matchers.equalTo(ConfidenceDegree.CONFIDENT))
                MatcherAssert.assertThat(response.explanation, Matchers.equalTo(fakeExplanations[index].content))
                MatcherAssert.assertThat(response.attempt, Matchers.equalTo(1))
                Assertions.assertTrue(response.fake)
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
                MatcherAssert.assertThat(response.evaluationCount, Matchers.equalTo(3))
                MatcherAssert.assertThat(response.meanGrade!!, Matchers.equalTo(BigDecimal("1.33")))
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
            MatcherAssert.assertThat(response.evaluationCount, Matchers.equalTo(0))
            MatcherAssert.assertThat(response.meanGrade, Matchers.nullValue())
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
            MatcherAssert.assertThat(response.recommendedByTeacher, Matchers.equalTo(true))
        }
    }

    @Test
    fun `a teacher can hide all the peerGrading in the assignement he own`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        val assignement = response.interaction.sequence.assignment!!
        val teacher = response.interaction.sequence.owner

        tGiven("An assignement own by the teacher and a peerGrading of the response") {
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
            Assertions.assertEquals(teacher, assignement.owner)
            Assertions.assertTrue(
                peerGradingService.canHidePeerGrading(teacher, peerGrading),
                "The teacher can hide the feedback"
            )
            Assertions.assertEquals(0, peerGrading.response.draxoEvaluationHiddenCount, "The hidden count must be 0")
            peerGrading
        }.tWhen("the teacher hide the peerGrading") { peerGrading ->
            peerGradingService.markAsHidden(teacher, peerGrading)
            peerGrading
        }.tThen("the peerGrading is hidden") { peerGrading ->
            Assertions.assertTrue(peerGrading.hiddenByTeacher, "The feedback is hidden")
            Assertions.assertEquals(
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
            Assertions.assertFalse(
                peerGradingService.canHidePeerGrading(student, peerGrading),
                "The student can't hide a peergrading"
            )
            peerGrading
        }.tWhen("the student try hidding the peerGrading") { peerGrading ->
            peerGrading
        }.tThen("an excetion is thrown despite owning the response") { peerGrading ->
            Assertions.assertThrows(
                IllegalAccessException::class.java
            ) { peerGradingService.markAsHidden(student, peerGrading) }
            Assertions.assertEquals(student, peerGrading.response.learner, "The student own the response")
            Assertions.assertEquals(
                0,
                peerGrading.response.draxoEvaluationHiddenCount,
                "The hidden count must not have changed"
            )
        }
    }

    @Test
    fun `a student can only moderate the feedback for his anwser`() {
        val student = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()

        tGiven("Given an anwser given by a student") {
            response.learner = student

        }.tThen("Then the student can moderate the feedback of the answer") {
            Assertions.assertTrue(
                responseService.canReactOnFeedbackOfResponse(student, response),
                "A student can moderate his own response"
            )

        }.tThen("Another studnet can't moderate the answer") {
            val anotherStudent = integrationTestingService.getNLearners(1).first()
            Assertions.assertFalse(
                responseService.canReactOnFeedbackOfResponse(anotherStudent, response),
                "Another student can't moderate the response"
            )
            Assertions.assertNotEquals(student, anotherStudent, "The two students must be different")
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
            Assertions.assertTrue(peerGrading.hiddenByTeacher, "The feedback is hidden")
            Assertions.assertInstanceOf(
                DraxoPeerGrading::class.java,
                peerGrading,
                "The peerGrading is a DraxoPeerGrading"
            )
            Assertions.assertEquals(1, response.draxoEvaluationHiddenCount, "The hidden count must be 1")
            peerGrading
        }.tWhen("the teacher unhide the peerGrading") { peerGrading ->
            peerGradingService.markAsShow(teacher, peerGrading)
            peerGrading
        }.tThen("the peerGrading is unhidden") { peerGrading ->
            Assertions.assertFalse(peerGrading.hiddenByTeacher, "The feedback is unhidden")
            Assertions.assertInstanceOf(
                DraxoPeerGrading::class.java,
                peerGrading,
                "The peerGrading is a DraxoPeerGrading"
            )
            Assertions.assertEquals(0, peerGrading.response.draxoEvaluationHiddenCount, "The hidden count must be 0")
        }
    }

    @Test
    fun `the mean grade is compute only with the visible peergrading`() {
        val grader = integrationTestingService.getTestStudent()
        val response = Response(
            integrationTestingService.getNLearners(1).first(),
            integrationTestingService.getAnyInteraction(),
            1,
            null,
            null,
            null,
            null,
            null,
            false,
            0,
            0,
            integrationTestingService.getAnyStatement(),
            hiddenByTeacher = false,
            recommendedByTeacher = false
        )
        responseRepository.save(response)
        val teacher = response.interaction.sequence.owner
        val two = BigDecimal(2).setScale(2, RoundingMode.HALF_UP)

        responseService.updateMeanGradeAndEvaluationCount(response)
        Assertions.assertEquals(0, response.evaluationCount, "The response doesn't have any peergrading")
        Assertions.assertNull(response.meanGrade, "The mean grade is null")

        tGiven("An assignement own by the teacher and a peerGrading of the response") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.YES)
                    .addEvaluation(
                        Criteria.R,
                        OptionId.YES
                    ), // The score of the criteria `R` with the option `YES` is 2
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.save(it)
                    it
                }
        }.tThen("The compute mean grade is 2") { peerGrading ->
            responseService.updateMeanGradeAndEvaluationCount(response)
            Assertions.assertEquals(two, response.meanGrade, "The mean grade is 2")
            peerGrading
        }.tWhen("the teacher hide the peerGrading") { peerGrading ->
            peerGradingService.markAsHidden(teacher, peerGrading)
            peerGrading
        }.tThen("The compute mean grade is 0") { peerGrading ->
            Assertions.assertNull(response.meanGrade, "The mean grade is null")
            peerGrading
        }.tWhen("the teacher unhide the peerGrading") { peerGrading ->
            peerGradingService.markAsShow(teacher, peerGrading)
        }.tThen("The compute mean grade is 2") {
            Assertions.assertEquals(two, response.meanGrade, "The mean grade is 2")
        }
    }

    @Test
    fun `test of findByIdLastAttempt`() {
        // Given
        val learners = integrationTestingService.getNLearners(1).first()
        val subject = functionalTestingService.createSubject(integrationTestingService.getTestTeacher())
        functionalTestingService.addQuestion(subject, QuestionType.OpenEnded)
        val assignement = functionalTestingService.createAssignment(subject)
        val sequence = assignement.sequences.first()

        lateinit var responseAttenmpt1: Response

        tWhen("we start the sequence") {
            functionalTestingService.startSequence(sequence, ExecutionContext.FaceToFace)
        }.tWhen("we submit a response for the learner") {
            responseAttenmpt1 = functionalTestingService.submitResponse(
                Phase.PHASE_1,
                learners,
                sequence,
                true,
                ConfidenceDegree.CONFIDENT,
                learners.getDisplayName() + " 1",
            )

        }.tThen("we get the response for the first attempt") {
            Assertions.assertEquals(responseAttenmpt1, responseService.findByIdLastAttempt(responseAttenmpt1.id!!))
        }.tWhen("The student change is response") {
            functionalTestingService.nextPhase(sequence) // Evaluation Phase
            functionalTestingService.submitResponse(
                Phase.PHASE_2,
                learners,
                sequence,
                true,
                ConfidenceDegree.CONFIDENT,
                learners.getDisplayName() + " 2",
            )
        }.tThen("We get the response for the second attempt with the id of the first attempt") {
            Assertions.assertEquals(it, responseService.findByIdLastAttempt(responseAttenmpt1.id!!))
        }
    }

    @Test
    fun `test of findResponseByResponseAndAttempt`() {
        // Given
        val learners = integrationTestingService.getNLearners(1).first()
        val subject = functionalTestingService.createSubject(integrationTestingService.getTestTeacher())
        functionalTestingService.addQuestion(subject, QuestionType.OpenEnded)
        val assignement = functionalTestingService.createAssignment(subject)
        val sequence = assignement.sequences.first()

        lateinit var responseAttenmpt1: Response

        tWhen("we start the sequence") {
            functionalTestingService.startSequence(sequence, ExecutionContext.FaceToFace)
        }.tWhen("we submit a response for the learner") {
            responseAttenmpt1 = functionalTestingService.submitResponse(
                Phase.PHASE_1,
                learners,
                sequence,
                true,
                ConfidenceDegree.CONFIDENT,
                learners.getDisplayName() + " 1",
            )

        }.tThen("we get the response for the first attempt") {
            Assertions.assertEquals(
                responseAttenmpt1,
                responseService.findResponseByResponseAndAttempt(responseAttenmpt1, 1)
            )
        }.tWhen("The student change is response") {
            functionalTestingService.nextPhase(sequence) // Evaluation Phase
            functionalTestingService.submitResponse(
                Phase.PHASE_2,
                learners,
                sequence,
                true,
                ConfidenceDegree.CONFIDENT,
                learners.getDisplayName() + " 2",
            )
        }.tThen("We get the response for the second attempt with the id of the first attempt") {
            Assertions.assertEquals(it, responseService.findResponseByResponseAndAttempt(responseAttenmpt1, 2))
        }
    }
}