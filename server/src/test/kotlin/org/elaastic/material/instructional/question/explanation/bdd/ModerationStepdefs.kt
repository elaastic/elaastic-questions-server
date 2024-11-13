package org.elaastic.material.instructional.question.explanation.bdd;

import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.elaastic.activity.evaluation.peergrading.PeerGrading
import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoEvaluation
import org.elaastic.activity.evaluation.peergrading.draxo.criteria.Criteria
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionId
import org.elaastic.activity.response.ResponseService
import org.elaastic.material.instructional.subject.Subject
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingService
import org.elaastic.sequence.Sequence
import org.elaastic.test.FunctionalTestingService
import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.getAnyAssignment
import org.elaastic.test.getAnySequence
import org.elaastic.test.interpreter.command.Phase
import org.elaastic.user.User
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ModerationStepdefs(
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val responseService: ResponseService,
    @Autowired val entityManager: EntityManager,
    @Autowired val draxoPeerGradingService: DraxoPeerGradingService,
) {
    lateinit var teacher: User
    lateinit var subject: Subject
    lateinit var sequence: Sequence
    lateinit var peerGrading: PeerGrading

    @Given("a peer grading")
    fun aPeerGrading() {

        teacher = integrationTestingService.getTestTeacher()
        subject = functionalTestingService.generateSubjectWithQuestionsAndAssignments(teacher)
        sequence = subject.getAnyAssignment().getAnySequence()

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
            .let { draxoEvaluation ->
                peerGrading = draxoPeerGradingService.createOrUpdateDraxo(grader, response, draxoEvaluation, false)
            }
    }

    @And("the teacher owner of the sequence the peer grading belongs to")
    fun theTeacherOwnerOfTheSequenceThePeerGradingBelongsTo() {
        assertEquals(teacher, sequence.owner)
    }

    @When("The teacher hide the peer grading")
    fun theTeacherHideThePeerGrading() {
        peerGrading = entityManager.merge(peerGrading!!)
        peerGradingService.markAsHidden(teacher, peerGrading!!)
    }

    @When("The teacher remove the peer grading")
    fun theTeacherRemoveThePeerGrading() {
        peerGrading = entityManager.merge(peerGrading!!)
        peerGradingService.markAsRemoved(teacher, peerGrading!!)
    }

    @Then("the peer grading is mark as hidden")
    fun thePeerGradingIsMarkAsHidden() {
        assertTrue(peerGrading!!.hiddenByTeacher)
    }

    @Then("the peer grading is mark as removed")
    fun thePeerGradingIsMarkAsRemoved() {
        assertTrue(peerGrading!!.removedByTeacher)
    }

    @Given("the learner owner of the response the peer grading belongs to")
    fun the_learner_owner_of_the_response_the_peer_grading_belongs_to() {
        assertNotNull(peerGrading.response.learner)
    }

    @When("The learner report the peer grading without comment")
    fun the_learner_report_the_peer_grading_without_comment() {
        val learner = peerGrading.response.learner
        peerGradingService.updateReport(learner, peerGrading, listOf("reason1", "reason2"))
    }

    @When("The learner report the peer grading with comment")
    fun the_learner_report_the_peer_grading_with_comment() {
        val learner = peerGrading.response.learner
        peerGradingService.updateReport(learner, peerGrading, listOf("reason1", "reason2"), "a comment")
    }

    @Then("the peer grading has report reason")
    fun the_peer_grading_has_report_reason() {
        assertNotNull(peerGrading.reportReasons)
        assertFalse(peerGrading.reportReasons!!.isEmpty())
    }

    @Then("the peer grading have no comment in the report")
    fun the_peer_grading_have_no_comment_in_the_report() {
        assertNull(peerGrading.reportComment)
    }

    @Then("the peer grading have a comment attached to the report")
    fun the_peer_grading_have_a_comment_attached_to_the_report() {
        assertNotNull(peerGrading.reportComment)
    }
}
