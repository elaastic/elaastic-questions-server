package org.elaastic.player.assignmentview

import org.elaastic.activity.evaluation.peergrading.PeerGradingRepository
import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ResponseService
import org.elaastic.material.instructional.statement.StatementRepository
import org.elaastic.material.instructional.subject.SubjectRepository
import org.elaastic.material.instructional.subject.SubjectService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingService
import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionService
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.test.FunctionalTestingService
import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.user.RoleService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AssignmentOverviewModelFactoryIntegrationTest(
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val responseService: ResponseService,
    @Autowired val entityManager: EntityManager,
    @Autowired val roleService: RoleService,
    @Autowired val interactionService: InteractionService,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val draxoPeerGradingService: DraxoPeerGradingService,
    @Autowired val statementRepository: StatementRepository,
    @Autowired val subjectRepository: SubjectRepository,
    @Autowired val subjectService: SubjectService,
) {
    val chart = AssignmentOverviewModelFactory.chartIcon
    val lock = AssignmentOverviewModelFactory.lockIcon
    val minus = AssignmentOverviewModelFactory.minusIcon
    val comment = AssignmentOverviewModelFactory.commentIcon
    val comments = AssignmentOverviewModelFactory.commentsIcon

    fun testResolveIcons(
        expectedIcons: List<AssignmentOverviewModel.PhaseIcon>,
        isTeacher: Boolean,
        sequence: Sequence,
        message: String = "",
    ) {
        val actualIcons = AssignmentOverviewModelFactory.resolveIcons(isTeacher, sequence)
        assertEquals(expectedIcons, actualIcons, message)
    }

    @Test
    fun `test resolveIcons with sequence and Interaction in Face to Face context`() {
        lateinit var sequence: Sequence
        tGiven("a sequence") {
            val teacher = integrationTestingService.getTestTeacher()
            sequence = functionalTestingService.generateSequence(teacher)

            assertEquals(sequence.state, State.beforeStart, "A sequence who is not started, should have a state beforeStart")
            assertNull(sequence.activeInteraction, "A sequence who is not started, should not have an active interaction")
            assertNull(sequence.activeInteractionType, "A sequence who is not started, should not have an active interaction type")

            testResolveIcons(listOf(minus), true, sequence, message = "A sequence who is not started, should have a minus icon")
        }.tWhen ("The sequence is start with Face to Face context") {
            functionalTestingService.startSequence(sequence, ExecutionContext.FaceToFace)
        }.tThen ("Then the first interaction type is ResponseSubmission, is state is show and the active interaction is not null") {
            assertEquals(sequence.state, State.show, "A sequence who is started, should have the `show` state")
            assertNotNull(sequence.activeInteraction, "A sequence who is started, should have an active interaction")
            assertEquals(sequence.activeInteraction?.state, State.show, "The active interaction should be in the `show` state")
            assertNotNull(sequence.activeInteractionType, "A sequence who is started, should have an active interaction type")
            assertEquals(sequence.activeInteractionType, sequence.activeInteraction?.interactionType, "The active interaction type should be the same as the active interaction")
            assertEquals(sequence.activeInteractionType, InteractionType.ResponseSubmission, "A sequence who is started, should have an active interaction type of ResponseSubmission")

            testResolveIcons(listOf(comment), true, sequence, message = "A sequence who is just started in Face to Face context, should have a comment icon")
        }.tWhen ("We stop the first interaction") {
            functionalTestingService.stopPhase(sequence)
        }.tThen ("The sequence isn't stop but the active interaction is") {
            assertEquals(sequence.state, State.show, "Until it stop a sequence who is started, should have the `show` state")
            assertNotNull(sequence.activeInteraction, "A sequence who is started, should have an active interaction")
            assertEquals(sequence.activeInteraction?.state, State.afterStop, "The active interaction should be in the `afterStop` state")
            assertNotNull(sequence.activeInteractionType, "A sequence who is started, should have an active interaction type")
            assertEquals(sequence.activeInteractionType, sequence.activeInteraction?.interactionType, "The active interaction type should be the same as the active interaction")
            assertEquals(sequence.activeInteractionType, InteractionType.ResponseSubmission, "When we stop the first interaction, the active interaction type still should be ResponseSubmission")

            testResolveIcons(listOf(comment), true, sequence, message = "A sequence in Face to Face context and in ResponsSubmission phase, despite the interaction is stopped, should have a comment icon")
        }.tWhen ("We start the second interaction") {
            functionalTestingService.startNextPhase(sequence)
        }.tThen ("The sequence as a new active interaction of type Evaluation") {
            assertEquals(sequence.state, State.show, "A sequence who is started, should have the `show` state")

            assertNotNull(sequence.activeInteraction, "A sequence who is started, should have an active interaction")
            assertEquals(sequence.activeInteraction?.state, State.show, "The active interaction should be in the `show` state")

            assertNotNull(sequence.activeInteractionType, "A sequence who is started, should have an active interaction type")
            assertEquals(sequence.activeInteractionType, sequence.activeInteraction?.interactionType, "The active interaction type should be the same as the active interaction")
            assertEquals(sequence.activeInteractionType, InteractionType.Evaluation, "The second interaction type of a sequence is Evaluation")

            testResolveIcons(listOf(comments), true, sequence, message = "A sequence in Face to Face context and in Evaluation phase, should have a comments icon")
        }.tWhen ("We start the next interaction") {
            functionalTestingService.nextPhase(sequence)
        }.tThen ("The sequence as a new active interaction of type Read") {
            assertEquals(sequence.state, State.show, "A sequence who is started, should have the `show` state")

            assertNotNull(sequence.activeInteraction, "A sequence who is started, should have an active interaction")
            assertEquals(sequence.activeInteraction?.state, State.show, "The active interaction should be in the `show` state")

            assertNotNull(sequence.activeInteractionType, "A sequence who is started, should have an active interaction type")
            assertEquals(sequence.activeInteractionType, sequence.activeInteraction?.interactionType, "The active interaction type should be the same as the active interaction")
            assertEquals(sequence.activeInteractionType, InteractionType.Read, "The third interaction type of a sequence is Read")

            testResolveIcons(listOf(chart), true, sequence, message = "A sequence in Face to Face context and in Read phase, should have a chart icon")
        }.tWhen ("We stop the serquence and the result are published") {
            functionalTestingService.stopSequence(sequence)
            functionalTestingService.publishResults(sequence)
        }.tThen ("The sequence is stopped and the result are published") {
            assertEquals(sequence.state, State.afterStop, "A sequence who is stopped, should have the `afterStop` state")
            assertTrue(sequence.resultsArePublished, "The results should be published")

            testResolveIcons(listOf(chart), true, sequence, message = "A sequence who is stopped and the result are published should have a chart icon")
        }.tWhen ("We unpublished the result") {
            functionalTestingService.unpublishResults(sequence)
        }.tThen {
            assertFalse(sequence.resultsArePublished, "The results should not be published")

            testResolveIcons(listOf(lock), true, sequence, message = "A sequence who is stopped and the result are not published should have a lock icon")
        }
    }

    @Test
    fun `test resolveIcons with sequence and Interaction in Distance context`() {
        val executionContext = ExecutionContext.Distance

        lateinit var sequence: Sequence
        tGiven("a sequence") {
            val teacher = integrationTestingService.getTestTeacher()
            sequence = functionalTestingService.generateSequence(teacher)

            assertEquals(sequence.state, State.beforeStart, "A sequence who is not started, should have a state beforeStart")
            assertNull(sequence.activeInteraction, "A sequence who is not started, should not have an active interaction")
            assertNull(sequence.activeInteractionType, "A sequence who is not started, should not have an active interaction type")

            testResolveIcons(listOf(minus), true, sequence, message = "A sequence who is not started, should have a minus icon")
        }.tWhen ("The sequence is start with Distance context") {
            functionalTestingService.startSequence(sequence, executionContext)
        }.tThen ("Then all interactions are in show state, the active interaction is not null and is a Read and the result are not published") {
            assertEquals(sequence.state, State.show, "A sequence who is started, should have the `show` state")
            assertNotNull(sequence.activeInteraction, "A sequence who is started, should have an active interaction")
            sequence.interactions.values.forEach {
                assertEquals(it.state, State.show, "All interactions should be in the `show` state")
            }
            assertEquals(sequence.activeInteraction?.state, State.show, "The active interaction should be in the `show` state")
            assertNotNull(sequence.activeInteractionType, "A sequence who is started, should have an active interaction type")
            assertEquals(sequence.activeInteractionType, sequence.activeInteraction?.interactionType, "The active interaction type should be the same as the active interaction")
            assertEquals(sequence.activeInteractionType, InteractionType.Read, "A sequence who is started, should have an active interaction type of Read")

            assertTrue(sequence.resultsArePublished, "The results should be published")

            testResolveIcons(listOf(comment, comments, chart), true, sequence, message = "A sequence who is just started in Distance context, should have a comment icon")
        }.tWhen("We unpublished the result") {
            functionalTestingService.unpublishResults(sequence)
        }.tThen("The icons are comment, comments") {
            assertFalse(sequence.resultsArePublished, "The results shouldn't be published")

            testResolveIcons(listOf(comment, comments), true, sequence, message = "A sequence who is started in Distance context and the result are published, should have a comment, comments and chart icon")
        }.tWhen("We stop the sequence") {
            functionalTestingService.stopSequence(sequence)
        }.tThen("The icon is lock") {
            assertFalse(sequence.resultsArePublished, "The results should not be published")

            testResolveIcons(listOf(lock), true, sequence, message = "A sequence who is stopped and the result are not published should have a lock icon")
        }.tWhen("We published the result") {
            functionalTestingService.publishResults(sequence)
        }.tThen("The icon is chart") {
            assertEquals(sequence.state, State.afterStop, "A sequence who is stopped, should have the `afterStop` state")
            assertTrue(sequence.resultsArePublished, "The results should be published")

            testResolveIcons(listOf(chart), true, sequence, message = "A sequence who is stopped and the result are published should have a chart icon")
        }
    }

    @Test
    fun `test resolveIcons with sequence and Interaction in Blended context`() {
        val executionContext = ExecutionContext.Blended

        lateinit var sequence: Sequence
        tGiven("a sequence") {
            val teacher = integrationTestingService.getTestTeacher()
            sequence = functionalTestingService.generateSequence(teacher)

            assertEquals(sequence.state, State.beforeStart, "A sequence who is not started, should have a state beforeStart")
            assertNull(sequence.activeInteraction, "A sequence who is not started, should not have an active interaction")
            assertNull(sequence.activeInteractionType, "A sequence who is not started, should not have an active interaction type")

            testResolveIcons(listOf(minus), true, sequence, message = "A sequence who is not started, should have a minus icon")
        }.tWhen ("The sequence is start with Distance context") {
            functionalTestingService.startSequence(sequence, executionContext)
        }.tThen ("Then all interactions are in show state (except the Read interaction), the active interaction is not null and is a Read and the result are not published") {
            assertEquals(sequence.state, State.show, "A sequence who is started, should have the `show` state")
            assertNotNull(sequence.activeInteraction, "A sequence who is started, should have an active interaction")
            sequence.interactions.values.forEach {
                if (it.interactionType != InteractionType.Read) {
                    assertEquals(it.state, State.show, "The ${it.interactionType} interaction should be in the `show` state")
                } else {
                    assertEquals(it.state, State.beforeStart, "The ${it.interactionType} interaction should be in the `beforeStart` state")
                }
            }

            assertEquals(sequence.activeInteraction?.state, State.beforeStart, "The active interaction (Read) should be in the `show` state")
            assertNotNull(sequence.activeInteractionType, "A sequence who is started, should have an active interaction type")
            assertEquals(sequence.activeInteractionType, sequence.activeInteraction?.interactionType, "The active interaction type should be the same as the active interaction")
            assertEquals(sequence.activeInteractionType, InteractionType.Read, "A sequence who is started, should have an active interaction type of Read")

            assertFalse(sequence.resultsArePublished, "The results shouldn't be published")

            testResolveIcons(listOf(comment, comments), true, sequence, message = "A sequence who is just started in Distance context, should have a comment and a comments icon")
        }.tWhen("We published the result") {
            functionalTestingService.publishResults(sequence)
        }.tThen("The icons are comment, comments, chart") {
            assertTrue(sequence.resultsArePublished, "The results should be published")

            testResolveIcons(listOf(comment, comments, chart), true, sequence, message = "A sequence who is started in Distance context and the result are published, should have a comment, comments and chart icon")
        }.tWhen("We stop the sequence") {
            functionalTestingService.stopSequence(sequence)
        }.tThen("The icon is chart") {
            assertEquals(sequence.state, State.afterStop, "A sequence who is stopped, should have the `afterStop` state")
            assertTrue(sequence.resultsArePublished, "The results should be published")

            testResolveIcons(listOf(chart), true, sequence, message = "A sequence who is stopped and the result are published should have a chart icon")
        }.tWhen("We unpublished the result") {
            functionalTestingService.unpublishResults(sequence)
        }.tThen("The icon is lock") {
            assertFalse(sequence.resultsArePublished, "The results should not be published")

            testResolveIcons(listOf(lock), true, sequence, message = "A sequence who is stopped and the result are not published should have a lock icon")
        }
    }

}