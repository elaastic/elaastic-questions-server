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

package org.elaastic.questions.player.components.explanationViewer

import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import java.math.BigDecimal

class ResponseData(
        val choices: List<Int> = listOf(),
        val score: Int, // percents
        val correct: Boolean
) {
    constructor(response: Response) : this(
            choices = response.learnerChoice ?: error("The learner choice is undefined"),
            score = ((response.score ?: error("The score is undefined"))).toInt(),
            correct = response.score?.compareTo(BigDecimal(100)) == 0
    )

    override fun equals(other: Any?): Boolean {
        return (other is ResponseData) && choices == other.choices
    }

    override fun hashCode(): Int {
        return choices.hashCode()
    }
}
