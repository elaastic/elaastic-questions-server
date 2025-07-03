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

package org.elaastic.sequence.interaction

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.elaastic.assertInstanceOf
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.SequenceConfig
import org.elaastic.sequence.State
import org.elaastic.sequence.config.EvaluationSpecification
import org.elaastic.sequence.config.ReadSpecification
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.sequence.phase.evaluation.EvaluationMethod
import org.elaastic.sequence.phase.evaluation.EvaluationPhaseConfig
import org.elaastic.sequence.phase.response.ResponsePhaseConfig
import org.elaastic.sequence.phase.result.ResultPhaseConfig
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.user.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.togglz.core.Feature
import org.togglz.core.context.FeatureContext
import org.togglz.core.manager.FeatureManager

class InteractionTest {

    @Test
    fun `test getStateForTeacher`() {
        tGiven("an interaction with a sequence in distance execution context") {
            mockk<Interaction> {
                every { getStateForTeacher() } answers { callOriginal() }
                every { sequence } returns mockk<Sequence> {
                    every { executionContext } returns ExecutionContext.Distance
                }
            }
        }.tWhen("getStateForTeacher is called") { it.getStateForTeacher() }
            .tThen { assertEquals(State.afterStop, it) }

        tGiven("an interaction with a sequence in Blended execution context and not in Read interaction type") {
            mockk<Interaction> {
                every { getStateForTeacher() } answers { callOriginal() }
                every { interactionType } returns InteractionType.ResponseSubmission
                every { sequence } returns mockk<Sequence> {
                    every { executionContext } returns ExecutionContext.Distance
                }
            }
        }.tWhen("getStateForTeacher is called") { it.getStateForTeacher() }
            .tThen { assertEquals(State.afterStop, it) }

        State.values().forEach { stateValue ->
            tGiven("an interaction with a sequence in Blended execution context and in Read interaction type") {
                mockk<Interaction> {
                    every { getStateForTeacher() } answers { callOriginal() }
                    every { interactionType } returns InteractionType.Read
                    every { sequence } returns mockk<Sequence> {
                        every { executionContext } returns ExecutionContext.Blended
                    }
                    every { state } returns stateValue
                }
            }.tWhen("getStateForTeacher is called") { it.getStateForTeacher() }
                .tThen { assertEquals(stateValue, it) }

            tGiven("an interaction with a sequence in FaceToFace execution context") {
                mockk<Interaction> {
                    every { getStateForTeacher() } answers { callOriginal() }
                    every { sequence } returns mockk<Sequence> {
                        every { executionContext } returns ExecutionContext.FaceToFace
                    }
                    every { state } returns stateValue
                }.tWhen("getStateForTeacher is called") { it.getStateForTeacher() }
                    .tThen { assertEquals(stateValue, it) }
            }
        }
    }

    @Test
    fun `test for stateForRegisteredUsers`() {
        fun Interaction.assertStateEqualsTo(expectedState: State) =
            tWhen("stateForRegisteredUsers is called") { it.stateForRegisteredUsers() }
                .tThen("should return ${expectedState.name}") { assertEquals(expectedState, it) }

        // isRead() && sequence.resultsArePublished -> State.show
        tGiven("an interaction with a sequence in Distance, results published ad in Read interaction type") {
            mockk<Interaction> {
                every { stateForRegisteredUsers() } answers { callOriginal() }
                every { isRead() } returns true
                every { sequence } returns mockk<Sequence> {
                    every { resultsArePublished } returns true
                }
            }
        }.assertStateEqualsTo(State.show)

        /*
         * sequence.isStopped() ->
         *    [...]
         *    else State.beforeStart
         */
        tGiven("") {
            mockk<Interaction> {
                every { stateForRegisteredUsers() } answers { callOriginal() }
                every { isRead() } returns false
                every { rank } returns 1
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns true
                    every { activeInteraction } returns mockk<Interaction> {
                        every { rank } returns 0
                    }
                }
            }
        }.assertStateEqualsTo(State.beforeStart)

