package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.directory.User

object SequenceGenerator {

    fun generateAllTypes(user: User): List<Sequence> =
            ExecutionContext.values().flatMap { executionContext ->
                // sequenceState: beforeStart
                listOf(
                        SequenceInfoSetup(
                                executionContext = executionContext,
                                sequenceState = State.beforeStart
                        )
                ) +

                        // sequenceState: show
                        InteractionType.values().flatMap { interactionType ->
                            State.values().flatMap { interactionState ->
                                when (interactionType) {
                                    InteractionType.Read ->
                                        listOf(true, false).map { resultsArePublished ->
                                            SequenceInfoSetup(
                                                    executionContext = executionContext,
                                                    sequenceState = State.show,
                                                    interactionType = interactionType,
                                                    interactionState = interactionState,
                                                    resultsArePublished = resultsArePublished
                                            )
                                        }
                                    else ->
                                        listOf(
                                                SequenceInfoSetup(
                                                        executionContext = executionContext,
                                                        sequenceState = State.show,
                                                        interactionType = interactionType,
                                                        interactionState = interactionState
                                                )
                                        )
                                }
                            }
                        } +
                        // sequenceState: afterStop
                        SequenceInfoSetup(
                                executionContext = executionContext,
                                sequenceState = State.afterStop
                        )
            }.map { setup ->
                Sequence(
                        owner = user,
                        rank = 0,
                        state = setup.sequenceState,
                        statement = Statement.createDefaultStatement(user),
                        assignment = null,
                        executionContext = setup.executionContext,
                        resultsArePublished = setup.resultsArePublished
                ).let {
                    if (setup.interactionType != null) {
                        it.activeInteraction = Interaction(
                                owner = user,
                                interactionType = setup.interactionType,
                                rank = when (setup.interactionType) {
                                    InteractionType.ResponseSubmission -> 1
                                    InteractionType.Evaluation -> 2
                                    InteractionType.Read -> 3
                                },
                                sequence = it,
                                state = setup.interactionState!!
                        )
                    }

                    it
                }
            }


    data class SequenceInfoSetup(
            val executionContext: ExecutionContext,
            val sequenceState: State,
            val resultsArePublished: Boolean = false,
            val interactionType: InteractionType? = null,
            val interactionState: State? = null
    )
}