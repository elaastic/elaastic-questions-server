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

package org.elaastic.player.steps

import io.mockk.every
import io.mockk.mockk
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State.*
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StepsModelFactoryTest {
    @Test
    fun `test buildForTeacher with no interactions`() {
        tGiven("A sequence with no interactions") {
            mockk<Sequence> {
                every { getResponseSubmissionInteractionOrNull() } returns null
                every { getEvaluationInteractionOrNull() } returns null
                every { getReadInteractionOrNull() } returns null
                every { phase2Skipped } returns false
            }
        }.tWhen("buildForTeacher is called") {
            StepsModelFactory.buildForTeacher(it)
        }.tThen {
            assertNotNull(it)
            assertNull(it.responseSubmission)
            assertNull(it.evaluation)
            assertNull(it.read)
            assertTrue(it.showStatistics)
            assertFalse(it.phase2Skipped)
        }
    }

    @Test
    fun `test buildForTeacher with interactions`() {
        tGiven("A sequence with interactions") {
            mockk<Sequence> {
                every { getResponseSubmissionInteractionOrNull() } returns mockk {
                    every { stateForRegisteredUsers() } returns show
                    every { rank } returns 1
                }
                every { getEvaluationInteractionOrNull() } returns mockk {
                    every { stateForRegisteredUsers() } returns beforeStart
                    every { rank } returns 2
                }
                every { getReadInteractionOrNull() } returns mockk {
                    every { stateForRegisteredUsers() } returns afterStop
                    every { rank } returns 3
                }
                every { phase2Skipped } returns false
            }
        }.tWhen("buildForTeacher is called") {
            StepsModelFactory.buildForTeacher(it)
        }.tThen {
            assertNotNull(it)
            assertNotNull(it.responseSubmission)
            assertTrue(it.responseSubmission is PhaseStep)
            assertEquals(StepsModel.PhaseState.ACTIVE, it.responseSubmission?.state)
            assertEquals(1, it.responseSubmission?.rank)

            assertNotNull(it.evaluation)
            assertTrue(it.evaluation is PhaseStep)
            assertEquals(StepsModel.PhaseState.DISABLED, it.evaluation?.state)
            assertEquals(2, it.evaluation?.rank)

            assertNotNull(it.read)
            assertTrue(it.read is PhaseStep)
            assertEquals(StepsModel.PhaseState.COMPLETED, it.read?.state)
            assertEquals(3, it.read?.rank)

            assertTrue(it.showStatistics)
            assertFalse(it.phase2Skipped)
        }
    }


    @Test
    fun `test buildForLearner with interactions`() {
        lateinit var learnerActiveInteraction: Interaction
        lateinit var sequence: Sequence
        tGiven("A sequence with interactions and a learner active interaction") {
            learnerActiveInteraction = mockk<Interaction>()
            sequence = mockk<Sequence> {
                every { getResponseSubmissionInteractionOrNull() } returns mockk {
                    every { stateForLearner(learnerActiveInteraction) } returns show
                    every { rank } returns 1
                }
                every { getEvaluationInteractionOrNull() } returns mockk {
                    every { stateForLearner(learnerActiveInteraction) } returns beforeStart
                    every { rank } returns 2
                }
                every { getReadInteractionOrNull() } returns mockk {
                    every { stateForLearner(learnerActiveInteraction) } returns afterStop
                    every { rank } returns 3
                }
                every { phase2Skipped } returns true
            }
        }.tWhen("buildForLearner is called") {
            StepsModelFactory.buildForLearner(sequence, learnerActiveInteraction)
        }.tThen {
            assertNotNull(it)
            assertNotNull(it.responseSubmission)
            assertTrue(it.responseSubmission is PhaseStep)
            assertEquals(StepsModel.PhaseState.ACTIVE, it.responseSubmission?.state)
            assertEquals(1, it.responseSubmission?.rank)

            assertNotNull(it.evaluation)
            assertTrue(it.evaluation is PhaseStep)
            assertEquals(StepsModel.PhaseState.DISABLED, it.evaluation?.state)
            assertEquals(2, it.evaluation?.rank)

            assertNotNull(it.read)
            assertTrue(it.read is PhaseStep)
            assertEquals(StepsModel.PhaseState.COMPLETED, it.read?.state)
            assertEquals(3, it.read?.rank)

            assertFalse(it.showStatistics)
            assertTrue(it.phase2Skipped)
        }
    }

    @Test
    fun `test buildForLearner with no interactions`() {
        lateinit var learnerActiveInteraction: Interaction
        lateinit var sequence: Sequence
        tGiven("A sequence with no interactions") {
            learnerActiveInteraction = mockk<Interaction>()
            sequence = mockk<Sequence> {
                every { getResponseSubmissionInteractionOrNull() } returns null
                every { getEvaluationInteractionOrNull() } returns null
                every { getReadInteractionOrNull() } returns null
                every { phase2Skipped } returns false
            }
        }.tWhen("buildForLearner is called") {
            StepsModelFactory.buildForLearner(sequence, learnerActiveInteraction)
        }.tThen {
            assertNotNull(it)
            assertNull(it.responseSubmission)
            assertNull(it.evaluation)
            assertNull(it.read)
            assertFalse(it.showStatistics)
            assertFalse(it.phase2Skipped)
        }
    }

}