        // isRead() && sequence.executionIsDistance() -> State.afterStop
        tGiven("") {
            mockk<Interaction> {
                every { stateForRegisteredUsers() } answers { callOriginal() }
                every { isRead() } returns true
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns false
                    every { executionIsFaceToFace() } returns false
                    every { executionIsBlended() } returns false
                    every { executionIsDistance() } returns true
                    every { resultsArePublished } returns false
                }
            }
        }.assertStateEqualsTo(State.afterStop)

        // else -> State.show
        tGiven("") {
            mockk<Interaction> {
                every { stateForRegisteredUsers() } answers { callOriginal() }
                every { isRead() } returns false
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns false
                    every { executionIsFaceToFace() } returns false
                    every { executionIsBlended() } returns false
                    every { executionIsDistance() } returns false
                    every { executionContext } returns ExecutionContext.Distance
                }
            }
        }.assertStateEqualsTo(State.show)

        State.values().forEach { stateValue ->
            /*
             * sequence.isStopped() ->
             *     if (rank <= (sequence.activeInteraction?.rank ?: 0))
             *         state
             */
            tGiven("") {
                mockk<Interaction> {
                    every { stateForRegisteredUsers() } answers { callOriginal() }
                    every { isRead() } returns false
                    every { rank } returns 1
                    every { sequence } returns mockk<Sequence> {
                        every { isStopped() } returns true
                        every { activeInteraction } returns mockk<Interaction> {
                            every { rank } returns 1
                        }
                    }
                    every { state } returns stateValue
                }
            }.assertStateEqualsTo(stateValue)

            // sequence.executionIsFaceToFace() -> state
            tGiven("an interaction with a sequence in face to face execution context and results are published") {
                mockk<Interaction> {
                    every { stateForRegisteredUsers() } answers { callOriginal() }
                    every { isRead() } returns false
                    every { sequence } returns mockk<Sequence> {
                        every { isStopped() } returns false
                        every { executionIsFaceToFace() } returns true
                    }
                    every { state } returns stateValue
                }
            }.assertStateEqualsTo(stateValue)

            // isRead() && sequence.executionIsBlended() -> state
            tGiven("") {
                mockk<Interaction> {
                    every { stateForRegisteredUsers() } answers { callOriginal() }
                    every { isRead() } returns true
                    every { sequence } returns mockk<Sequence> {
                        every { isStopped() } returns false
                        every { executionIsFaceToFace() } returns false
                        every { executionIsBlended() } returns true
                        every { resultsArePublished } returns false
                    }
                    every { state } returns stateValue
                }
            }.assertStateEqualsTo(stateValue)
        }
    }

    @Test
    fun `test for stateForLearner`() {
        fun State.assertStateEqualsTo(expectedState: State) = tThen { assertEquals(expectedState, this) }
        fun Interaction.assertStateForLearnerEqualsTo(
            expectedState: State,
            interaction: Interaction = mockk<Interaction>()
        ) = stateForLearner(interaction).assertStateEqualsTo(expectedState)

        /*
         * sequence.isStopped() -> when {
         *     isRead() && sequence.resultsArePublished -> State.show
         *     rank <= sequence.activeInteraction!!.rank -> State.afterStop
         *     else -> State.beforeStart
         * }
         */
        tGiven {
            mockk<Interaction> {
                every { stateForLearner(any()) } answers { callOriginal() }
                every { isRead() } returns true
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns true
                    every { resultsArePublished } returns true
                }
            }
        }.assertStateForLearnerEqualsTo(State.show)

        tGiven {
            mockk<Interaction> {
                every { stateForLearner(any()) } answers { callOriginal() }
                every { isRead() } returns false
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns true
                    every { activeInteraction } returns mockk<Interaction> {
                        every { rank } returns 3
                    }
                }
                every { rank } returns 1
            }
        }.assertStateForLearnerEqualsTo(State.afterStop)

        tGiven {
            mockk<Interaction> {
                every { stateForLearner(any()) } answers { callOriginal() }
                every { isRead() } returns false
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns true
                    every { activeInteraction } returns mockk<Interaction> {
                        every { rank } returns 0
                    }
                }
                every { rank } returns 3
            }
        }.assertStateForLearnerEqualsTo(State.beforeStart)

        /*
         * !sequence.executionIsFaceToFace() && !sequence.isStopped() ->
         *     if (this == learnerActiveInteraction)
         *         when {
         *             isRead() && sequence.resultsArePublished -> State.show
         *             [...]
         *             else -> State.show
         *         }
         */
        tGiven {
            mockk<Interaction> {
                every { stateForLearner(any()) } answers { callOriginal() }
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns false
                    every { executionIsFaceToFace() } returns false
                    every { resultsArePublished } returns true
                }
                every { isRead() } returns true
            }
        }.let { it.assertStateForLearnerEqualsTo(State.show, it) }

        tGiven {
            mockk<Interaction> {
                every { stateForLearner(any()) } answers { callOriginal() }
                every { isRead() } returns false
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns false
                    every { executionIsFaceToFace() } returns false
                }
            }
        }.let { it.assertStateForLearnerEqualsTo(State.show, it) }


        /*
         *     else State.afterStop
         */
        tGiven {
            mockk<Interaction> {
                every { stateForLearner(any()) } answers { callOriginal() }
                every { sequence } returns mockk<Sequence> {
                    every { isStopped() } returns false
                    every { executionIsFaceToFace() } returns false
                    every { executionIsBlended() } returns true
                    every { resultsArePublished } returns false
                }
                every { isRead() } returns true
                every { state } returns State.afterStop
            }
        }.assertStateForLearnerEqualsTo(State.afterStop)

        State.values().forEach { stateValue ->
            /*
             * isRead() && sequence.executionIsBlended() -> state
             */
            tGiven {
                mockk<Interaction> {
                    every { stateForLearner(any()) } answers { callOriginal() }
                    every { sequence } returns mockk<Sequence> {
                        every { isStopped() } returns false
                        every { executionIsFaceToFace() } returns false
                        every { executionIsBlended() } returns true
                        every { resultsArePublished } returns false
                    }
                    every { isRead() } returns true
                    every { state } returns stateValue
                }
            }.let { it.assertStateForLearnerEqualsTo(stateValue, it) }

            /*
             * else -> state
             */
            tGiven {
                mockk<Interaction> {
                    every { stateForLearner(any()) } answers { callOriginal() }
                    every { isRead() } returns false
                    every { sequence } returns mockk<Sequence> {
                        every { isStopped() } returns false
                        every { executionIsFaceToFace() } returns true
                    }
                    every { state } returns stateValue
                }
            }.assertStateForLearnerEqualsTo(stateValue)
        }
    }

    @Test
    fun `test constructor with InteractionSpecification`() {
        tGiven("an interaction created with ResponseSubmissionSpecification") {
            Interaction(
                sequence = Sequence(mockk<User>(), mockk<Statement>()),
                interactionSpecification = ResponseSubmissionSpecification(
                    studentsProvideExplanation = true,
                    studentsProvideConfidenceDegree = false
                ),
                rank = 1
            )
        }.tThen("should create an interaction with the correct type and specification") {
            assertEquals(InteractionType.ResponseSubmission, it.interactionType)
            assertInstanceOf<ResponseSubmissionSpecification>(it.specification!!)
            assertEquals(1, it.rank)
            assertEquals(State.beforeStart, it.state)
            val specification = it.specification as ResponseSubmissionSpecification
            assertEquals(true, specification.studentsProvideExplanation)
            assertEquals(false, specification.studentsProvideConfidenceDegree)
        }
    }

    @Test
    fun `test createInteractions with SequenceConfig`() {
        val sequence = Sequence(mockk<User>(), mockk<Statement>())

        // We need to mock FeatureContext to return a FeatureManager that always returns true for isActive
        mockkStatic(FeatureContext::class) {
            every { FeatureContext.getFeatureManager() } returns mockk<FeatureManager> {
                every { isActive(any<Feature>()) } returns true
            }

            tGiven {
                val sequenceConfig = SequenceConfig(
                    ExecutionContext.Distance,
                    ResponsePhaseConfig(true),
                    EvaluationPhaseConfig(true, 2, EvaluationMethod.DRAXO),
                    ResultPhaseConfig(true)
                )
                Interaction.createInteractions(sequence, sequenceConfig)
            }.tThen {
                assertEquals(3, it.size)
                assertInstanceOf<ResponseSubmissionSpecification>(it[0].specification!!)
                assertInstanceOf<EvaluationSpecification>(it[1].specification!!)
                assertInstanceOf<ReadSpecification>(it[2].specification!!)

                // Check the interaction types
                assertEquals(InteractionType.ResponseSubmission, it[0].interactionType)
                assertEquals(InteractionType.Evaluation, it[1].interactionType)
                assertEquals(InteractionType.Read, it[2].interactionType)

                // Check the ranks
                assertEquals(1, it[0].rank)
                assertEquals(2, it[1].rank)
                assertEquals(3, it[2].rank)

                it[0].let { response ->
                    assertInstanceOf<ResponseSubmissionSpecification>(response.specification!!)
                    val responseSpec = response.specification as ResponseSubmissionSpecification
                    assertEquals(true, responseSpec.studentsProvideExplanation)
                    assertEquals(true, responseSpec.studentsProvideConfidenceDegree)
                }

                it[1].let { evaluation ->
                    assertInstanceOf<EvaluationSpecification>(evaluation.specification!!)
                    val evalSpec = evaluation.specification as EvaluationSpecification
                    assertEquals(2, evalSpec.responseToEvaluateCount)
                }

                it.all { interaction ->
                    interaction.state == State.beforeStart &&
                            interaction.owner == sequence.owner &&
                            interaction.sequence == sequence
                }
            }
        }
    }

    @Test
    fun `test createInteractions with SequenceConfig but ConfrontingView isn't active`() {
        val sequence = Sequence(mockk<User>(), mockk<Statement>())

        // We need to mock FeatureContext to return a FeatureManager that always returns true for isActive
        mockkStatic(FeatureContext::class) {
            every { FeatureContext.getFeatureManager() } returns mockk<FeatureManager> {
                every { isActive(any<Feature>()) } returns true
            }

            tGiven {
                val sequenceConfig = SequenceConfig(
                    ExecutionContext.Distance,
                    ResponsePhaseConfig(true),
                    EvaluationPhaseConfig(false),
                    ResultPhaseConfig(true)
                )
                Interaction.createInteractions(sequence, sequenceConfig)
            }.tThen {
                assertEquals(2, it.size)
                assertInstanceOf<ResponseSubmissionSpecification>(it[0].specification!!)
                assertInstanceOf<ReadSpecification>(it[1].specification!!)

                // Check the interaction types
                assertEquals(InteractionType.ResponseSubmission, it[0].interactionType)
                assertEquals(InteractionType.Read, it[1].interactionType)

                // Check the ranks
                assertEquals(1, it[0].rank)
                assertEquals(2, it[1].rank)

                it[0].let { response ->
                    assertInstanceOf<ResponseSubmissionSpecification>(response.specification!!)
                    val responseSpec = response.specification as ResponseSubmissionSpecification
                    assertEquals(true, responseSpec.studentsProvideExplanation)
                    assertEquals(true, responseSpec.studentsProvideConfidenceDegree)
                }

                it.all { interaction ->
                    interaction.state == State.beforeStart &&
                            interaction.owner == sequence.owner &&
                            interaction.sequence == sequence
                }
            }
        }
    }
}