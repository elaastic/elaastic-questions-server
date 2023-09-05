package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationRepository
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["no-async"])
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

        tWhen {
            chatGptEvaluationService.createEvaluation(response)
        }.tThen {
            MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(1L))
            MatcherAssert.assertThat(it.dateCreated, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.notNullValue())

            MatcherAssert.assertThat(it.annotation, CoreMatchers.notNullValue())
            //MatcherAssert.assertThat(it.grade, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(it.status, CoreMatchers.equalTo("DONE"))

            MatcherAssert.assertThat(it.reportReasons, CoreMatchers.nullValue())
            MatcherAssert.assertThat(it.reportComment, CoreMatchers.nullValue())
            MatcherAssert.assertThat(it.utilityGrade, CoreMatchers.nullValue())

            MatcherAssert.assertThat(it.hiddenByTeacher, CoreMatchers.equalTo(false))
            MatcherAssert.assertThat(it.removedByTeacher, CoreMatchers.equalTo(false))
        }
    }

    @AfterEach
    @Transactional
    fun cleanup() {
        chatGptEvaluationRepository.deleteAll()
    }
}