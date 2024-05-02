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

package org.elaastic.questions.assignment.sequence.interaction

/**
 * InteractionType is an enum class that represents the type of
 * interaction.
 *
 * @property ResponseSubmission The first phase, where the user submits
 *     their response to the statement.
 * @property Evaluation The second phase, where the user's response is
 *     evaluated by their peers.
 * @property Read The third phase, where the user can the result of the two
 *     previous phases.
 */
enum class InteractionType {
    /**
     * The first phase, where the user submits their response to the statement.
     *
     * @see org.elaastic.questions.assignment.sequence.interaction.specification.ResponseSubmissionSpecification
     */
    ResponseSubmission,

    /**
     * The second phase, where the user's response is evaluated by their peers.
     *
     * @see org.elaastic.questions.assignment.sequence.interaction.specification.EvaluationSpecification
     */
    Evaluation,

    /**
     * The third phase, where the user can the result of the two previous
     * phases.
     *
     * @see org.elaastic.questions.assignment.sequence.interaction.specification.ReadSpecification
     */
    Read
}
