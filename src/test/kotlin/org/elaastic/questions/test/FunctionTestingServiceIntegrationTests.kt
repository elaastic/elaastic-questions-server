package org.elaastic.questions.test

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.test.interpreter.command.PublishResults
import org.elaastic.questions.test.interpreter.command.StartSequence
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class FunctionTestingServiceIntegrationTests(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val functionalTestingService: FunctionalTestingService,
) {

    @Test
    fun `3 sequences should be started`() {
        val teacher = integrationTestingService.getTestTeacher()
        val subject = functionalTestingService.generateSubject(teacher)

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
        val subject = functionalTestingService.generateSubject(teacher)
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
}