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
import org.elaastic.sequence.ExecutionContext
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
}