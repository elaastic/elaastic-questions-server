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

package org.elaastic.questions.assignment.sequence.interaction.specification

import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import javax.validation.constraints.Max

/**
 * EvaluationSpecification is a class that represents the specification of an evaluation interaction.
 * It contains the number of responses that the user has to evaluate.
 *
 * @property responseToEvaluateCount The number of responses that the user has to evaluate.
 */
data class EvaluationSpecification(

        @field:Max(5)
        var responseToEvaluateCount: Int
) : InteractionSpecification {
    
    override fun getType(): InteractionType {
        return InteractionType.Evaluation
    }

    fun setType(value: InteractionType) {
        assert(value == InteractionType.Evaluation)
    }

}
