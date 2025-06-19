/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.player.command

import io.mockk.mockk
import org.elaastic.alsoSetId
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.player.command.CommandModel.ActionStatus
import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.user.User
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CommandModelFactoryTest {

    @Test
    fun `test build without id`() {
        tGiven("A sequence without id") {
            getASequence()
                .also { it.id = null }
        }.tWhen("We try to build the command model") {
            { CommandModelFactory.build(mockk<User>(), it) }
        }.tThen("An error occurs") {
            assertThrows<IllegalStateException> {
                it()
            }
        }

        tGiven("A statement without id") {
            getASequence()
                .also { it.statement.id = null }
        }.tWhen("We try to build the command model") {
            { CommandModelFactory.build(mockk<User>(), it) }
        }.tThen("An error occurs") {
            assertThrows<IllegalStateException> {
                it()
            }
        }
    }

    @Test
    fun `test build with ExpectedExplanation`() {
        tGiven("A statement with an expected explanation") {
            getASequence()
                .also { it.statement.expectedExplanation = "Expected explanation" }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertTrue(it.hasExpectedExplanation)
        }

        tGiven("A statement without an expected explanation") {
            getASequence()
                .also { it.statement.expectedExplanation = "" }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertFalse(it.hasExpectedExplanation)
        }

        tGiven("A statement without an expected explanation") {
            getASequence()
                .also { it.statement.expectedExplanation = null }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertFalse(it.hasExpectedExplanation)
        }
    }

    @Test
    fun `test build with a not started sequence`() {
        tGiven("A sequence that is not started") {
            getASequence()
                .also { it.state = State.beforeStart }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen("The command model should allow starting starting the sequence only") {
            assertEquals(ActionStatus.ENABLED, it.actionStartSequence)

            assertEquals(ActionStatus.HIDDEN, it.actionStartInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStopInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartNextInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionStopSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionPublishResults)
            assertEquals(ActionStatus.HIDDEN, it.actionUnpublishResults)
        }
    }

    @Test
    fun `test build with ResponseSubmission shown`() {
        tGiven("A sequence started with ResponseSubmission interaction") {
            getASequence()
                .addDefaultInteraction(InteractionType.ResponseSubmission, State.show)
                .also { it.executionContext = ExecutionContext.FaceToFace }
                .also { it.state = State.show }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen("The command model should allow stopping the interaction and sequence") {
            assertEquals(ActionStatus.DISABLED, it.actionStartInteraction)
            assertEquals(ActionStatus.ENABLED, it.actionStopInteraction)
            assertEquals(ActionStatus.ENABLED, it.actionStopSequence)

            assertEquals(ActionStatus.HIDDEN, it.actionStartSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionStartNextInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionPublishResults)
            assertEquals(ActionStatus.HIDDEN, it.actionUnpublishResults)
        }
    }

    @Test
    fun `test build with ResponseSubmission beforeStart`() {
        tGiven("A sequence started with ResponseSubmission beforeStart") {
            getASequence()
                .addDefaultInteraction(InteractionType.ResponseSubmission, State.beforeStart)
                .also { it.executionContext = ExecutionContext.FaceToFace }
                .also { it.state = State.show }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertEquals(ActionStatus.ENABLED, it.actionStartInteraction)
            assertEquals(ActionStatus.ENABLED, it.actionStopSequence)

            assertEquals(ActionStatus.HIDDEN, it.actionStopInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionStartNextInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionPublishResults)
            assertEquals(ActionStatus.HIDDEN, it.actionUnpublishResults)
        }
    }

    @Test
    fun `test build with ResponseSubmission afterStop`() {
        tGiven("A sequence started with ResponseSubmission afterStop") {
            getASequence()
                .addDefaultInteraction(InteractionType.ResponseSubmission, State.afterStop)
                .also { it.executionContext = ExecutionContext.FaceToFace }
                .also { it.state = State.show }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertEquals(ActionStatus.ENABLED, it.actionStopSequence)
            assertEquals(ActionStatus.ENABLED, it.actionStartNextInteraction)
            assertEquals(ActionStatus.ENABLED, it.actionReopenInteraction)

            assertEquals(ActionStatus.HIDDEN, it.actionStartInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStopInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionPublishResults)
            assertEquals(ActionStatus.HIDDEN, it.actionUnpublishResults)
        }
    }

    @Test
    fun `test build with sequence stopped and can't be reopen`() {
        tGiven("A sequence started with Read afterStop and FaceToFace mode") {
            getASequence()
                .addDefaultInteraction(InteractionType.Read, State.afterStop)
                .also { it.executionContext = ExecutionContext.FaceToFace }
                .also { it.state = State.afterStop }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertEquals(ActionStatus.HIDDEN, it.actionReopenSequence)
            assertEquals(ActionStatus.ENABLED, it.actionPublishResults)

            assertEquals(ActionStatus.HIDDEN, it.actionStopSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionStartNextInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStopInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionUnpublishResults)
        }
    }

    @Test
    fun `test build with sequence stopped but can be reopen`() {
        tGiven("A sequence in Distance mode") {
            getASequence()
                .addDefaultInteraction()
                .also { it.executionContext = ExecutionContext.Distance }
                .also { it.state = State.afterStop }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertEquals(ActionStatus.ENABLED, it.actionReopenSequence)
            assertEquals(ActionStatus.ENABLED, it.actionPublishResults)

            assertEquals(ActionStatus.HIDDEN, it.actionStopSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionStartNextInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStopInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionUnpublishResults)
        }
    }

    @Test
    fun `test build with sequence that can be stopped`() {
        tGiven("A sequence shown") {
            getASequence()
                .also { it.state = State.show }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertEquals(ActionStatus.ENABLED, it.actionStopSequence)

            assertEquals(ActionStatus.HIDDEN, it.actionReopenSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionPublishResults)
            assertEquals(ActionStatus.HIDDEN, it.actionStartNextInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStopInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionUnpublishResults)
        }
    }

    @Test
    fun `test build with sequence where we can unpublish results`() {
        tGiven("A sequence where result are already been published") {
            getASequence()
                .also { it.resultsArePublished = true }
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertEquals(ActionStatus.ENABLED, it.actionUnpublishResults)

            assertEquals(ActionStatus.ENABLED, it.actionStartSequence) // Because the sequence is not started
            assertEquals(ActionStatus.HIDDEN, it.actionReopenSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionStopSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionPublishResults)
            assertEquals(ActionStatus.HIDDEN, it.actionStartNextInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStartInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStopInteraction)
        }
    }

    @Test
    fun `test build with sequence in Read and afterstop`() {
        tGiven("A sequence with Read interaction in afterStop state") {
            getASequence()
                .addDefaultInteraction(InteractionType.Read, State.afterStop)
        }.tWhen("We build the command model") {
            CommandModelFactory.build(mockk<User>(), it)
        }.tThen {
            assertEquals(ActionStatus.HIDDEN, it.actionStartInteraction)
            assertEquals(ActionStatus.ENABLED, it.actionStartSequence)
            assertEquals(ActionStatus.ENABLED, it.actionPublishResults)

            assertEquals(ActionStatus.HIDDEN, it.actionUnpublishResults)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionStopSequence)
            assertEquals(ActionStatus.HIDDEN, it.actionStartNextInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionReopenInteraction)
            assertEquals(ActionStatus.HIDDEN, it.actionStopInteraction)
        }
    }

    private fun getASequence(): Sequence = Sequence(
        owner = mockk<User>(),
        statement = Statement(
            owner = mockk<User>(),
            questionType = QuestionType.OpenEnded
        ).alsoSetId(),
    ).alsoSetId()

    /**
     * Add default interactions to the sequence.
     *
     * The active interaction type and its state can be set.
     *
     * @param activeInteractionType The type of the interaction that should be set as active.
     * @param activeInteractionState The state of the active interaction if set.
     */
    private fun Sequence.addDefaultInteraction(
        activeInteractionType: InteractionType? = null,
        activeInteractionState: State = State.beforeStart
    ): Sequence {
        val interactions = listOf(
            Interaction(InteractionType.ResponseSubmission, 1, owner = mockk<User>(), sequence = this),
            Interaction(InteractionType.Evaluation, 2, owner = mockk<User>(), sequence = this),
            Interaction(InteractionType.Read, 3, owner = mockk<User>(), sequence = this),
        )
        this.interactions = interactions
            .associateBy { it.interactionType } as MutableMap<InteractionType, Interaction>
        this.activeInteraction = activeInteractionType
            ?.let { this.interactions[it] }
            ?.also { it.state = activeInteractionState }
        return this
    }

}