package org.elaastic.player.results

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationResponseStore
import org.elaastic.common.web.MessageBuilder
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.sequence.State
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionRepository
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.test.FunctionalTestingService
import org.elaastic.test.IntegrationTestingService
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
class ResultsModelFactoryIntegrationTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val messageBuilder: MessageBuilder,
    @Autowired val interactionRepository: InteractionRepository,
) {

    @Test
    fun `test build`() {
        val sequence = integrationTestingService.getAnySequence()
            .also {
                it.statement.questionType = QuestionType.MultipleChoice
                val interaction = Interaction(
                    interactionType = InteractionType.ResponseSubmission,
                    rank = 1,
                    owner = it.owner,
                    sequence = it,
                    state = State.show,
                    specification = ResponseSubmissionSpecification(true, true)
                ).also(interactionRepository::save)
                it.interactions[InteractionType.ResponseSubmission] = interaction
                it.activeInteraction = interaction
            }

        val responseSet = functionalTestingService.createResponseSet(sequence, 2)

        val resultsModel = ResultsModelFactory.build(
            teacher = true,
            sequence = sequence,
            responseSet = responseSet,
            userCanRefreshResults = true,
            messageBuilder = messageBuilder,
            chatGptEvaluationResponseStore = ChatGptEvaluationResponseStore()
        )

        assertNotNull(resultsModel)
        assertInstanceOf(ChoiceResultsModel::class.java, resultsModel)
    }
}