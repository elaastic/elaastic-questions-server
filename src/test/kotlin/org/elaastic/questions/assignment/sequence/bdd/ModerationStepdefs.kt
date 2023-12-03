package org.elaastic.questions.assignment.sequence.bdd;

import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.test.FunctionalTestingService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.getAnyAssignment
import org.elaastic.questions.test.getAnySequence
import org.elaastic.questions.test.interpreter.command.Phase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ModerationStepdefs(
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val responseService: ResponseService,
    @Autowired val entityManager: EntityManager,
    val teacher: User = integrationTestingService.getTestTeacher(),
    val subject: Subject = functionalTestingService.generateSubjectWithQuestionsAndAssignments(teacher),
    val sequence: Sequence = subject.getAnyAssignment().getAnySequence(),
    var peerGrading: PeerGrading? = null
) {

    @Given("a peer grading")
    fun aPeerGrading() {

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
            .let {draxoEvaluation ->
                peerGrading = peerGradingService.createOrUpdateDraxo(grader, response, draxoEvaluation ,false)
            }
    }

    @And("the teacher owner of the sequence the peer grading belongs to")
    fun theTeacherOwnerOfTheSequenceThePeerGradingBelongsTo() {
        assertEquals(teacher, sequence.owner)
    }

    @When("The teacher hide the peer grading")
    fun theTeacherHideThePeerGrading() {
        peerGrading = entityManager.merge(peerGrading!!)
        peerGradingService.markAsHidden(peerGrading!!, teacher)
    }

    @When("The teacher remove the peer grading")
    fun theTeacherRemoveThePeerGrading() {
        peerGrading = entityManager.merge(peerGrading!!)
        peerGradingService.markAsRemoved(peerGrading!!, teacher)
    }

    @Then("the peer grading is mark as hidden")
    fun thePeerGradingIsMarkAsHidden() {
        assertTrue(peerGrading!!.hiddenByTeacher)
    }

    @Then("the peer grading is mark as removed")
    fun thePeerGradingIsMarkAsRemoved() {
        assertTrue(peerGrading!!.removedByTeacher)
    }
}
