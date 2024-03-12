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

package org.elaastic.questions.assignment.choice

import org.elaastic.questions.assignment.sequence.TeacherExplanation
import javax.validation.constraints.Max
import javax.validation.constraints.Min

data class ExclusiveChoiceSpecification(
        @field:Max(10) override var nbCandidateItem: Int,
        var expectedChoice: ChoiceItem,
        var explanationChoiceList: List<TeacherExplanation> = listOf()
) : ChoiceSpecification {

    override fun getChoiceType(): ChoiceType {
        return ChoiceType.EXCLUSIVE
    }

    override fun toLegacy(): org.elaastic.questions.assignment.choice.legacy.ChoiceSpecification {
        return org.elaastic.questions.assignment.choice.legacy.ChoiceSpecification(
                choiceInteractionType = getChoiceType().name,
                itemCount = nbCandidateItem,
                expectedChoiceList = listOf(expectedChoice),
                explanationChoiceList = explanationChoiceList
        )
    }
}
