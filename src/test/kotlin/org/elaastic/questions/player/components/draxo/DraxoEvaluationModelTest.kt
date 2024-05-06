package org.elaastic.questions.player.components.draxo

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DraxoEvaluationModelTest(
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
    fun `isUtilityGradeSelected should return true if the given utility grade is selected`() {
        val utilityGrade = UtilityGrade.AGREE

        tGiven("a DraxoEvaluationModel with a utility grade") {
            DraxoEvaluationModel(
                graderName = "graderName",
                graderNum = 1,
                score = null,
                draxoEvaluation = DraxoEvaluation(),
                userCanDisplayStudentsIdentity = false,
                draxoPeerGradingId = null,
                utilityGrade = utilityGrade,
                hiddenByTeacher = false,
                responseId = null,
                draxoPeerGrading = null
            )
        }.tThen("the utility grade should be selected") { draxoEvaluationModel ->
            assertTrue(draxoEvaluationModel.isUtilityGradeSelected(utilityGrade))
            assertFalse(draxoEvaluationModel.isUtilityGradeSelected(UtilityGrade.DISAGREE))
            assertFalse(draxoEvaluationModel.isUtilityGradeSelected(UtilityGrade.STRONGLY_AGREE))
            assertFalse(draxoEvaluationModel.isUtilityGradeSelected(UtilityGrade.STRONGLY_DISAGREE))

        }
    }

    @Test
    fun `isReported should return true if the student has reported this peer grading`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a reported peer grading") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO),
                lastSequencePeerGrading = false
            )
        }.tWhen("the peer grading is reported") {
            it.reportReasons = "reportReasons"
            it
        }.tThen("the peer grading should be reported") { draxoPeerGrading ->
            val draxoEvaluationModel = DraxoEvaluationModel(
                graderName = "graderName",
                graderNum = 1,
                score = null,
                draxoEvaluation = DraxoEvaluation(),
                userCanDisplayStudentsIdentity = false,
                draxoPeerGradingId = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                responseId = null,
                draxoPeerGrading = draxoPeerGrading
            )
            assertTrue(draxoEvaluationModel.isReported())
        }
    }

    @Test
    fun `canBeReacted should return true if the student can react to this peer grading`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a peer grading that can be reacted") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO, "explanation"),
                lastSequencePeerGrading = false
            )
        }.tThen("the peer grading should be able to be reacted") { draxoPeerGrading ->
            val draxoEvaluationModel = DraxoEvaluationModel(
                graderName = "graderName",
                graderNum = 1,
                score = null,
                draxoEvaluation = DraxoEvaluation(),
                userCanDisplayStudentsIdentity = false,
                draxoPeerGradingId = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                responseId = null,
                draxoPeerGrading = draxoPeerGrading
            )
            draxoEvaluationModel.canBeReacted()
            assertFalse(draxoEvaluationModel.isReported())
            assertFalse(draxoEvaluationModel.hiddenByTeacher)
            assertTrue(draxoEvaluationModel.canBeReacted())
        }
    }

    @Test
    fun `canBeReacted should return false to a peer grading with DONT_KNOW OptionId`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a peer grading that cannot be reacted") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.YES)
                    .addEvaluation(Criteria.R, OptionId.DONT_KNOW), // You can't react to a peer grading with a DONT_KNOW OptionId selected
                lastSequencePeerGrading = false
            )
        }.tThen("the peer grading should not be able to be reacted") { draxoPeerGrading ->
            val draxoEvaluationModel = DraxoEvaluationModel(
                graderName = "graderName",
                graderNum = 1,
                score = null,
                draxoEvaluation = draxoPeerGrading.getDraxoEvaluation(),
                userCanDisplayStudentsIdentity = false,
                draxoPeerGradingId = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                responseId = null,
                draxoPeerGrading = draxoPeerGrading
            )
            assertFalse(draxoEvaluationModel.canBeReacted())
        }
    }

    @Test
    fun `canBeReacted should return false to a peer grading with NO_OPINION OptionId`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a peer grading that cannot be reacted") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.YES)
                    .addEvaluation(Criteria.R, OptionId.YES)
                    .addEvaluation(Criteria.A, OptionId.NO_OPINION), // You can't react to a peer grading with a NO_OPINION OptionId selected
                lastSequencePeerGrading = false
            )
        }.tThen("the peer grading should not be able to be reacted") { draxoPeerGrading ->
            val draxoEvaluationModel = DraxoEvaluationModel(
                graderName = "graderName",
                graderNum = 1,
                score = null,
                draxoEvaluation = draxoPeerGrading.getDraxoEvaluation(),
                userCanDisplayStudentsIdentity = false,
                draxoPeerGradingId = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                responseId = null,
                draxoPeerGrading = draxoPeerGrading
            )
            assertFalse(draxoEvaluationModel.canBeReacted())
        }
    }

    @Test
    fun `canBeReacted should return false to a peer grading with hiddenByTeacher`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a peer grading that cannot be reacted") {
            val draxoPeerGrading = DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO, "explanation"),
                lastSequencePeerGrading = false,
            )
            DraxoEvaluationModel(
                graderName = "graderName",
                graderNum = 1,
                score = null,
                draxoEvaluation = DraxoEvaluation(),
                userCanDisplayStudentsIdentity = false,
                draxoPeerGradingId = null,
                utilityGrade = null,
                hiddenByTeacher = true, // The teacher has hidden this peer grading, so a student can't react to it
                responseId = null,
                draxoPeerGrading = draxoPeerGrading
            )
        }.tThen("the peer grading should not be able to be reacted") {
            assertFalse(it.canBeReacted())
        }
    }

    @Test
    fun `canBeReacted should return false to a peer grading that have been reported`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a peer grading that cannot be reacted") {
            val draxoPeerGrading = DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO, "explanation"),
                lastSequencePeerGrading = false,
            )
            draxoPeerGrading.reportReasons = "reportReasons" // The student has reported this peer grading, so a student can't react to it
            DraxoEvaluationModel(
                graderName = "graderName",
                graderNum = 1,
                score = null,
                draxoEvaluation = DraxoEvaluation(),
                userCanDisplayStudentsIdentity = false,
                draxoPeerGradingId = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                responseId = null,
                draxoPeerGrading = draxoPeerGrading
            )
        }.tThen("the peer grading should not be able to be reacted") {
            assertTrue(it.isReported())
            assertFalse(it.canBeReacted())
        }
    }

    @Test
    fun `canBeReacted should return true if all criteria are green`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a peer grading that can be reacted") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.YES)
                    .addEvaluation(Criteria.R, OptionId.YES)
                    .addEvaluation(Criteria.A, OptionId.YES)
                    .addEvaluation(Criteria.X, OptionId.YES)
                    .addEvaluation(Criteria.O, OptionId.NO), // All criteria are green
                lastSequencePeerGrading = false
            )
        }.tThen("the peer grading should be able to be reacted") { draxoPeerGrading ->
            val draxoEvaluationModel = DraxoEvaluationModel(
                1,
                draxoPeerGrading,
                false
            )
            assertFalse(draxoEvaluationModel.isReported())
            assertFalse(draxoEvaluationModel.hiddenByTeacher)
            assertTrue(draxoEvaluationModel.canBeReacted())
        }
    }

    @Test
    fun `canBeReacted should return true if the first criteria is NO`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a peer grading that can be reacted") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.NO,"explanation"),
                lastSequencePeerGrading = false
            )
        }.tThen("the peer grading should be able to be reacted") { draxoPeerGrading ->
            val draxoEvaluationModel = DraxoEvaluationModel(
                1,
                draxoPeerGrading,
                false
            )
            assertFalse(draxoEvaluationModel.isReported())
            assertFalse(draxoEvaluationModel.hiddenByTeacher)
            assertEquals(OptionId.NO, draxoEvaluationModel.draxoEvaluation[Criteria.D])
            assertTrue(draxoEvaluationModel.canBeReacted())
        }
    }

    @Test
    fun `canBeReacted should return false if the first criteria is DONT_KNOW`() {
        val grader = integrationTestingService.getTestStudent()
        val response = integrationTestingService.getAnyResponse()
        tGiven("a DraxoEvaluationModel with a peer grading that can be reacted") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.NO,"explanation"),
                lastSequencePeerGrading = false
            )
        }.tThen("the peer grading should be able to be reacted") { draxoPeerGrading ->
            val draxoEvaluationModel = DraxoEvaluationModel(
                1,
                draxoPeerGrading,
                false
            )
            assertFalse(draxoEvaluationModel.isReported())
            assertFalse(draxoEvaluationModel.hiddenByTeacher)
            assertEquals(OptionId.NO, draxoEvaluationModel.draxoEvaluation[Criteria.D])
            assertTrue(draxoEvaluationModel.canBeReacted())
        }
    }


}