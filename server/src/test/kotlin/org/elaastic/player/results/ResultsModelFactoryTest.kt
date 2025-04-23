package org.elaastic.player.results

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.elaastic.activity.response.ResponseSet
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationResponseStore
import org.elaastic.common.abtesting.ElaasticFeatures
import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.common.web.MessageBuilder
import org.elaastic.material.instructional.question.*
import org.elaastic.material.instructional.question.QuestionType.*
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.config.EvaluationSpecification
import org.elaastic.sequence.config.ReadSpecification
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.sequence.interaction.InteractionType.*
import org.elaastic.user.User
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test


class ResultsModelFactoryTest {

    @Test
    fun `test build with and without choices`() {

        mockkObject(ElaasticFeatures.RECOMMENDATIONS)
        every { ElaasticFeatures.RECOMMENDATIONS.isActive() } returns false

        val responseSet = createResponseSet()

        assertInstanceOf<ChoiceResultsModel>(
            ResultsModelFactory.build(
                teacher = true,
                sequence = createSequence(MultipleChoice, MultipleChoiceSpecification(2)),
                responseSet = responseSet,
                userCanRefreshResults = true,
                messageBuilder = mockk<MessageBuilder>(),
                chatGptEvaluationResponseStore = mockk<ChatGptEvaluationResponseStore>()
            )
        )

        assertInstanceOf<ChoiceResultsModel>(
            ResultsModelFactory.build(
                teacher = true,
                sequence = createSequence(ExclusiveChoice, ExclusiveChoiceSpecification(2, ChoiceItem(1, 1f))),
                responseSet = responseSet,
                userCanRefreshResults = true,
                messageBuilder = mockk<MessageBuilder>(),
                chatGptEvaluationResponseStore = mockk<ChatGptEvaluationResponseStore>()
            )
        )

        assertInstanceOf<OpenResultsModel>(
            ResultsModelFactory.build(
                teacher = true,
                sequence = createSequence(OpenEnded, null),
                responseSet = responseSet,
                userCanRefreshResults = true,
                messageBuilder = mockk<MessageBuilder>(),
                chatGptEvaluationResponseStore = mockk<ChatGptEvaluationResponseStore>()
            )
        )
    }

    @Test
    fun `test build with choices and RECOMMENDATIONS`() {

        mockkObject(ElaasticFeatures.RECOMMENDATIONS)
        every { ElaasticFeatures.RECOMMENDATIONS.isActive() } returns true

        val responseSet = createResponseSet()

        assertInstanceOf<ChoiceResultsModel>(
            ResultsModelFactory.build(
                teacher = true,
                sequence = createSequence(MultipleChoice, MultipleChoiceSpecification(2)),
                responseSet = responseSet,
                userCanRefreshResults = true,
                messageBuilder = mockk<MessageBuilder>(),
                chatGptEvaluationResponseStore = mockk<ChatGptEvaluationResponseStore>()
            )
        )

        assertInstanceOf<ChoiceResultsModel>(
            ResultsModelFactory.build(
                teacher = true,
                sequence = createSequence(ExclusiveChoice, ExclusiveChoiceSpecification(2, ChoiceItem(1, 1f))),
                responseSet = responseSet,
                userCanRefreshResults = true,
                messageBuilder = mockk<MessageBuilder>(),
                chatGptEvaluationResponseStore = mockk<ChatGptEvaluationResponseStore>()
            )
        )
    }

    private fun createSequence(
        questionType: QuestionType,
        choiceSpecification: ChoiceSpecification?
    ): Sequence {
        return Sequence(
            owner = mockk<User>(),
            statement = Statement(
                owner = mockk<User>(),
                questionType = questionType,
                choiceSpecification = choiceSpecification
            )
        ).also {
            it.id = 1L
            it.interactions = createInteractions(it)
        }
    }

    private fun createInteractions(it: Sequence): MutableMap<InteractionType, Interaction> {
        return listOf(
            Interaction(ResponseSubmission, 1, ResponseSubmissionSpecification(true, true), mockk<User>(), it),
            Interaction(Evaluation, 2, EvaluationSpecification(1), mockk<User>(), it),
            Interaction(Read, 3, ReadSpecification(), mockk<User>(), it),
        )
            .onEach { interaction -> interaction.alsoSetId(interaction.rank.toLong()) }
            .associateBy { interaction -> interaction.interactionType }
            .toMutableMap()
    }

    private fun createResponseSet(): ResponseSet {
        return ResponseSet(
            responses = emptyList()
        )
    }

    private fun <T : AbstractJpaPersistable<Long>> T.alsoSetId(givenId: Long = 1): T {
        return this.also { it.id = givenId }
    }

    private inline fun <reified T> assertInstanceOf(actualValue: Any) =
        assertInstanceOf(T::class.java, actualValue)
}