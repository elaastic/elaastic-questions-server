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

package org.elaastic.sequence

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.elaastic.material.instructional.statement.Statement
import org.elaastic.sequence.config.EvaluationSpecification
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.sequence.phase.evaluation.EvaluationMethod
import org.elaastic.sequence.phase.evaluation.EvaluationPhaseConfig
import org.elaastic.sequence.phase.response.ResponsePhaseConfig
import org.elaastic.sequence.phase.result.ResultPhaseConfig
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.elaastic.user.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.togglz.core.Feature
import org.togglz.core.context.FeatureContext
import org.togglz.core.manager.FeatureManager

class SequenceTest {

    @Test
    fun `test saveConfiguration`() {

        // We need to mock FeatureContext to return a FeatureManager that always returns true for isActive
        mockkStatic(FeatureContext::class) {
            every { FeatureContext.getFeatureManager() } returns mockk<FeatureManager> {
                every { isActive(any<Feature>()) } returns true
            }
            val executionContext = ExecutionContext.Distance
            val responsePhaseConfig = ResponsePhaseConfig(true)
            val evaluationPhaseConfig = EvaluationPhaseConfig(true, 3, EvaluationMethod.DRAXO)
            val resultPhaseConfig = ResultPhaseConfig(true)
            val sequence = Sequence(
                owner = mockk<User>(),
                statement = mockk<Statement>(),
            )
            assertFalse(sequence.isConfigured())

            tGiven("a sequence configuration") {
                SequenceConfig(
                    executionContext,
                    responsePhaseConfig,
                    evaluationPhaseConfig,
                    resultPhaseConfig
                )
            }.tWhen("we save the configuration") {
                sequence.saveConfiguration(
                    sequence.owner,
                    it
                )
            }.tThen {
                assertTrue(sequence.isConfigured())
                assertEquals(executionContext, it.executionContext)
                assertEquals(evaluationPhaseConfig.evaluationMethod, it.evaluationMethod)
                assertEquals(resultPhaseConfig.evaluationByIa, it.chatGptEvaluationEnabled)

                // Check for each interaction if its correctly configured
                it.getResponseSubmissionInteraction().let { response ->
                    assertEquals(InteractionType.ResponseSubmission, response.interactionType)
                    assertEquals(
                        responsePhaseConfig.studentGiveExplanation,
                        (response.specification as? ResponseSubmissionSpecification)?.studentsProvideExplanation
                    )
                    assertEquals(
                        responsePhaseConfig.studentGiveExplanation,
                        (response.specification as? ResponseSubmissionSpecification)?.studentsProvideConfidenceDegree
                    )
                }

                it.getEvaluationInteraction().let { evaluation ->
                    assertEquals(InteractionType.Evaluation, evaluation.interactionType)
                    assertEquals(
                        evaluationPhaseConfig.nbResponseToEvaluate,
                        (evaluation.specification as EvaluationSpecification).responseToEvaluateCount
                    )
                }

                assertEquals(InteractionType.Read, it.getReadInteraction().interactionType)
            }
        }
    }

    @Test
    fun `test saveConfiguration but fail requirement`() {
        tGiven {
            mockk<Sequence>("Sequence's owner dont match") {
                every { owner } returns mockk<User>("John")
                every { saveConfiguration(any(), any()) } answers { callOriginal() }
            }
        }.tWhen {
            val user = mockk<User>("George")
            assertNotEquals(user, it.owner);
            { it.saveConfiguration(user, mockk<SequenceConfig>()) }
        }.tThen {
            assertThrows<IllegalArgumentException> {
                it()
            }.apply {
                assertEquals("Only the owner can save the configuration of a sequence", message)
            }
        }

        tGiven {
            mockk<Sequence>("Sequence's state is not beforeStart") {
                every { owner } returns mockk<User>("John")
                every { state } returns State.afterStop
                every { saveConfiguration(any(), any()) } answers { callOriginal() }
            }
        }.tWhen {
            { it.saveConfiguration(it.owner, mockk<SequenceConfig>()) }
        }.tThen {
            assertThrows<IllegalArgumentException> {
                it()
            }.apply {
                assertEquals("The sequence must be in the beforeStart state to save its configuration", message)
            }
        }
    }
}
