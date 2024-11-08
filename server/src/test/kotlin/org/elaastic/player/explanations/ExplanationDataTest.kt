package org.elaastic.player.explanations

import org.elaastic.activity.evaluation.peergrading.PeerGradingRepository
import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.evaluation.peergrading.PeerGradingType
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoEvaluation
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoPeerGrading
import org.elaastic.activity.evaluation.peergrading.draxo.criteria.Criteria
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionId
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseService
import org.elaastic.material.instructional.subject.SubjectService
import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.user.User
import org.elaastic.user.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ExplanationDataTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userService: UserService,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired var responseService: ResponseService,
    @Autowired val draxoPeerGradingService: DraxoPeerGradingService,
) {


    @Test
    fun `test de la fonction getNbEvaluation`() {
        val grader: User = integrationTestingService.getNLearners(1).first()
        var response: Response = integrationTestingService.getAnyResponse()
        val teacher: User = response.statement.owner

        response = responseService.updateMeanGradeAndEvaluationCount(response)

        Assertions.assertEquals(
            1,
            response.evaluationCount
        ) //The response come from the integrationTestingService with 1 likert evaluation
        Assertions.assertEquals(0, response.draxoEvaluationCount)
        Assertions.assertEquals(0, response.draxoEvaluationHiddenCount)

        tGiven("A peerGrading of the response given by another student") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO),
                lastSequencePeerGrading = false
            ).tWhen("the peer grading is saved and varaibles are updated") { peerGrading ->
                peerGradingRepository.save(peerGrading)
                response = responseService.updateMeanGradeAndEvaluationCount(response)
                entityManager.clear()
                peerGrading
            }.tThen("variables are set as expected") { peerGrading ->
                Assertions.assertEquals(1, peerGradingService.findAllDraxo(response).size)
                Assertions.assertEquals(2, response.evaluationCount)
                Assertions.assertEquals(1, response.draxoEvaluationCount)
                Assertions.assertEquals(0, response.draxoEvaluationHiddenCount)
                peerGrading
            }.tThen("The number of evaluations should be 2 for the student and the teacher") { peerGrading ->
                response = responseService.responseRepository.findById(response.id!!).get()
                response = responseService.updateMeanGradeAndEvaluationCount(response)
                val explanationData = ExplanationData(response, false)
                Assertions.assertEquals(2, explanationData.getNbEvaluation(false))
                Assertions.assertEquals(2, explanationData.getNbEvaluation(true))
                peerGrading
            }.tWhen("The peerGrading is hidden by the teacher") { peerGrading ->
                peerGradingService.markAsHidden(teacher, peerGrading)
            }.tThen("The number of evaluations should be 1 for the student and 2 for the teacher") {
                response = responseService.responseRepository.findById(response.id!!).get()
                val explanationData = ExplanationData(response, false)
                Assertions.assertEquals(1, explanationData.getNbEvaluation(false))
                Assertions.assertEquals(2, explanationData.getNbEvaluation(true))
            }
        }

    }

    @Test
    fun getNbDraxoEvaluation() {
        val grader: User = integrationTestingService.getNLearners(1).first()
        var response: Response = integrationTestingService.getAnyResponse()
        val teacher: User = response.statement.owner

        response = responseService.updateMeanGradeAndEvaluationCount(response)

        Assertions.assertEquals(
            1,
            response.evaluationCount
        ) //The response come from the integrationTestingService with 1 likert evaluation
        Assertions.assertEquals(0, response.draxoEvaluationCount)
        Assertions.assertEquals(0, response.draxoEvaluationHiddenCount)

        tGiven("A peerGrading of the response given by another student") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO),
                lastSequencePeerGrading = false
            ).tWhen("the peer grading is saved and varaibles are updated") { draxoPeerGrading ->
                peerGradingRepository.save(draxoPeerGrading)
                response = responseService.updateMeanGradeAndEvaluationCount(response)
                entityManager.clear()
                peerGrading
            }.tThen("variables are set as expected") { peerGrading ->
                Assertions.assertEquals(1, peerGradingService.findAllDraxo(response).size)
                Assertions.assertEquals(2, response.evaluationCount)
                Assertions.assertEquals(1, response.draxoEvaluationCount)
                Assertions.assertEquals(0, response.draxoEvaluationHiddenCount)
                peerGrading
            }.tThen("The number of evaluations should be 2 for the student and the teacher") { peerGrading ->
                response = responseService.responseRepository.findById(response.id!!).get()
                response = responseService.updateMeanGradeAndEvaluationCount(response)
                val explanationData = ExplanationData(response, false)
                Assertions.assertEquals(1, explanationData.getNbDraxoEvaluation(false))
                Assertions.assertEquals(1, explanationData.getNbDraxoEvaluation(true))
                peerGrading
            }.tWhen("The peerGrading is hidden by the teacher") { peerGrading ->
                peerGradingService.markAsHidden(teacher, peerGrading)
            }.tThen("The number of draxo evaluations should be 0 for the student and 1 for the teacher") {
                response = responseService.responseRepository.findById(response.id!!).get()
                val explanationData = ExplanationData(response, false)
                Assertions.assertEquals(0, explanationData.getNbDraxoEvaluation(false))
                Assertions.assertEquals(1, explanationData.getNbDraxoEvaluation(true))
            }.tWhen("The likert evaluation is hidden by the teacher") {
                response = responseService.responseRepository.findById(response.id!!).get()
                peerGradingRepository.findAllByResponseAndType(response, PeerGradingType.LIKERT).forEach {
                    peerGradingService.markAsHidden(teacher, it)
                }
            }.tThen("The number of draxo evaluations should be 0 for the student and 1 for the teacher") {
                response = responseService.responseRepository.findById(response.id!!).get()
                val explanationData = ExplanationData(response, false)
                Assertions.assertEquals(0, explanationData.getNbDraxoEvaluation(false))
                Assertions.assertEquals(1, explanationData.getNbDraxoEvaluation(true))
            }
        }.tThen("variables are set as expected") { peerGrading ->
            assertEquals(1, draxoPeerGradingService.findAllDraxo(response).size, "There should be 1 draxo evaluation")
            assertEquals(2, response.evaluationCount, "There should be 2 evaluations (1 likert and 1 draxo)")
            assertEquals(1, response.draxoEvaluationCount, "There should be 1 draxo evaluation")
            assertEquals(0, response.draxoEvaluationHiddenCount, "There should be 0 hidden draxo evaluation")
            peerGrading
        }.tThen("The number of evaluations should be 2 for the student and the teacher") { draxoPeerGrading ->
            response = responseService.responseRepository.findById(response.id!!).get()
            response = responseService.updateMeanGradeAndEvaluationCount(response)
            val explanationData = ExplanationData(response, false)

            // As the Draxo evaluation is not hidden, the number of Draxo evaluations should be 1 for the student and the teacher
            assertEquals(
                1,
                explanationData.getNbDraxoEvaluation(false),
                "The number of Draxo evaluations should be 1 for the student"
            )
            assertEquals(
                1,
                explanationData.getNbDraxoEvaluation(true),
                "The number of Draxo evaluations should be 1 for the teacher"
            )
            draxoPeerGrading

        }.tWhen("The peerGrading is hidden by the teacher") { draxoPeerGrading ->
            peerGradingService.markAsHidden(teacher, draxoPeerGrading)

        }.tThen("The number of draxo evaluations should be 0 for the student and 1 for the teacher") {
            response = responseService.responseRepository.findById(response.id!!).get()
            val explanationData = ExplanationData(response, false)

            assertEquals(
                0,
                explanationData.getNbDraxoEvaluation(false),
                "The number of Draxo evaluations should be 0 for the student"
            )
            assertEquals(
                1,
                explanationData.getNbDraxoEvaluation(true),
                "The number of Draxo evaluations should be 1 for the teacher"
            )

        }.tWhen("The likert evaluation is hidden by the teacher") {
            response = responseService.responseRepository.findById(response.id!!).get()
            peerGradingRepository.findAllByResponseAndType(response, PeerGradingType.LIKERT).forEach {
                peerGradingService.markAsHidden(teacher, it)
            }
        }.tThen("The number of draxo evaluations should'nt change") {
            response = responseService.responseRepository.findById(response.id!!).get()
            val explanationData = ExplanationData(response, false)
            assertEquals(0, explanationData.getNbDraxoEvaluation(false))
            assertEquals(1, explanationData.getNbDraxoEvaluation(true))
        }
    }
}