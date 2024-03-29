package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationRepository
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["no-async"])
@EnabledIf(value = "#{@featureManager.isActive(@featureResolver.getFeature('CHATGPT_EVALUATION'))}", loadContext = true)
internal class ChatGptEvaluationServiceIntegrationTest (
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
){

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    fun `get a chatgpt evaluation - valid`() {

        val response = integrationTestingService.getAnyResponse()
        response.explanation = "explanation test"

        // Precondition
        assertThat(chatGptEvaluationRepository.findAll(), `is`(empty()))

        tWhen {
            chatGptEvaluationService.createEvaluation(response, "fr")
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.version, equalTo(1L))
            assertThat(it.dateCreated, notNullValue())
            assertThat(it.lastUpdated, notNullValue())

            assertThat(it.status, equalTo("DONE"))
            assertThat(it.annotation, notNullValue())

            assertThat(it.reportReasons, nullValue())
            assertThat(it.reportComment, nullValue())
            assertThat(it.utilityGrade, nullValue())

            assertThat(it.hiddenByTeacher, equalTo(false))
            assertThat(it.removedByTeacher, equalTo(false))
        }
    }

    @AfterEach
    @Transactional
    fun cleanup() {
        chatGptEvaluationRepository.deleteAll()
    }
}