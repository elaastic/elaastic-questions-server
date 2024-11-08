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

package org.elaastic.material.instructional.question

import javax.validation.constraints.Max

/**
 * ChoiceItem is a class that represents an item in a choice question.
 *
 * @property index The index of the item in the list of items.
 * @property score The score of the item.
 * @see MultipleChoiceSpecification
 * @see ExclusiveChoiceSpecification
 */
data class ChoiceItem(
        var index: Int,
        @field:Max(100) var score: Float
)
