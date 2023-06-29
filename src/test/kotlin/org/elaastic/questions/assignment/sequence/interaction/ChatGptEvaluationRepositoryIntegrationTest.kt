package org.elaastic.questions.assignment.sequence.interaction

import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluation
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluationRepository
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class ChatGptEvaluationRepositoryIntegrationTest (
    @Autowired val chatGptEvaluationRepository : ChatGptEvaluationRepository,
    @Autowired val integrationTestingService : IntegrationTestingService,
    @Autowired val entityManager : EntityManager
){

    @Test
    fun `save a valid ChatGPT evaluation`() {
        ChatGptEvaluation(
            response = integrationTestingService.getAnyResponse()
        )
            .tWhen {
                chatGptEvaluationRepository.saveAndFlush(it)
                entityManager.refresh(it)
                it
            }
            .tThen {
                MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
                MatcherAssert.assertThat(it.dateCreated, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.lastUpdated, CoreMatchers.notNullValue())

                MatcherAssert.assertThat(it.annotation, CoreMatchers.nullValue())
                MatcherAssert.assertThat(it.grade, CoreMatchers.nullValue())
                MatcherAssert.assertThat(it.status, CoreMatchers.nullValue())

                MatcherAssert.assertThat(it.reportReasons, CoreMatchers.nullValue())
                MatcherAssert.assertThat(it.reportComment, CoreMatchers.nullValue())
                MatcherAssert.assertThat(it.utilityGrade, CoreMatchers.nullValue())

                MatcherAssert.assertThat(it.hiddenByTeacher, CoreMatchers.equalTo(false))
                MatcherAssert.assertThat(it.removedByTeacher, CoreMatchers.equalTo(false))
            }
    }

}