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

import org.elaastic.questions.assignment.sequence.TeacherExplanation
import javax.validation.constraints.Max
import javax.validation.constraints.NotEmpty

/**
 * This class is used to store the specification of a multiple choice interaction.
 *
 * @see org.elaastic.questions.assignment.choice.ChoiceSpecification
 */
data class MultipleChoiceSpecification(

    /**
     * The number of candidate items
     */
    @field:Max(10) override var nbCandidateItem: Int,

    /**
     * The expected choice list
     * @see org.elaastic.questions.assignment.choice.ChoiceItem
     */
    @field:NotEmpty var expectedChoiceList: List<ChoiceItem> = listOf(),

    /**
     * The explanation choice list
     * @see org.elaastic.questions.assignment.sequence.TeacherExplanation
     */
    var explanationChoiceList: List<TeacherExplanation> = listOf()
) : ChoiceSpecification {

    override fun getChoiceType(): ChoiceType {
        return ChoiceType.MULTIPLE
    }

    override fun toLegacy(): org.elaastic.material.instructional.question.legacy.ChoiceSpecification {
        return org.elaastic.material.instructional.question.legacy.ChoiceSpecification(
            choiceInteractionType = getChoiceType().name,
            itemCount = nbCandidateItem,
            expectedChoiceList = expectedChoiceList,
            explanationChoiceList = explanationChoiceList
        )

    }
}
