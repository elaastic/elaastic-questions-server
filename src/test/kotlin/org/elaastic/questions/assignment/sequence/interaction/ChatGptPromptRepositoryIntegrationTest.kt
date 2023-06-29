package org.elaastic.questions.assignment.sequence.interaction

import org.ehcache.config.ResourceType.Core
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.ChatGptEvaluation
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptPrompt.ChatGptPrompt
import org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptPrompt.ChatGptPromptRepository
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
internal class ChatGptPromptRepositoryIntegrationTest (
    @Autowired val chatGptPromptRepository : ChatGptPromptRepository,
    @Autowired val integrationTestingService : IntegrationTestingService,
    @Autowired val entityManager : EntityManager
){

    @Test
    fun `save a valid ChatGPT evaluation - only content`() {
        ChatGptPrompt(
            content = "test prompt"
        )
            .tWhen {
                chatGptPromptRepository.saveAndFlush(it)
                entityManager.refresh(it)
                it
            }
            .tThen {
                MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
                MatcherAssert.assertThat(it.startDate, CoreMatchers.notNullValue())
                MatcherAssert.assertThat(it.endDate, CoreMatchers.nullValue())

                MatcherAssert.assertThat(it.active, CoreMatchers.equalTo(true))
                MatcherAssert.assertThat(it.content, CoreMatchers.equalTo("test prompt"))
                MatcherAssert.assertThat(it.language, CoreMatchers.equalTo("fr"))
            }
    }

}