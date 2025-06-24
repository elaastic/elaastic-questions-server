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

import io.mockk.every
import io.mockk.mockk
import org.elaastic.assertInstanceOf
import org.elaastic.sequence.ILearnerSequence
import org.elaastic.sequence.State
import org.elaastic.sequence.interaction.InteractionType
import org.elaastic.sequence.phase.evaluation.EvaluationMethod
import org.elaastic.sequence.phase.evaluation.all_at_once.AllAtOnceLearnerEvaluationPhase
import org.elaastic.sequence.phase.evaluation.draxo.DraxoLearnerEvaluationPhase
import org.elaastic.sequence.phase.response.LearnerResponsePhase
import org.elaastic.sequence.phase.result.LearnerResultPhase
import org.elaastic.test.directive.tGiven
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.junit.jupiter.api.Test

class LearnerPhaseFactoryTest {
    @Test
    fun `test build with ResponseSubmission interaction`() {
        tGiven("A ResponseSubmission interaction") {
            InteractionType.ResponseSubmission
        }.tWhen("We build a LearnerPhase") {
            LearnerPhaseFactory().build(
                it,
                mockk<ILearnerSequence>(),
                phaseIndex = 1,
                active = true,
                state = State.beforeStart
            )
        }.tThen("it should return a LearnerResponsePhase") {
            assertInstanceOf<LearnerResponsePhase>(it)
        }
    }

    @Test
    fun `test build with Evaluation interaction`() {
        tGiven("An Evaluation interaction") {
            InteractionType.Evaluation
        }.tWhen("We build a LearnerPhase with ALL_AT_ONCE evaluation method") {
            val iLearnerSequence = mockk<ILearnerSequence> {
                // Mock the sequence to return a specific evaluation method
                // This is necessary to test the evaluation phase creation
                every { sequence.evaluationMethod } returns EvaluationMethod.ALL_AT_ONCE
            }

            LearnerPhaseFactory().build(
                it,
                iLearnerSequence,
                phaseIndex = 1,
                active = true,
                state = State.beforeStart
            )
        }.tThen("it should return a LearnerEvaluationPhase") {
            assertInstanceOf<AllAtOnceLearnerEvaluationPhase>(it)
        }

        tGiven("An Evaluation interaction") {
            InteractionType.Evaluation
        }.tWhen("We build a LearnerPhase with DRAXO evaluation method") {
            val iLearnerSequence = mockk<ILearnerSequence> {
                // Mock the sequence to return a specific evaluation method
                // This is necessary to test the evaluation phase creation
                every { sequence.evaluationMethod } returns EvaluationMethod.DRAXO
            }

            LearnerPhaseFactory().build(
                it,
                iLearnerSequence,
                phaseIndex = 1,
                active = true,
                state = State.beforeStart
            )
        }.tThen("it should return a DraxoLearnerEvaluationPhase") {
            assertInstanceOf<DraxoLearnerEvaluationPhase>(it)
        }
    }

    @Test
    fun `test build with Read interaction`() {
        tGiven("A Read interaction") {
            InteractionType.Read
        }.tWhen("We build a LearnerPhase") {
            LearnerPhaseFactory().build(
                it,
                mockk<ILearnerSequence>(),
                phaseIndex = 1,
                active = true,
                state = State.beforeStart
            )
        }.tThen("it should return a LearnerResultPhase") {
            assertInstanceOf<LearnerResultPhase>(it)
        }
    }
}