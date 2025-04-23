package org.elaastic.test

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.elaastic.activity.response.Response
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionRepository
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.test.interpreter.command.NextPhase
import org.elaastic.test.interpreter.command.PublishResults
import org.elaastic.test.interpreter.command.StartSequence
import org.elaastic.test.interpreter.command.StopSequence
import org.elaastic.user.User
import org.elaastic.user.UserRepository
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.test.util.ReflectionTestUtils
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class FunctionTestingServiceIntegrationTests(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val interactionRepository: InteractionRepository,
) {
    @SpyBean
    lateinit var userRepositorySpy: UserRepository

    @Test
    fun `3 sequences should be started`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = functionalTestingService.generateSubjectWithQuestionsAndAssignmentsReadyToPratice(teacher)

        val sequences = subject.getAnyAssignment().getAnyNSequences(3)

        ExecutionContext.values().forEachIndexed { index, executionContext ->
            assertThat(sequences[index].isNotStarted(), equalTo(true))

            functionalTestingService.executeScript(
                sequences[index].id!!,
                listOf(
                    StartSequence(executionContext)
                )
            )
            assertThat(sequences[index].isNotStarted(), equalTo(false))
            assertThat(sequences[index].executionContext, equalTo(executionContext))
        }
    }

    @Test
    fun `publish the results of a sequence`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = functionalTestingService.generateSubjectWithQuestionsAndAssignmentsReadyToPratice(teacher)
        val sequence = subject.getAnyAssignment().getAnySequence()

        assertThat(sequence.resultsArePublished, equalTo(false))

        functionalTestingService.executeScript(
            sequence.id!!,
            listOf(
                StartSequence(ExecutionContext.Blended),
                PublishResults()
            )
        )

        assertThat(sequence.resultsArePublished, equalTo(true))
    }

    @Test
    fun `stop a sequence`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = functionalTestingService.generateSubjectWithQuestionsAndAssignmentsReadyToPratice(teacher)
        val sequence = subject.getAnyAssignment().getAnySequence()

        assertThat(sequence.isNotStarted(), equalTo(true))
        assertThat(sequence.isStopped(), equalTo(false))

        functionalTestingService.executeScript(
            sequence.id!!,
            listOf(
                StartSequence(ExecutionContext.Blended),
                StopSequence(),
            )
        )

        assertThat(sequence.isStopped(), equalTo(true))
    }

    @Test
    fun `start the next phase`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = functionalTestingService.generateSubjectWithQuestionsAndAssignmentsReadyToPratice(teacher)
        val sequence = subject.getAnyAssignment().getAnySequence()

        assertThat(sequence.activeInteraction, equalTo(null))

        functionalTestingService.executeScript(
            sequence.id!!,
            listOf(
                StartSequence(ExecutionContext.FaceToFace),
            )
        )

        assertThat(sequence.activeInteraction?.isResponseSubmission(), equalTo(true))

        functionalTestingService.executeScript(
            sequence.id!!,
            listOf(
                NextPhase(),
            )
        )

        assertThat(sequence.activeInteraction?.isEvaluation(), equalTo(true))

        functionalTestingService.executeScript(
            sequence.id!!,
            listOf(
                NextPhase(),
            )
        )

        assertThat(sequence.activeInteraction?.isRead(), equalTo(true))
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `test creatResponseSet`() {
        val sequence = integrationTestingService.getAnySequence()
            .also {
                it.statement.questionType = QuestionType.MultipleChoice
                val interaction = Interaction(
                    interactionType = InteractionType.ResponseSubmission,
                    rank = 1,
                    owner = it.owner,
                    sequence = it,
                    state = State.show
                ).also(interactionRepository::save)
                it.interactions[InteractionType.ResponseSubmission] = interaction
                it.activeInteraction = interaction
            }

        val responseSet = functionalTestingService.createResponseSet(sequence, 2)

        verify(userRepositorySpy, times(2)).save(any<User>())

        val responseByAttempt =
            ReflectionTestUtils.getField(responseSet, "responsesByAttempt") as Array<MutableList<Response>>
        val actualSize = responseByAttempt[0].size + responseByAttempt[1].size
        assertEquals(2, actualSize)
    }
}