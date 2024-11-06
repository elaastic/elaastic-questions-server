package org.elaastic.ai.evaluation.chatgpt

import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.EnabledIf
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@EnabledIf(value = "#{@featureManager.isActive(@featureResolver.getFeature('CHATGPT_EVALUATION'))}", loadContext = true)
internal class ChatGptEvaluationRepositoryIntegrationTest(
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val entityManager: EntityManager
) {

    @BeforeEach
    fun setup() {
        chatGptEvaluationRepository.deleteAll()
        // Precondition
        assertThat(chatGptEvaluationRepository.findAll(), `is`(empty()))
    }

    @Test
    fun `save a valid ChatGPT evaluation`() {

        ChatGptEvaluation(
            response = integrationTestingService.getAnyResponse()
        )
            .tWhen {
                chatGptEvaluationRepository.save(it)
                it
            }
            .tThen {
                MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.dateCreated, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.notNullValue())

                MatcherAssert.assertThat(it.annotation, CoreMatchers.nullValue())
                MatcherAssert.assertThat(it.grade, CoreMatchers.nullValue())
                MatcherAssert.assertThat(it.status, CoreMatchers.equalTo(ChatGptEvaluationStatus.UNKNOWN.name))

                MatcherAssert.assertThat(it.reportReasons, CoreMatchers.nullValue())
                MatcherAssert.assertThat(it.reportComment, CoreMatchers.nullValue())
                MatcherAssert.assertThat(it.utilityGrade, CoreMatchers.nullValue())

                MatcherAssert.assertThat(it.hiddenByTeacher, CoreMatchers.equalTo(false))
                MatcherAssert.assertThat(it.removedByTeacher, CoreMatchers.equalTo(false))
            }
    }

}