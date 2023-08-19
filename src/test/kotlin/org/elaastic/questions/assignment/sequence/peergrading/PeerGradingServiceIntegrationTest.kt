package org.elaastic.questions.assignment.sequence.peergrading

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import org.elaastic.questions.test.FunctionalTestingService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.elaastic.questions.test.getAnyAssignment
import org.elaastic.questions.test.getAnySequence
import org.elaastic.questions.test.interpreter.command.Phase
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.assertThat

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class PeerGradingServiceIntegrationTest(
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val responseService: ResponseService,
    @Autowired val entityManager: EntityManager,
) {

    @Test
    fun `save a DRAXO Peer Grading`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = functionalTestingService.generateSubjectWithQuestionsAndAssignments(teacher)
        val sequence = subject.getAnyAssignment().getAnySequence()

        val learners = integrationTestingService.getNLearners(3)
        functionalTestingService.startSequence(sequence)
        functionalTestingService.submitRandomResponses(Phase.PHASE_1, learners, sequence)
        functionalTestingService.nextPhase(sequence)


        val grader = learners.first()
        val response = responseService.findAll(sequence).getWithoutFake(1).first()

        val explanation = "That do no really answer the question"
        DraxoEvaluation()
            .addEvaluation(Criteria.D, OptionId.YES)
            .addEvaluation(Criteria.R, OptionId.PARTIALLY, explanation)
            .tWhen {draxoEvaluation ->
                val peerGrading = peerGradingService.createOrUpdateDraxo(grader, response, draxoEvaluation)
                entityManager.flush()
                entityManager.clear()
                peerGrading
            }
            .tThen {
                assertThat(it.id, notNullValue())
                assertThat(it.version, equalTo(0L))
                assertThat(it.dateCreated, notNullValue())
                assertThat(it.lastUpdated, notNullValue())
                assertThat(it.grader, equalTo(grader))
                assertThat(it.response, equalTo(response))
                assertThat(it.criteriaD, equalTo(OptionId.YES))
                assertThat(it.criteriaR, equalTo(OptionId.PARTIALLY))
                assertThat(it.criteriaA, nullValue())
                assertThat(it.criteriaX, nullValue())
                assertThat(it.criteriaO, nullValue())
                assertThat(it.annotation, equalTo(explanation))
            }
    }
}