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


/**
 * ResponseSubmissionSpecification is a data class that represents the
 * specification of the first phase of an interaction sequence.
 *
 * @property studentsProvideExplanation A boolean that indicates whether the
 *     students are required to provide an explanation for their response.
 * @property studentsProvideConfidenceDegree A boolean that indicates whether
 *     the students are required to provide a confidence degree for their
 *     response.
 * @see org.elaastic.questions.assignment.sequence.ConfidenceDegree
 */
data class ResponseSubmissionSpecification(
        var studentsProvideExplanation: Boolean,
        var studentsProvideConfidenceDegree: Boolean
) : InteractionSpecification {


    override fun getType(): InteractionType {
        return InteractionType.ResponseSubmission
    }

    fun setType(value: InteractionType) {
        assert(value == InteractionType.ResponseSubmission)
    }
}
