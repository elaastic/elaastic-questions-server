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

package org.elaastic.sequence.phase

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.elaastic.assertInstanceOf
import org.elaastic.assertIsEmpty
import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.LearnerSequenceService
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.sequence.phase.response.LearnerResponsePhase
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.user.User
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.Logger
import org.springframework.context.ApplicationContext
import org.springframework.test.util.ReflectionTestUtils


@ExtendWith(MockKExtension::class)
class LearnerPhaseServiceTest {

    @MockK
    lateinit var learnerSequenceService: LearnerSequenceService

    @MockK
    lateinit var learnerPhaseFactory: LearnerPhaseFactory

    @MockK
    lateinit var ctx: ApplicationContext

    lateinit var learnerPhaseService: LearnerPhaseService

    @BeforeEach
    fun setUp() {
        learnerPhaseService = LearnerPhaseService(learnerSequenceService, learnerPhaseFactory, ctx)
    }

    @Test
    fun `test loadPhaseList without interactions`() {
        val logger = mockk<Logger>()
        every { logger.warn(any()) } answers {callOriginal()}
        ReflectionTestUtils.setField(learnerPhaseService, "logger", logger)
        tGiven("a learner sequence without interactions") {

            mockk<ILearnerSequence> {
                every { learner } returns mockk<User> {
                    every { id } returns 1L
                }
                every { sequence } returns mockk<Sequence> {
                    every { id } returns 1L
                    every { interactions } returns emptyMap<InteractionType, Interaction>().toMutableMap()
                }
            }
        }.tWhen("loadPhaseList is called") { iLearnerSequence ->
            assertIsEmpty(iLearnerSequence.sequence.interactions)
            every {
                learnerSequenceService.getActiveInteractionForLearner(
                    any<User>(),
                    any<Sequence>()
                )
            } returns null
            learnerPhaseService.loadPhaseList(iLearnerSequence)
            iLearnerSequence
        }.tThen("it should load all phases for the sequence") {
            verify(exactly = 0) {
                learnerPhaseFactory.build(any(), any(), any(), any(), any())
            }
            verify(exactly = 1) { logger.warn("This sequence (1), has no interactions defined.") }
        }
    }

    @Test
    fun buildPhase() {
        tGiven("a learner sequence") {
            mockk<ILearnerSequence> {
                every { isNotStarted() } returns true
                every { hasStarted() } returns false
            }
        }.tWhen("buildPhase is called with ResponseSubmission phase type") { sequence ->
            val interactionType = InteractionType.ResponseSubmission
            val phaseIndex = 1
            val active = true

            every {
                learnerPhaseFactory
                    .build(interactionType, sequence, phaseIndex, active, State.beforeStart)
            } answers { callOriginal() }

            learnerPhaseService.buildPhase(sequence, interactionType, phaseIndex, active)
        }.tThen("it should return a LearnerPhase") {
            assertInstanceOf<LearnerResponsePhase>(it)
        }
    }

}