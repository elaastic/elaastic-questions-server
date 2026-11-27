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

package org.elaastic.ai.evaluation.chatgpt

/**
 * Enum representing the status of a ChatGptEvaluation.
 *
 * @property UNKNOWN The status is unknown.
 * @property PENDING The evaluation is pending.
 * @property DONE The evaluation is done.
 * @property ERROR An error occurred during the evaluation.
 */
enum class ChatGptEvaluationStatus {
    /** The status is unknown. */
    UNKNOWN,

    /** The evaluation is pending. */
    PENDING,

    /** The evaluation is done. */
    DONE,

    /** An error occurred during the evaluation. */
    ERROR
}