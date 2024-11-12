package org.elaastic.activity.evaluation.peergrading.draxo

import org.elaastic.activity.evaluation.peergrading.PeerGradingRepository
import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.evaluation.peergrading.draxo.criteria.Criteria
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionId
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.assignment.LearnerAssignment
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.subject.SubjectService
import org.elaastic.moderation.ReportReason
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingService
import org.elaastic.sequence.ExecutionContext
import org.elaastic.test.FunctionalTestingService
import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.test.interpreter.command.Phase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class DraxoPeerGradingServiceIntegrationTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val entityManager: EntityManager,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val draxoPeerGradingService: DraxoPeerGradingService,
    @Autowired val subjectService: SubjectService,
) {

    @Test
    fun `test of countAllReportedNotHiddenForGrader`() {
        // Given
        val learners = integrationTestingService.getNLearners(2)
        val subject = functionalTestingService.createSubject(integrationTestingService.getTestTeacher())
        functionalTestingService.addQuestion(subject, QuestionType.OpenEnded)
        val assignement = functionalTestingService.createAssignment(subject)
        val sequence = assignement.sequences.first()
        val grader = learners[1]

        tGiven("A started sequence") {
            functionalTestingService.startSequence(sequence, ExecutionContext.FaceToFace)
            val learnersAssignementList: List<LearnerAssignment> = learners.map { LearnerAssignment(it, assignement) }

            assertEquals(
                0,
                draxoPeerGradingService.countAllReportedNotHiddenForGrader(
                    sequence.getResponseSubmissionInteraction(),
                    grader
                ),
                "No peerGrading reported for the grader"
            )
            learnersAssignementList
        }.tWhen("Two learner answer and report a peerGrading") { learnerAssignments ->
            val response = functionalTestingService.submitResponse(
                Phase.PHASE_1,
                learners[0],
                sequence,
                true,
                ConfidenceDegree.CONFIDENT,
                "response"
            )

            val peerGrading = DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO, "explanation"),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.save(it)
                    it
                }
            peerGradingService.updateReport(learners[0], peerGrading, listOf(ReportReason.INCOHERENCE.name))
            learnerAssignments //learnersAssignementList
        }.tThen("the two learner who answer, are mark as so") {
            assertEquals(
                1,
                draxoPeerGradingService.countAllReportedNotHiddenForGrader(
                    sequence.getResponseSubmissionInteraction(),
                    grader
                ),
                "One peerGrading reported for the grader"
            )
            assertEquals(
                0,
                draxoPeerGradingService.countAllReportedNotHiddenForGrader(
                    sequence.getResponseSubmissionInteraction(),
                    learners[0]
                ),
                "One peerGrading reported for the grader"
            )
        }
    }

}