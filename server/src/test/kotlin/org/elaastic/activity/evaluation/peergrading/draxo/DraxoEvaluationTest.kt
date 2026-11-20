/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.activity.evaluation.peergrading.draxo

import org.elaastic.activity.evaluation.peergrading.draxo.criteria.Criteria
import org.elaastic.activity.evaluation.peergrading.draxo.option.OptionId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DraxoEvaluationTest {

    @Test
    fun `a DRAXO criteria cannot be evaluated if the previous ones are have not been evaluated`() {
        assertThrows<IllegalStateException> {
            DraxoEvaluation()
                .addEvaluation(Criteria.R, OptionId.YES)
        }.let { exception ->
            assertThat(exception.message, equalTo("This criteria cannot be evaluated now"))
        }

        assertThrows<IllegalStateException> {
            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.YES)
                .addEvaluation(Criteria.A, OptionId.YES)
        }.let { exception ->
            assertThat(exception.message, equalTo("This criteria cannot be evaluated now"))
        }
    }

    @Test
    fun `It is not possible to evaluate a DRAXO criteria if the previous one has a non-positive evaluation`() {
        assertThrows<IllegalStateException> {
            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.PARTIALLY)
                .addEvaluation(Criteria.R, OptionId.YES)
        }.let { exception ->
            assertThat(exception.message, equalTo("This criteria cannot be evaluated now"))
        }
    }

    @Test
    fun `A DRAXO criteria must be evaluated by an option it its range`() {
        assertThrows<IllegalArgumentException> {
            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.DONT_KNOW)
        }.let { exception ->
            assertThat(exception.message, equalTo("The criteria D does not have the option DONT_KNOW"))
        }
    }

    @Test
    fun `an empty DRAXO evaluation is considered as valid`() {
        assertThat(DraxoEvaluation().isValid(), equalTo(true))
    }

    @Test
    fun `All these DRAXO evaluations are valid`() {
        listOf(
            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.YES),

            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.PARTIALLY),

            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.NO),

            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.YES)
                .addEvaluation(Criteria.R, OptionId.YES),

            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.YES)
                .addEvaluation(Criteria.R, OptionId.DONT_KNOW),

            DraxoEvaluation()
                .addEvaluation(Criteria.D, OptionId.YES)
                .addEvaluation(Criteria.R, OptionId.YES)
                .addEvaluation(Criteria.A, OptionId.YES)
                .addEvaluation(Criteria.X, OptionId.YES)
                .addEvaluation(Criteria.O, OptionId.NO),
        )
            .forEach { evaluation -> assertThat(evaluation.isValid(), equalTo(true)) }
    }

}