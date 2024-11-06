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

package org.elaastic.questions.assignment

/**
 * Different types of questions.
 *
 * @property ExclusiveChoice A question with only one correct answer.
 * @property MultipleChoice A question with multiple correct answers.
 * @property OpenEnded A question with an open answer.
 */
enum class QuestionType {
    /**
     * A question with only one correct answer.
     *
     * There i only one correct answer among the alternatives.
     */
    ExclusiveChoice,

    /**
     * A question with multiple correct answers.
     *
     * There are multiple correct answers among the alternatives.
     */
    MultipleChoice,

    /**
     * A question with an open answer.
     *
     * The answer is open and can be anything.
     */
    OpenEnded
}
